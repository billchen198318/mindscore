import { defineStore } from 'pinia';
import { getInitGridConfigVariable, type GridConfig } from '@/components/GridHelper';

export const useMdProg006d0004Store = defineStore('md_prog006d0004', {
    state: () => {
        return {
            gridConfig : getInitGridConfigVariable() as GridConfig,
            queryParam : {
                cycleOid : '',
                objectiveOid : '',
                krOid : '',
                checkinDateStart : '',
                checkinDateEnd : ''
            }
        }
    },
    actions: {
        clearData() {
            this.queryParam.cycleOid = '';
            this.queryParam.objectiveOid = '';
            this.queryParam.krOid = '';
            this.queryParam.checkinDateStart = '';
            this.queryParam.checkinDateEnd = '';
            this.gridConfig.page = 1;
            this.gridConfig.row = 10;
            this.gridConfig.total = 0;
        }
    },
})
