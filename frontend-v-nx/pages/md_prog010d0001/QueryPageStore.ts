import { defineStore } from 'pinia';
import { getInitGridConfigVariable, type GridConfig } from '@/components/GridHelper';

export const getMdProg010d0001Store = function() {
    return useMdProg010d0001Store();
}

export const useMdProg010d0001Store = defineStore('md_prog010d0001', {
    state: () => {
        return {
            gridConfig : getInitGridConfigVariable() as GridConfig,
            logGridConfig : getInitGridConfigVariable() as GridConfig,
            queryParam : {
                providerCode : '',
                providerName : '',
                providerType : '',
                enabledFlag : ''
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
        setLogGridConfig(gJsonRes:any) {
            this.logGridConfig = gJsonRes;
        },
        clearData() {
            this.queryParam.providerCode = '';
            this.queryParam.providerName = '';
            this.queryParam.providerType = '';
            this.queryParam.enabledFlag = '';
            this.gridConfig.page = 1;
            this.gridConfig.row = 10;
            this.gridConfig.total = 0;
        }
    },
})
