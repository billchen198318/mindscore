import { defineStore } from 'pinia';
import { getInitGridConfigVariable, type GridConfig } from '@/components/GridHelper';

export const getMdProg006d0002Store = function() {
    return useMdProg006d0002Store();
}

export const useMdProg006d0002Store = defineStore('md_prog006d0002', {
    state: () => {
        return {
            gridConfig : getInitGridConfigVariable() as GridConfig,
            queryParam : {
                cycleOid : '',
                objectiveCode : '',
                objectiveName : '',
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
            this.queryParam.cycleOid = '';
            this.queryParam.objectiveCode = '';
            this.queryParam.objectiveName = '';
            this.queryParam.status = '';
            this.gridConfig.page = 1;
            this.gridConfig.row = 10;
            this.gridConfig.total = 0;
        }
    },
})
