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

package org.apache.kylin.source.hive;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class BeelineOptionsProcessor {
    private final Options options = new Options();

    public BeelineOptionsProcessor() {

        options.addOption(OptionBuilder.hasArg().withArgName("url").create('u'));
        options.addOption(OptionBuilder.hasArg().withArgName("username").create('n'));
        options.addOption(OptionBuilder.hasArg().withArgName("password").create('p'));

    }

    public CommandLine process(String[] argv) {
        try {
            return new GnuParser().parse(options, argv);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
