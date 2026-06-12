import { defineStore } from 'pinia';
import { getInitGridConfigVariable, type GridConfig } from '@/components/GridHelper';

export const getMdProg002d0003Store = function() {
    return useMdProg002d0003Store();
}

export const useMdProg002d0003Store = defineStore('md_prog002d0003', {
    state: () => {
        return {
            gridConfig : getInitGridConfigVariable() as GridConfig,
            queryParam : {
                ruleCode : '',
                ruleName : '',
                managementMode : '',
                compareMode : '',
                periodType : '',
                dataType : '',
                isDefault : '',
                enabled : ''
            }
        }
    },
    actions: {
        setQueryParam(qJsonRes:any) {
            this.queryParam = qJsonRes;
        },
        setGridConfig(gJsonRes:any) {
            this.gridConfig = gJsonRes;
        },
        clearData() {
            this.queryParam.ruleCode = '';
            this.queryParam.ruleName = '';
            this.queryParam.managementMode = '';
            this.queryParam.compareMode = '';
            this.queryParam.periodType = '';
            this.queryParam.dataType = '';
            this.queryParam.isDefault = '';
            this.queryParam.enabled = '';
            this.gridConfig.page = 1;
            this.gridConfig.row = 10;
            this.gridConfig.total = 0;
        }
    },
})
