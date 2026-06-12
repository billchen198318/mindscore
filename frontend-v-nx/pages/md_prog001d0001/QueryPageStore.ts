import { defineStore } from 'pinia';
import { getInitGridConfigVariable, type GridConfig } from '@/components/GridHelper';

export const getMdProg001d0001Store = function() {
    return useMdProg001d0001Store();
}

export const useMdProg001d0001Store = defineStore('md_prog001d0001', {
    state: () => {
        return { 
            gridConfig : getInitGridConfigVariable() as GridConfig,
            queryParam : {
                orgCode : '',
                orgName : ''
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
            this.queryParam.orgCode = '';
            this.queryParam.orgName = '';
            this.gridConfig.page = 1;
            this.gridConfig.row = 10;
            this.gridConfig.total = 0;
        }
    },
})