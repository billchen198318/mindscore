import { defineStore } from 'pinia';

const currentDateKey = () => new Date().toISOString().slice(0, 10);

export const useMdProg006d0006Store = defineStore('md_prog006d0006', {
    state: () => {
        return {
            queryParam : {
                cycleOid : '',
                periodKey : currentDateKey(),
                status : '',
                orgOid : '',
                account : ''
            }
        }
    },
    actions: {
        clearData() {
            this.queryParam.cycleOid = '';
            this.queryParam.periodKey = currentDateKey();
            this.queryParam.status = '';
            this.queryParam.orgOid = '';
            this.queryParam.account = '';
        }
    },
})
