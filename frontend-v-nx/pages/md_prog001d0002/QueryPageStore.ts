import { defineStore } from 'pinia';
import { getInitGridConfigVariable, type GridConfig } from '@/components/GridHelper';

export const getMdProg001d0002Store = function() {
    return useMdProg001d0002Store();
}

export const useMdProg001d0002Store = defineStore('md_prog001d0002', {
    state: () => {
        return { 
            gridConfig : getInitGridConfigVariable() as GridConfig,
            queryParam : {
                orgOid : '',
                account : '',
                displayName : '',
                employeeId : '',
                email : ''
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
            this.queryParam.orgOid = '';
            this.queryParam.account = '';
            this.queryParam.displayName = '';
            this.queryParam.employeeId = '';
            this.queryParam.email = '';
            this.gridConfig.page = 1;
            this.gridConfig.row = 10;
            this.gridConfig.total = 0;
        }
    },
})