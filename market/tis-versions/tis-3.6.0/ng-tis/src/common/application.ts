/**
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

/**
 * Created by baisui on 2017/3/29 0029.
 */
export class Application {
  // constructor(public name: string,
  //             public tpl: string,
  //             public workflowId: number,
  //             public crontab: Crontab,
  //             public departmentId: number,
  //             public recept: string /*接口人*/) {
  // }
  appId: number;
  appType: AppType;
  createTime: number;
  dptId: number;
  dptName: string;
  projectName: string;
  recept: string;
  updateTime: number;
}

export enum AppType {
  DataX = 2,
  Solr = 1
}

export class Crontab {
}
