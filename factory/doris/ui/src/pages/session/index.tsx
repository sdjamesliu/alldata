/**
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

import React, {useEffect, useState} from 'react';
import {Row, Typography} from 'antd';
import {getSession} from 'Src/api/api';
import Table from 'Src/components/table';

const {Text, Title} = Typography;
// import {useHistory} from 'react-router-dom';
export default function Session(params: any) {
    // const [parentUrl, setParentUrl] = useState('');
    const [allTableData, setAllTableData] = useState<any>({column_names: [], rows: []});
    // const history = useHistory();
    const getSessionData = function (ac: AbortController) {
        getSession({signal: ac.signal}).then(res => {
            if (res && res.msg === 'success') {
                setAllTableData(res.data);
            }
        }).catch(err => {
        });
    };
    useEffect(() => {
        const ac = new AbortController();
        getSessionData(ac);
        return () => ac.abort();
    }, []);
    return (
        <Typography style={{padding: '30px'}}>
            <Title>Session Info</Title>

            <Row style={{paddingBottom: '15px'}}>
                <Text strong={true}>This page lists the session info, there are {allTableData?.rows?.length} active
                    sessions.</Text>
            </Row>
            <Table
                isSort={true}
                isFilter={true}
                // isInner={true}
                allTableData={allTableData}
                rowKey={(record) => record.Id}
            />
        </Typography>
    );
}
 
