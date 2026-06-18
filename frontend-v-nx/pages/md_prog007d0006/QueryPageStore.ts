import { defineStore } from 'pinia';

export const getMdProg007d0006Store = function() {
    return useMdProg007d0006Store();
}

export const useMdProg007d0006Store = defineStore('md_prog007d0006', {
    state: () => {
        return {
            queryParam : {
                workspaceOid : '',
                periodType : 'MONTH',
                periodKey : '',
                dataForType : 'GLOBAL',
                account : '',
                orgOid : ''
            }
        }
    },
    actions: {
        setQueryParam(qJsonRes:any) {
            this.queryParam = qJsonRes;
        },
        clearData() {
            this.queryParam.workspaceOid = '';
            this.queryParam.periodType = 'MONTH';
            this.queryParam.periodKey = '';
            this.queryParam.dataForType = 'GLOBAL';
            this.queryParam.account = '';
            this.queryParam.orgOid = '';
        }
    },
})
