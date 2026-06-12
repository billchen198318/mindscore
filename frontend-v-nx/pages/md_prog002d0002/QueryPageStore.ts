import { defineStore } from 'pinia';
import { getInitGridConfigVariable, type GridConfig } from '@/components/GridHelper';

export const getMdProg002d0002Store = function() {
    return useMdProg002d0002Store();
}

export const useMdProg002d0002Store = defineStore('md_prog002d0002', {
    state: () => {
        return {
            gridConfig : getInitGridConfigVariable() as GridConfig,
            queryParam : {
                aggrCode : '',
                aggrName : '',
                aggrType : '',
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
            this.queryParam.aggrCode = '';
            this.queryParam.aggrName = '';
            this.queryParam.aggrType = '';
            this.queryParam.enabled = '';
            this.gridConfig.page = 1;
            this.gridConfig.row = 10;
            this.gridConfig.total = 0;
        }
    },
})
