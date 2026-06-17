import { defineStore } from 'pinia';
import { getInitGridConfigVariable, type GridConfig } from '@/components/GridHelper';

export const getMdProg007d0002Store = function() {
    return useMdProg007d0002Store();
}

export const useMdProg007d0002Store = defineStore('md_prog007d0002', {
    state: () => {
        return {
            gridConfig : getInitGridConfigVariable() as GridConfig,
            queryParam : {
                workspaceOid : '',
                themeCode : '',
                themeName : ''
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
            this.queryParam.themeCode = '';
            this.queryParam.themeName = '';
            this.gridConfig.page = 1;
            this.gridConfig.row = 10;
            this.gridConfig.total = 0;
        }
    },
})
