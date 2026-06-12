import { defineStore } from 'pinia';
import { getInitGridConfigVariable, type GridConfig } from '@/components/GridHelper';

export const getMdProg002d0001Store = function() {
    return useMdProg002d0001Store();
}

export const useMdProg002d0001Store = defineStore('md_prog002d0001', {
    state: () => {
        return {
            gridConfig : getInitGridConfigVariable() as GridConfig,
            queryParam : {
                formulaCode : '',
                formulaName : '',
                formulaType : '',
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
            this.queryParam.formulaCode = '';
            this.queryParam.formulaName = '';
            this.queryParam.formulaType = '';
            this.queryParam.enabled = '';
            this.gridConfig.page = 1;
            this.gridConfig.row = 10;
            this.gridConfig.total = 0;
        }
    },
})
