import { defineStore } from 'pinia';
import { getInitGridConfigVariable, type GridConfig } from '@/components/GridHelper';

export const getMdProg007d0004Store = function() {
    return useMdProg007d0004Store();
}

export const useMdProg007d0004Store = defineStore('md_prog007d0004', {
    state: () => {
        return {
            gridConfig : getInitGridConfigVariable() as GridConfig,
            queryParam : {
                workspaceOid : '',
                themeOid : '',
                strategyObjectiveOid : '',
                linkType : '',
                linkOid : ''
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
            this.queryParam.workspaceOid = '';
            this.queryParam.themeOid = '';
            this.queryParam.strategyObjectiveOid = '';
            this.queryParam.linkType = '';
            this.queryParam.linkOid = '';
            this.gridConfig.page = 1;
            this.gridConfig.row = 10;
            this.gridConfig.total = 0;
        }
    },
})
