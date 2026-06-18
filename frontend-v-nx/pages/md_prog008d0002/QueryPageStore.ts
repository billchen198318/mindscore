import { defineStore } from 'pinia';
import { getInitGridConfigVariable, type GridConfig } from '@/components/GridHelper';

export const getMdProg008d0002Store = function() {
    return useMdProg008d0002Store();
}

export const useMdProg008d0002Store = defineStore('md_prog008d0002', {
    state: () => {
        return {
            gridConfig : getInitGridConfigVariable() as GridConfig,
            queryParam : {
                planOid : '',
                itemName : '',
                actionStage : '',
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
            this.queryParam.planOid = '';
            this.queryParam.itemName = '';
            this.queryParam.actionStage = '';
            this.queryParam.status = '';
            this.gridConfig.page = 1;
            this.gridConfig.row = 10;
            this.gridConfig.total = 0;
        }
    },
})
