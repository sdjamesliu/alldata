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

import React, { useState } from 'react';

import { BasicAction } from '../../utils/basic-action';
import { SideButtonMetaData, TableActionDialog } from '../table-action-dialog/table-action-dialog';

import { LookupValuesTable } from './lookup-values-table/lookup-values-table';

interface LookupTableActionDialogProps {
  lookupId?: string;
  actions: BasicAction[];
  onClose: () => void;
}

export const LookupTableActionDialog = React.memo(function LookupTableActionDialog(
  props: LookupTableActionDialogProps,
) {
  const { onClose, lookupId, actions } = props;
  const [activeTab, setActiveTab] = useState('values');

  const taskTableSideButtonMetadata: SideButtonMetaData[] = [
    {
      icon: 'list-columns',
      text: 'Values',
      active: activeTab === 'values',
      onClick: () => setActiveTab('values'),
    },
  ];

  return (
    <TableActionDialog
      sideButtonMetadata={taskTableSideButtonMetadata}
      onClose={onClose}
      title={`Lookup: ${lookupId}`}
      actions={actions}
    >
      {activeTab === 'values' && (
        <LookupValuesTable
          lookupId={lookupId ? lookupId : ''}
          downloadFilename={`lookup-dimensions-${lookupId}.json`}
        />
      )}
    </TableActionDialog>
  );
});
