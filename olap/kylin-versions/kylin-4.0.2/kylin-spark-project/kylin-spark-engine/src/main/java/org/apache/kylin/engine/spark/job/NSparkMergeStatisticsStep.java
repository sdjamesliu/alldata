/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.kylin.engine.spark.job;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.kylin.common.KylinConfig;
import org.apache.kylin.common.persistence.ResourceStore;
import org.apache.kylin.common.util.ByteArray;
import org.apache.kylin.common.util.Bytes;
import org.apache.kylin.common.util.HadoopUtil;
import org.apache.kylin.cube.CubeInstance;
import org.apache.kylin.cube.CubeManager;
import org.apache.kylin.cube.CubeSegment;
import org.apache.kylin.engine.mr.common.BatchConstants;
import org.apache.kylin.engine.mr.common.CubeStatsWriter;
import org.apache.kylin.job.constant.ExecutableConstants;
import org.apache.kylin.job.exception.ExecuteException;
import org.apache.kylin.job.execution.ExecutableContext;
import org.apache.kylin.job.execution.ExecuteResult;
import org.apache.kylin.measure.hllc.HLLCounter;
import org.apache.kylin.metadata.MetadataConstants;
import org.apache.kylin.shaded.com.google.common.collect.Lists;
import org.apache.kylin.shaded.com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class NSparkMergeStatisticsStep extends NSparkExecutable {
    private static final Logger logger = LoggerFactory.getLogger(NSparkMergeStatisticsStep.class);

    private List<CubeSegment> mergingSegments = Lists.newArrayList();
    protected Map<Long, HLLCounter> cuboidHLLMap = Maps.newHashMap();

    public NSparkMergeStatisticsStep() {
        this.setName(ExecutableConstants.STEP_NAME_MERGE_STATISTICS);
    }

    @Override
    protected ExecuteResult doWork(ExecutableContext context) throws ExecuteException {
        String jobId = getParam(MetadataConstants.P_JOB_ID);
        String cubeId = getParam(MetadataConstants.P_CUBE_ID);

        String mergedSegmentUuid = getParam(MetadataConstants.P_SEGMENT_IDS);
        final KylinConfig kylinConfig = wrapConfig(context);
        CubeInstance cube = CubeManager.getInstance(kylinConfig).getCubeByUuid(cubeId);
        CubeSegment mergedSeg = cube.getSegmentById(mergedSegmentUuid);

        String jobTmpDir = kylinConfig.getJobTmpDir(cube.getProject()) + "/" + jobId;
        Path statisticsDir = new Path(jobTmpDir + "/" + ResourceStore.CUBE_STATISTICS_ROOT + "/"
                + cubeId + "/" + mergedSeg.getUuid() + "/");

        mergingSegments = cube.getMergingSegments(mergedSeg);

        Configuration conf = HadoopUtil.getCurrentConfiguration();
        ResourceStore rs = ResourceStore.getStore(kylinConfig);
        try {
            int averageSamplingPercentage = 0;
            long sourceRecordCount = 0;
            for (CubeSegment segment : mergingSegments) {
                String segmentId = segment.getUuid();
                String fileKey = CubeSegment
                        .getStatisticsResourcePath(cube.getName(), segmentId);
                InputStream is = rs.getResource(fileKey).content();
                File tempFile = null;
                FileOutputStream tempFileStream = null;
                try {
                    tempFile = File.createTempFile(segmentId, ".seq");
                    tempFileStream = new FileOutputStream(tempFile);
                    org.apache.commons.io.IOUtils.copy(is, tempFileStream);
                } finally {
                    IOUtils.closeStream(is);
                    IOUtils.closeStream(tempFileStream);
                }

                FileSystem fs = HadoopUtil.getFileSystem("file:///" + tempFile.getAbsolutePath());
                SequenceFile.Reader reader = null;
                try {
                    reader = new SequenceFile.Reader(fs, new Path(tempFile.getAbsolutePath()), conf);
                    LongWritable key = (LongWritable) ReflectionUtils.newInstance(reader.getKeyClass(), conf);
                    BytesWritable value = (BytesWritable) ReflectionUtils.newInstance(reader.getValueClass(), conf);
                    while (reader.next(key, value)) {
                        if (key.get() == 0L) {
                            // sampling percentage;
                            averageSamplingPercentage += Bytes.toInt(value.getBytes());
                        } else if (key.get() == -3) {
                            long perSourceRecordCount = Bytes.toLong(value.getBytes());
                            if (perSourceRecordCount > 0) {
                                sourceRecordCount += perSourceRecordCount;
                            }
                        } else if (key.get() > 0) {
                            HLLCounter hll = new HLLCounter(kylinConfig.getCubeStatsHLLPrecision());
                            ByteArray byteArray = new ByteArray(value.getBytes());
                            hll.readRegisters(byteArray.asBuffer());

                            if (cuboidHLLMap.get(key.get()) != null) {
                                cuboidHLLMap.get(key.get()).merge(hll);
                            } else {
                                cuboidHLLMap.put(key.get(), hll);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                } finally {
                    IOUtils.closeStream(reader);
                    if (tempFile != null)
                        tempFile.delete();
                }
            }
            averageSamplingPercentage = averageSamplingPercentage / mergingSegments.size();
            CubeStatsWriter.writeCuboidStatistics(conf, statisticsDir, cuboidHLLMap,
                    averageSamplingPercentage, sourceRecordCount);
            Path statisticsFilePath = new Path(statisticsDir, BatchConstants.CFG_STATISTICS_CUBOID_ESTIMATION_FILENAME);
            FileSystem fs = HadoopUtil.getFileSystem(statisticsFilePath, conf);
            FSDataInputStream is = fs.open(statisticsFilePath);
            try {
                // put the statistics to metadata store
                String statisticsFileName = mergedSeg.getStatisticsResourcePath();
                rs.putResource(statisticsFileName, is, System.currentTimeMillis());
            } finally {
                IOUtils.closeStream(is);
            }

            return ExecuteResult.createSucceed();
        } catch (IOException e) {
            logger.error("fail to merge cuboid statistics", e);
            return ExecuteResult.createError(e);
        }
    }

}
