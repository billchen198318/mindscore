import { defineStore } from 'pinia';
import { getInitGridConfigVariable, type GridConfig } from '@/components/GridHelper';

export const getMdProg007d0003Store = function() {
    return useMdProg007d0003Store();
}

export const useMdProg007d0003Store = defineStore('md_prog007d0003', {
    state: () => {
        return {
            gridConfig : getInitGridConfigVariable() as GridConfig,
            queryParam : {
                workspaceOid : '',
                themeOid : '',
                objectiveCode : '',
                objectiveName : ''
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
            this.queryParam.objectiveCode = '';
            this.queryParam.objectiveName = '';
            this.gridConfig.page = 1;
            this.gridConfig.row = 10;
            this.gridConfig.total = 0;
        }
    },
})
