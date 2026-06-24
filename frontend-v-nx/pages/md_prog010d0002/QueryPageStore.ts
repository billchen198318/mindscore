import { defineStore } from 'pinia';
import { getInitGridConfigVariable, type GridConfig } from '@/components/GridHelper';

export const useMdProg010d0002Store = defineStore('md_prog010d0002', {
    state: () => ({
        gridConfig: getInitGridConfigVariable() as GridConfig,
        queryParam: {
            sourceCode: '',
            sourceName: '',
            signalType: '',
            periodType: '',
            periodKey: '',
            statusCode: '',
            riskLevel: '',
            signalStatus: ''
        },
        generationParam: {
            kpiOid: '',
            periodType: '',
            periodKey: ''
        }
    }),
    actions: {
        clearData() {
            this.queryParam.sourceCode = '';
            this.queryParam.sourceName = '';
            this.queryParam.signalType = '';
            this.queryParam.periodType = '';
            this.queryParam.periodKey = '';
            this.queryParam.statusCode = '';
            this.queryParam.riskLevel = '';
            this.queryParam.signalStatus = '';
            this.gridConfig.page = 1;
            this.gridConfig.row = 10;
            this.gridConfig.total = 0;
        }
    }
});