// Copyright Contributors to the Amundsen project.
// SPDX-License-Identifier: Apache-2.0

import axios, { AxiosResponse } from 'axios';
import * as qs from 'simple-query-string';

import { ResourceType } from 'interfaces/Resources';
import { DashboardMetadata } from 'interfaces/Dashboard';

import { STATUS_CODES } from '../../../constants';

export type GetDashboardAPI = {
  msg: string;
  dashboard: DashboardMetadata;
};

const DASHBOARD_BASE = '/api/metadata/v0';

const getDashboardDataFromResponseData = (data) => {
  // Adds the type to the query resource
  data.queries = data.queries.map((item) => ({
    ...item,
    type: ResourceType.query,
  }));

  return data;
};

export function getDashboard(uri: string, index?: string, source?: string) {
  const queryParams = qs.stringify({ index, source, uri });

  return axios
    .get(`${DASHBOARD_BASE}/dashboard?${queryParams}`)
    .then((response: AxiosResponse<GetDashboardAPI>) => {
      const { data, status } = response;

      return {
        dashboard: getDashboardDataFromResponseData(data.dashboard),
        statusCode: status,
      };
    })
    .catch((e) => {
      const { response } = e;
      const statusMessage = response
        ? response.data
          ? response.data.msg
          : undefined
        : undefined;
      const statusCode = response
        ? response.status || STATUS_CODES.INTERNAL_SERVER_ERROR
        : STATUS_CODES.INTERNAL_SERVER_ERROR;

      return Promise.reject({
        statusCode,
        statusMessage,
      });
    });
}
