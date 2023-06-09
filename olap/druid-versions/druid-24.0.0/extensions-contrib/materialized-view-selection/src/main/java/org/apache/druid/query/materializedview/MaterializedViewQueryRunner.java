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

package org.apache.druid.query.materializedview;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.druid.java.util.common.guava.MergeSequence;
import org.apache.druid.java.util.common.guava.Sequence;
import org.apache.druid.java.util.common.guava.Sequences;
import org.apache.druid.query.Query;
import org.apache.druid.query.QueryPlus;
import org.apache.druid.query.QueryRunner;
import org.apache.druid.query.context.ResponseContext;


public class MaterializedViewQueryRunner<T> implements QueryRunner<T>
{
  private final QueryRunner runner;
  private final DataSourceOptimizer optimizer;
  
  public MaterializedViewQueryRunner(QueryRunner queryRunner, DataSourceOptimizer optimizer)
  {
    this.runner = queryRunner;
    this.optimizer = optimizer;
  }

  @Override
  public Sequence<T> run(QueryPlus<T> queryPlus, ResponseContext responseContext)
  {
    Query query = queryPlus.getQuery();
    return new MergeSequence<>(
        query.getResultOrdering(),
        Sequences.simple(
            Lists.transform(
                optimizer.optimize(query),
                new Function<Query, Sequence<T>>()
                {
                  @Override
                  public Sequence<T> apply(Query query)
                  {
                    return runner.run(
                        queryPlus.withQuery(query),
                        responseContext
                    );
                  }
                }
            )
        )
    );
  }
}
