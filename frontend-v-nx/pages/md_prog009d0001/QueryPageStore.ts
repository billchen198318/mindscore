import { defineStore } from 'pinia';
import { getInitGridConfigVariable, type GridConfig } from '@/components/GridHelper';

export const getMdProg009d0001Store = function() {
    return useMdProg009d0001Store();
}

export const useMdProg009d0001Store = defineStore('md_prog009d0001', {
    state: () => {
        return {
            gridConfig : getInitGridConfigVariable() as GridConfig,
            queryParam : {
                periodType : '',
                periodKey : '',
                periodKeyFrom : '',
                periodKeyTo : '',
                dataForType : '',
                account : '',
                orgOid : '',
                cycleOid : '',
                workspaceOid : ''
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
            this.queryParam.periodType = '';
            this.queryParam.periodKey = '';
            this.queryParam.periodKeyFrom = '';
            this.queryParam.periodKeyTo = '';
            this.queryParam.dataForType = '';
            this.queryParam.account = '';
            this.queryParam.orgOid = '';
            this.queryParam.cycleOid = '';
            this.queryParam.workspaceOid = '';
            this.gridConfig.page = 1;
            this.gridConfig.row = 10;
            this.gridConfig.total = 0;
        }
    },
})
