import { defineStore } from 'pinia';
import { getInitGridConfigVariable, type GridConfig } from '@/components/GridHelper';

export const getMdProg008d0001Store = function() {
    return useMdProg008d0001Store();
}

export const useMdProg008d0001Store = defineStore('md_prog008d0001', {
    state: () => {
        return {
            gridConfig : getInitGridConfigVariable() as GridConfig,
            queryParam : {
                planCode : '',
                planName : '',
                status : ''
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
            this.queryParam.planCode = '';
            this.queryParam.planName = '';
            this.queryParam.status = '';
            this.gridConfig.page = 1;
            this.gridConfig.row = 10;
            this.gridConfig.total = 0;
        }
    },
})
