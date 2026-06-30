import { defineStore } from 'pinia';
import { getInitGridConfigVariable, type GridConfig } from '@/components/GridHelper';

export const useMdProg010d0005Store = defineStore('md_prog010d0005', {
    state: () => ({
        insightGridConfig: getInitGridConfigVariable() as GridConfig,
        recommendationGridConfig: getInitGridConfigVariable() as GridConfig,
        queryParam: {
            insightNo: '',
            title: '',
            insightType: '',
            severity: '',
            sourceType: '',
            status: 'OPEN',
            ownerAccount: '',
            generatedByType: ''
        },
        recommendationQueryParam: {
            recommendationType: '',
            title: '',
            status: '',
            acceptedFlag: '',
            actionCreatedFlag: ''
        }
    }),
    actions: {
        clearInsightQuery() {
            this.queryParam.insightNo = '';
            this.queryParam.title = '';
            this.queryParam.insightType = '';
            this.queryParam.severity = '';
            this.queryParam.sourceType = '';
            this.queryParam.status = 'OPEN';
            this.queryParam.ownerAccount = '';
            this.queryParam.generatedByType = '';
            this.insightGridConfig.page = 1;
            this.insightGridConfig.row = 10;
            this.insightGridConfig.total = 0;
        },
        clearRecommendationQuery() {
            this.recommendationQueryParam.recommendationType = '';
            this.recommendationQueryParam.title = '';
            this.recommendationQueryParam.status = '';
            this.recommendationQueryParam.acceptedFlag = '';
            this.recommendationQueryParam.actionCreatedFlag = '';
            this.recommendationGridConfig.page = 1;
            this.recommendationGridConfig.row = 10;
            this.recommendationGridConfig.total = 0;
        }
    }
});
