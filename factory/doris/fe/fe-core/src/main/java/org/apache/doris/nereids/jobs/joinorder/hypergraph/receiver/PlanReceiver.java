// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.apache.doris.nereids.jobs.joinorder.hypergraph.receiver;

import org.apache.doris.nereids.jobs.joinorder.hypergraph.Edge;
import org.apache.doris.nereids.jobs.joinorder.hypergraph.bitmap.Bitmap;
import org.apache.doris.nereids.memo.Group;
import org.apache.doris.nereids.memo.GroupExpression;
import org.apache.doris.nereids.properties.PhysicalProperties;
import org.apache.doris.nereids.stats.StatsCalculator;
import org.apache.doris.nereids.trees.plans.Plan;
import org.apache.doris.nereids.trees.plans.logical.LogicalJoin;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.BitSet;
import java.util.HashMap;

/**
 * The Receiver is used for cached the plan that has been emitted and build the new plan
 */
public class PlanReceiver implements AbstractReceiver {
    // limit define the max number of csg-cmp pair in this Receiver
    HashMap<BitSet, Group> planTable = new HashMap<>();
    int limit;
    int emitCount = 0;

    public PlanReceiver() {
        limit = Integer.MAX_VALUE;
    }

    public PlanReceiver(int limit) {
        this.limit = limit;
    }

    /**
     * Emit a new plan from bottom to top
     *
     * @param left the bitmap of left child tree
     * @param right the bitmap of the right child tree
     * @param edge the join operator
     * @return the left and the right can be connected by the edge
     */
    @Override
    public boolean emitCsgCmp(BitSet left, BitSet right, Edge edge) {
        Preconditions.checkArgument(planTable.containsKey(left));
        Preconditions.checkArgument(planTable.containsKey(right));
        emitCount += 1;
        if (emitCount > limit) {
            return false;
        }
        BitSet fullKey = Bitmap.newBitmapUnion(left, right);
        Group group1 = constructGroup(left, right, edge);
        Group group2 = constructGroup(right, left, edge);
        Group winnerGroup;
        if (group1.getLogicalExpression().getCostByProperties(PhysicalProperties.ANY) < group2.getLogicalExpression()
                .getCostByProperties(PhysicalProperties.ANY)) {
            winnerGroup = group1;
        } else {
            winnerGroup = group2;
        }

        if (!planTable.containsKey(fullKey)
                || planTable.get(fullKey).getLogicalExpression().getCostByProperties(PhysicalProperties.ANY)
                > winnerGroup.getLogicalExpression().getCostByProperties(PhysicalProperties.ANY)) {
            planTable.put(fullKey, winnerGroup);
        }
        return true;
    }

    @Override
    public void addGroup(BitSet bitSet, Group group) {
        planTable.put(bitSet, group);
    }

    @Override
    public boolean contain(BitSet bitSet) {
        return planTable.containsKey(bitSet);
    }

    @Override
    public void reset() {
        planTable.clear();
        emitCount = 0;
    }

    @Override
    public Group getBestPlan(BitSet bitSet) {
        Preconditions.checkArgument(planTable.containsKey(bitSet));
        return planTable.get(bitSet);
    }

    private double getSimpleCost(Plan plan) {
        if (!(plan instanceof LogicalJoin)) {
            return plan.getGroupExpression().get().getOwnerGroup().getStatistics().getRowCount();
        }
        return plan.getGroupExpression().get().getCostByProperties(PhysicalProperties.ANY);
    }

    private Group constructGroup(BitSet left, BitSet right, Edge edge) {
        Preconditions.checkArgument(planTable.containsKey(left));
        Preconditions.checkArgument(planTable.containsKey(right));
        Group leftGroup = planTable.get(left);
        Group rightGroup = planTable.get(right);
        Plan leftPlan = leftGroup.getLogicalExpression().getPlan();
        Plan rightPlan = rightGroup.getLogicalExpression().getPlan();

        double cost = getSimpleCost(leftPlan) + getSimpleCost(rightPlan);
        LogicalJoin newJoin = new LogicalJoin(edge.getJoin().getJoinType(), edge.getJoin().getExpressions(),
                leftGroup.getLogicalExpression().getPlan(),
                rightGroup.getLogicalExpression().getPlan());

        GroupExpression groupExpression = new GroupExpression(newJoin, Lists.newArrayList(leftGroup, rightGroup));
        Group group = new Group();
        group.addGroupExpression(groupExpression);
        StatsCalculator.estimate(groupExpression);
        cost += group.getStatistics().getRowCount();

        groupExpression.updateLowestCostTable(PhysicalProperties.ANY,
                Lists.newArrayList(PhysicalProperties.ANY, PhysicalProperties.ANY), cost);
        return group;
    }
}

