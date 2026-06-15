import { defineStore } from 'pinia';
import { getInitGridConfigVariable, type GridConfig } from '@/components/GridHelper';

export const getMdProg003d0002Store = function() {
    return useMdProg003d0002Store();
}

export const useMdProg003d0002Store = defineStore('md_prog003d0002', {
    state: () => {
        return {
            gridConfig : getInitGridConfigVariable() as GridConfig,
            queryParam : {
                scopeType : '',
                kpiOid : '',
                colorType : '',
                colorCode : '',
                colorName : '',
                scoreStatus : '',
                enabled : ''
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
            this.queryParam.scopeType = '';
            this.queryParam.kpiOid = '';
            this.queryParam.colorType = '';
            this.queryParam.colorCode = '';
            this.queryParam.colorName = '';
            this.queryParam.scoreStatus = '';
            this.queryParam.enabled = '';
            this.gridConfig.page = 1;
            this.gridConfig.row = 10;
            this.gridConfig.total = 0;
        }
    },
})
