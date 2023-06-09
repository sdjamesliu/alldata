// This file is licensed under the Elastic License 2.0. Copyright 2021-present, StarRocks Inc.

package com.starrocks.sql.plan;

import com.google.common.collect.Lists;
import com.starrocks.common.FeConstants;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

class ColocateJoinTest extends PlanTestBase {

    @BeforeAll
    public static void beforeClass() throws Exception {
        PlanTestBase.beforeClass();
        FeConstants.runningUnitTest = true;
        starRocksAssert.withTable("CREATE TABLE `colocate_t2_1` (\n" +
                "  `v7` bigint NULL COMMENT \"\",\n" +
                "  `v8` bigint NULL COMMENT \"\",\n" +
                "  `v9` bigint NULL\n" +
                ") ENGINE=OLAP\n" +
                "DUPLICATE KEY(`v7`, `v8`, v9)\n" +
                "DISTRIBUTED BY HASH(`v7`) BUCKETS 3\n" +
                "PROPERTIES (\n" +
                "\"replication_num\" = \"1\",\n" +
                "\"in_memory\" = \"false\",\n" +
                "\"storage_format\" = \"DEFAULT\",\n" +
                "\"colocate_with\" = \"colocate_group_1\"" +
                ");");
    }

    @ParameterizedTest(name = "sql_{index}: {0}.")
    @MethodSource("colocateJoinOnceSqls")
    void testColocateJoinOnce(String sql) throws Exception {
        String plan = getFragmentPlan(sql);
        int count = StringUtils.countMatches(plan, "INNER JOIN (COLOCATE)");
        Assert.assertEquals(plan, 1, count);
    }

    @ParameterizedTest(name = "sql_{index}: {0}.")
    @MethodSource("colocateJoinTwiceSqls")
    void testColocateJoinTwice(String sql) throws Exception {
        String plan = getFragmentPlan(sql);
        int count = StringUtils.countMatches(plan, "INNER JOIN (COLOCATE)");
        Assert.assertEquals(plan, 2, count);
    }


    private static Stream<Arguments> colocateJoinOnceSqls() {
        List<String> sqls = Lists.newArrayList();

        // sqls should colocate join but not support now
        List<String> unsupportedSqls = Lists.newArrayList();
        sqls.add("select * from colocate_t0 join colocate_t1 on v1 = v5 and v1 = v4");
        sqls.add("select * from colocate_t0 join colocate_t1 on v2 = v4 and v1 = v4");
        sqls.add("select * from colocate_t0 join colocate_t1 on v1 + v2 = v4 + v5 and v1 = v4 + 1 and v1 = v4");
        sqls.add("select * from colocate_t0, colocate_t1 where v1 = v5 and v1 = v4");
        sqls.add("select * from colocate_t0, colocate_t1 where v2 = v4 and v1 = v4");
        sqls.add("select * from colocate_t0, colocate_t1 where v1 + v2 = v4 + v5 and v1 = v4 + 1 and v1 = v4");

        sqls.add("select * from colocate_t0, colocate_t1, colocate_t2_1 where  v1 = v5 and v5 = v7");

        // TODO(packy) now we cannot derive v1 = v7 plan from the below sqls
        unsupportedSqls.add("select * from colocate_t0 join colocate_t1 on v1 = v5 join colocate_t2 on v5 = v7");
        unsupportedSqls.add("select * from colocate_t0 join colocate_t1 on v1 = v5 + v6 join colocate_t2 on v5 + v6 = v7");
        unsupportedSqls.add("select * from colocate_t0, colocate_t1, colocate_t2_1 where  v1 = v5 + v6 and v5 + v6 = v7");
        return sqls.stream().map(e -> Arguments.of(e));
    }

    private static Stream<Arguments> colocateJoinTwiceSqls() {
        List<String> sqls = Lists.newArrayList();
        // sqls should colocate join but not support now
        List<String> unsupportedSqls = Lists.newArrayList();
        sqls.add("select * from colocate_t0 join colocate_t1 on v1 = v4 join colocate_t2_1 on v4 = v7");
        sqls.add("select * from colocate_t0 join colocate_t1 on v1 = v5 and v1 = v4 join colocate_t2_1 on v5 = v7 and v7 = v2");
        sqls.add("select * from colocate_t0 join colocate_t1 on v1 = v5 join colocate_t2_1 on v1 = v4 and v1 = v7");


        sqls.add("select * from colocate_t0, colocate_t1, colocate_t2_1 where v1 = v4 and v4 = v7");
        sqls.add("select * from colocate_t0, colocate_t1, colocate_t2_1 where v1 = v5 and v1 = v4 and v5 = v7 and v7 = v2");
        sqls.add("select * from colocate_t0, colocate_t1, colocate_t2_1 where v1 = v5 and v1 = v4 and v1 = v7");


        // TODO(packy) the expr col seems not been equivalent conduction
        unsupportedSqls.add("select * from colocate_t0 join colocate_t1 on v1 = v5 and v1 = v4 + v6 and v1 = v4 " +
                "join colocate_t2_1 on v4 + v6 = v7");
        unsupportedSqls.add("select * from colocate_t0 join colocate_t1 on v1 + v2 = v4 and v1 + v2 = v5 - v4 " +
                "join colocate_t2_1 on v5 - v4 = v7 and v7 = v1");
        unsupportedSqls.add("select * from colocate_t0 join colocate_t1 on v1 + v2 = v4 - v3 and v1 = v4 + v5 " +
                "join colocate_t2_1 on v4 + v5 = v4 and v4 + v5 = v7");
        return sqls.stream().map(e -> Arguments.of(e));
    }

}
