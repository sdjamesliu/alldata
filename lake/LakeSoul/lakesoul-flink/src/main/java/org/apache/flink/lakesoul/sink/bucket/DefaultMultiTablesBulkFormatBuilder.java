/*
 *
 *  * Copyright [2022] [DMetaSoul Team]
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.apache.flink.lakesoul.sink.bucket;

import org.apache.flink.api.connector.sink.Sink;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.Path;
import org.apache.flink.lakesoul.sink.writer.AbstractLakeSoulMultiTableSinkWriter;
import org.apache.flink.lakesoul.sink.writer.LakeSoulMultiTableSinkWriter;
import org.apache.flink.lakesoul.types.BinarySourceRecord;

public class DefaultMultiTablesBulkFormatBuilder
        extends BulkFormatBuilder<BinarySourceRecord, DefaultMultiTablesBulkFormatBuilder> {

    public DefaultMultiTablesBulkFormatBuilder(Path basePath, Configuration conf) {
        super(basePath, conf);
    }

    @Override
    public AbstractLakeSoulMultiTableSinkWriter<BinarySourceRecord> createWriter(Sink.InitContext context, int subTaskId) {
        return new LakeSoulMultiTableSinkWriter(
                subTaskId,
                context.metricGroup(),
                super.bucketFactory,
                super.rollingPolicy,
                super.outputFileConfig,
                context.getProcessingTimeService(),
                super.bucketCheckInterval,
                super.conf
        );
    }
}
