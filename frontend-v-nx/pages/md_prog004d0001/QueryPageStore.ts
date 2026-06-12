import { defineStore } from 'pinia';
import { getInitGridConfigVariable, type GridConfig } from '@/components/GridHelper';

export const getMdProg004d0001Store = function() {
    return useMdProg004d0001Store();
}

export const useMdProg004d0001Store = defineStore('md_prog004d0001', {
    state: () => {
        return {
            gridConfig : getInitGridConfigVariable() as GridConfig,
            queryParam : {
                kpiOid : '',
                periodType : '',
                periodKey : '',
                dataForType : '',
                account : '',
                orgOid : ''
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
            this.queryParam.kpiOid = '';
            this.queryParam.periodType = '';
            this.queryParam.periodKey = '';
            this.queryParam.dataForType = '';
            this.queryParam.account = '';
            this.queryParam.orgOid = '';
            this.gridConfig.page = 1;
            this.gridConfig.row = 10;
            this.gridConfig.total = 0;
        }
    },
})
