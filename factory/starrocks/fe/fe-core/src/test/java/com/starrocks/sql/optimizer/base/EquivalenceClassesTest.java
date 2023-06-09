// This file is licensed under the Elastic License 2.0. Copyright 2021-present, StarRocks Inc.

package com.starrocks.sql.optimizer.base;

import com.starrocks.catalog.Type;
import com.starrocks.sql.optimizer.operator.scalar.ColumnRefOperator;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class EquivalenceClassesTest {
    @Test
    public void test() {
        ColumnRefFactory columnRefFactory = new ColumnRefFactory();
        EquivalenceClasses ec = new EquivalenceClasses();
        ColumnRefOperator columnRef1 = columnRefFactory.create("column1", Type.INT, true);
        ColumnRefOperator columnRef2 = columnRefFactory.create("column2", Type.INT, true);
        ColumnRefOperator columnRef3 = columnRefFactory.create("column3", Type.INT, true);
        ColumnRefOperator columnRef4 = columnRefFactory.create("column4", Type.INT, true);
        ec.addEquivalence(columnRef1, columnRef2);
        ec.addEquivalence(columnRef1, columnRef3);
        ec.addEquivalence(columnRef4, columnRef3);

        Set<ColumnRefOperator> columnSet = ec.getEquivalenceClass(columnRef1);
        Assert.assertEquals(4, columnSet.size());
        List<Set<ColumnRefOperator>> columnSetList = ec.getEquivalenceClasses();
        Assert.assertEquals(4, columnSetList.size());
        Assert.assertEquals(columnSetList.get(0), columnSetList.get(1));
        Assert.assertEquals(columnSetList.get(1), columnSetList.get(2));
        Assert.assertEquals(columnSetList.get(2), columnSetList.get(3));

        ColumnRefOperator columnRef5 = columnRefFactory.create("column5", Type.INT, true);
        ColumnRefOperator columnRef6 = columnRefFactory.create("column6", Type.INT, true);
        ColumnRefOperator columnRef7 = columnRefFactory.create("column7", Type.INT, true);
        ec.addEquivalence(columnRef5, columnRef6);
        ec.addEquivalence(columnRef5, columnRef7);
        Assert.assertEquals(ec.getEquivalenceClass(columnRef6), ec.getEquivalenceClass(columnRef7));

        EquivalenceClasses ec2 = new EquivalenceClasses();
        ec2.addEquivalence(columnRef1, columnRef2);
        ec2.addEquivalence(columnRef1, columnRef3);

        ec2.addEquivalence(columnRef5, columnRef6);
        ec2.addEquivalence(columnRef5, columnRef7);
        ec2.addEquivalence(columnRef1, columnRef6);
        Assert.assertEquals(ec2.getEquivalenceClass(columnRef1), ec2.getEquivalenceClass(columnRef5));
        Assert.assertEquals(6, ec2.getEquivalenceClass(columnRef1).size());
    }
}
