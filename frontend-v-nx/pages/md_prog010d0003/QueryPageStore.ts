import { defineStore } from 'pinia';
import { getInitGridConfigVariable, type GridConfig } from '@/components/GridHelper';

export const useMdProg010d0003Store = defineStore('md_prog010d0003', {
    state: () => ({
        gridConfig: getInitGridConfigVariable() as GridConfig,
        queryParam: {
            ruleCode: '',
            ruleName: '',
            ruleType: '',
            sourceType: '',
            severity: '',
            enabledFlag: ''
        }
    }),
    actions: {
        clearData() {
            this.queryParam.ruleCode = '';
            this.queryParam.ruleName = '';
            this.queryParam.ruleType = '';
            this.queryParam.sourceType = '';
            this.queryParam.severity = '';
            this.queryParam.enabledFlag = '';
            this.gridConfig.page = 1;
            this.gridConfig.row = 10;
            this.gridConfig.total = 0;
        }
    }
});