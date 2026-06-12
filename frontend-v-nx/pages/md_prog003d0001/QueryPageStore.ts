import { defineStore } from 'pinia';
import { getInitGridConfigVariable, type GridConfig } from '@/components/GridHelper';

export const getMdProg003d0001Store = function() {
    return useMdProg003d0001Store();
}

export const useMdProg003d0001Store = defineStore('md_prog003d0001', {
    state: () => {
        return {
            gridConfig : getInitGridConfigVariable() as GridConfig,
            queryParam : {
                kpiCode : '',
                kpiName : '',
                dataType : '',
                periodType : '',
                managementMode : '',
                compareMode : '',
                formulaSelectionMode : '',
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
            this.queryParam.kpiCode = '';
            this.queryParam.kpiName = '';
            this.queryParam.dataType = '';
            this.queryParam.periodType = '';
            this.queryParam.managementMode = '';
            this.queryParam.compareMode = '';
            this.queryParam.formulaSelectionMode = '';
            this.queryParam.enabled = '';
            this.gridConfig.page = 1;
            this.gridConfig.row = 10;
            this.gridConfig.total = 0;
        }
    },
})
