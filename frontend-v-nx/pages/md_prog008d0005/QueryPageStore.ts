import { defineStore } from 'pinia';
import { getInitGridConfigVariable, type GridConfig } from '@/components/GridHelper';

export const getMdProg008d0005Store = function() {
    return useMdProg008d0005Store();
}

export const useMdProg008d0005Store = defineStore('md_prog008d0005', {
    state: () => {
        return {
            gridConfig : getInitGridConfigVariable() as GridConfig,
            queryParam : {
                planOid : '',
                actionStage : '',
                status : '',
                ownerType : '',
                account : '',
                orgOid : '',
                sourceType : '',
                sourceOid : '',
                startDateFrom : '',
                startDateTo : '',
                endDateFrom : '',
                endDateTo : '',
                overdueOnly : ''
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
            this.queryParam.actionStage = '';
            this.queryParam.status = '';
            this.queryParam.ownerType = '';
            this.queryParam.account = '';
            this.queryParam.orgOid = '';
            this.queryParam.sourceType = '';
            this.queryParam.sourceOid = '';
            this.queryParam.startDateFrom = '';
            this.queryParam.startDateTo = '';
            this.queryParam.endDateFrom = '';
            this.queryParam.endDateTo = '';
            this.queryParam.overdueOnly = '';
            this.gridConfig.page = 1;
            this.gridConfig.row = 10;
            this.gridConfig.total = 0;
        }
    },
})
