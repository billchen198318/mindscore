import { defineStore } from 'pinia';
import { getInitGridConfigVariable, type GridConfig } from '@/components/GridHelper';
import { defaultPeriodKey } from '@/types/Period';

const currentMonthKey = () => defaultPeriodKey('MONTH');

export const useMdProg005d0001Store = defineStore('md_prog005d0001', {
    state: () => {
        return {
            gridConfig : getInitGridConfigVariable() as GridConfig,
            queryParam : {
                kpiOid : '',
                periodMode : 'SINGLE',
                periodType : 'MONTH',
                periodKey : currentMonthKey(),
                periodKeyFrom : '',
                periodKeyTo : '',
                dataForType : '',
                account : '',
                orgOid : ''
            }
        }
    },
    actions: {
        clearData() {
            this.queryParam.kpiOid = '';
            this.queryParam.periodMode = 'SINGLE';
            this.queryParam.periodType = 'MONTH';
            this.queryParam.periodKey = currentMonthKey();
            this.queryParam.periodKeyFrom = '';
            this.queryParam.periodKeyTo = '';
            this.queryParam.dataForType = '';
            this.queryParam.account = '';
            this.queryParam.orgOid = '';
            this.gridConfig.page = 1;
            this.gridConfig.row = 10;
            this.gridConfig.total = 0;
        }
    },
})
