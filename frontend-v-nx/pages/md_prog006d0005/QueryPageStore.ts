import { defineStore } from 'pinia';
import { getInitGridConfigVariable, type GridConfig } from '@/components/GridHelper';

export const useMdProg006d0005Store = defineStore('md_prog006d0005', {
    state: () => {
        return {
            gridConfig : getInitGridConfigVariable() as GridConfig,
            queryParam : {
                cycleOid : '',
                objectiveOid : '',
                periodKeyFrom : '',
                periodKeyTo : '',
                scoreStatus : '',
                orgOid : '',
                account : ''
            }
        }
    },
    actions: {
        clearData() {
            this.queryParam.cycleOid = '';
            this.queryParam.objectiveOid = '';
            this.queryParam.periodKeyFrom = '';
            this.queryParam.periodKeyTo = '';
            this.queryParam.scoreStatus = '';
            this.queryParam.orgOid = '';
            this.queryParam.account = '';
            this.gridConfig.page = 1;
            this.gridConfig.row = 10;
            this.gridConfig.total = 0;
        }
    },
})
