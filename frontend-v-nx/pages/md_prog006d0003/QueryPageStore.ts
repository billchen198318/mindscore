import { defineStore } from 'pinia';
import { getInitGridConfigVariable, type GridConfig } from '@/components/GridHelper';

export const getMdProg006d0003Store = function() {
    return useMdProg006d0003Store();
}

export const useMdProg006d0003Store = defineStore('md_prog006d0003', {
    state: () => {
        return {
            gridConfig : getInitGridConfigVariable() as GridConfig,
            queryParam : {
                cycleOid : '',
                objectiveOid : '',
                krCode : '',
                krName : '',
                krType : '',
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
            this.queryParam.objectiveOid = '';
            this.queryParam.krCode = '';
            this.queryParam.krName = '';
            this.queryParam.krType = '';
            this.queryParam.status = '';
            this.gridConfig.page = 1;
            this.gridConfig.row = 10;
            this.gridConfig.total = 0;
        }
    },
})
