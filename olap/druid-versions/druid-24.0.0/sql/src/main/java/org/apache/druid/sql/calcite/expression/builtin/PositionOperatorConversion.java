/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.druid.sql.calcite.expression.builtin;

import com.google.common.collect.ImmutableList;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.segment.column.ColumnType;
import org.apache.druid.segment.column.RowSignature;
import org.apache.druid.sql.calcite.expression.DruidExpression;
import org.apache.druid.sql.calcite.expression.OperatorConversions;
import org.apache.druid.sql.calcite.expression.SqlOperatorConversion;
import org.apache.druid.sql.calcite.planner.PlannerContext;

public class PositionOperatorConversion implements SqlOperatorConversion
{
  private static final DruidExpression ZERO = DruidExpression.ofLiteral(ColumnType.LONG, "0");

  @Override
  public SqlOperator calciteOperator()
  {
    return SqlStdOperatorTable.POSITION;
  }

  @Override
  public DruidExpression toDruidExpression(
      final PlannerContext plannerContext,
      final RowSignature rowSignature,
      final RexNode rexNode
  )
  {
    return OperatorConversions.convertCall(
        plannerContext,
        rowSignature,
        rexNode,
        druidExpressions -> DruidExpression.ofExpression(
            ColumnType.LONG,
            (args) -> {
              final DruidExpression fromIndexExpression;
              if (args.size() > 2) {
                fromIndexExpression = DruidExpression.ofExpression(
                    ColumnType.LONG,
                    (_args) -> StringUtils.format("(%s - 1)", _args.get(2).getExpression()),
                    args
                );
              } else {
                fromIndexExpression = ZERO;
              }
              return StringUtils.format(
                  "(%s + 1)",
                  DruidExpression.functionCall(
                      "strpos",
                      ImmutableList.of(args.get(1), args.get(0), fromIndexExpression)
                  )
              );
            },
            druidExpressions
        )
    );
  }
}
