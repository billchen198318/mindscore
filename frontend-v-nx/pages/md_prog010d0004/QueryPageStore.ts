import { defineStore } from 'pinia';
import { getInitGridConfigVariable, type GridConfig } from '@/components/GridHelper';

export const useMdProg010d0004Store = defineStore('md_prog010d0004', {
    state: () => ({
        gridConfig: getInitGridConfigVariable() as GridConfig,
        queryParam: {
            insightNo: '',
            title: '',
            insightType: '',
            severity: '',
            sourceType: '',
            status: 'OPEN',
            ownerAccount: '',
            generatedByType: ''
        }
    }),
    actions: {
        clearData() {
            this.queryParam.insightNo = '';
            this.queryParam.title = '';
            this.queryParam.insightType = '';
            this.queryParam.severity = '';
            this.queryParam.sourceType = '';
            this.queryParam.status = 'OPEN';
            this.queryParam.ownerAccount = '';
            this.queryParam.generatedByType = '';
            this.gridConfig.page = 1;
            this.gridConfig.row = 10;
            this.gridConfig.total = 0;
        }
    }
});
