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

package org.apache.kylin.rest.request;

public class JobBuildRequest {

    private long startTime;

    private long endTime;

    private String buildType;

    private boolean force;

    private boolean forceMergeEmptySegment = false;
    
    private Integer priorityOffset = 0;
    
    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getBuildType() {
        return buildType;
    }

    public void setBuildType(String buildType) {
        this.buildType = buildType;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public boolean isForceMergeEmptySegment() {
        return forceMergeEmptySegment;
    }

    public void setForceMergeEmptySegment(boolean forceMergeEmptySegment) {
        this.forceMergeEmptySegment = forceMergeEmptySegment;
    }

    public Integer getPriorityOffset() {
        return priorityOffset;
    }

    public void setPriorityOffset(Integer priorityOffset) {
        this.priorityOffset = priorityOffset;
    }
}
