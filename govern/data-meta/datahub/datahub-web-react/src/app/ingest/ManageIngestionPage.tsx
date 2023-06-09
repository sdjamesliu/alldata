import {Tabs, Typography} from 'antd';
import React, {useState} from 'react';
import styled from 'styled-components';
import {IngestionSourceList} from './source/IngestionSourceList';
import {SecretsList} from './secret/SecretsList';
import {OnboardingTour} from '../onboarding/OnboardingTour';
import {
    INGESTION_CREATE_SOURCE_ID,
    INGESTION_REFRESH_SOURCES_ID,
} from '../onboarding/config/IngestionOnboardingConfig';

const PageContainer = styled.div`
    padding-top: 20px;
`;

const PageHeaderContainer = styled.div`
    && {
        padding-left: 24px;
    }
`;

const PageTitle = styled(Typography.Title)`
    && {
        margin-bottom: 12px;
    }
`;

const StyledTabs = styled(Tabs)`
    &&& .ant-tabs-nav {
        margin-bottom: 0;
        padding-left: 28px;
    }
`;

const Tab = styled(Tabs.TabPane)`
    font-size: 14px;
    line-height: 22px;
`;

const ListContainer = styled.div``;

enum TabType {
    Sources = 'Sources',
    Secrets = 'Secrets',
}

export const ManageIngestionPage = () => {
    /**
     * Determines which view should be visible: ingestion sources or secrets.
     */
    const [selectedTab, setSelectedTab] = useState<TabType>(TabType.Sources);

    const onClickTab = (newTab: string) => {
        setSelectedTab(TabType[newTab]);
    };

    return (
        <PageContainer>
            <OnboardingTour stepIds={[INGESTION_CREATE_SOURCE_ID, INGESTION_REFRESH_SOURCES_ID]}/>
            <PageHeaderContainer>
                <PageTitle level={3}>管理元数据</PageTitle>
                <Typography.Paragraph type="secondary">创建、调度和运行DataHub获取源。</Typography.Paragraph>
            </PageHeaderContainer>
            <StyledTabs activeKey={selectedTab} size="large" onTabClick={(tab: string) => onClickTab(tab)}>
                {/* eslint-disable-next-line eqeqeq */}
                <Tab key={TabType.Sources} tab={TabType.Sources == 'Sources' ? '元数据' : 'Sources'}/>
                {/* eslint-disable-next-line eqeqeq */}
                <Tab key={TabType.Secrets} tab={TabType.Secrets == 'Secrets' ? '秘密' : 'Secrets'}/>
            </StyledTabs>
            <ListContainer>{selectedTab === TabType.Sources ? <IngestionSourceList/> : <SecretsList/>}</ListContainer>
        </PageContainer>
    );
};
