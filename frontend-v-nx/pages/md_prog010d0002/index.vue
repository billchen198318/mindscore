<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';
import { toast } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

import Toolbar from '@/components/Toolbar.vue';
import Grid from '@/components/Grid.vue';
import GridPagination from '@/components/GridPagination.vue';
import HiddenQueryFieldAlertInfo from '@/components/HiddenQueryFieldAlertInfo.vue';
import PeriodPicker from '@/components/PeriodPicker.vue';
import { getGridConfig, resetConfigByOld, setConfigPage, setConfigRow, setConfigTotal } from '@/components/GridHelper';
import { escapeQifuHtmlMsg, getAxiosInstance } from '@/components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';
import { PageConstants, periodTypeOptions, signalTypeOptions } from './config';
import { useMdProg010d0002Store } from './QueryPageStore';

definePageMeta({ middleware: ['auth'] });

const queryPageStore = useMdProg010d0002Store();
const { showLoading, hideLoading, confirmFire } = useSwalLoading();
const pageProgramId = ref(PageConstants.QueryId);
const qFieldShow = ref(true);
const dsList = ref<any[]>([]);
const kpiList = ref<any[]>([]);

const statusHtml = (value: string) => {
    const status = value || 'UNKNOWN';
    const style = ['BAD', 'DOWN', 'BELOW_TARGET', 'ABOVE_TARGET', 'OUT_OF_RANGE'].includes(status)
        ? 'text-bg-danger' : ['WARNING', 'UNKNOWN'].includes(status) ? 'text-bg-warning' : 'text-bg-success';
    return `<span class="badge ${style}">${status}</span>`;
};
const riskHtml = (value: string) => {
    const style = value === 'HIGH' || value === 'CRITICAL' ? 'text-bg-danger'
        : value === 'MEDIUM' ? 'text-bg-warning' : 'text-bg-success';
    return `<span class="badge ${style}">${value || '-'}</span>`;
};
const lifecycleHtml = (value: string) => `<span class="badge ${value === 'OPEN' ? 'text-bg-warning' : 'text-bg-secondary'}">${value || '-'}</span>`;
const numberText = (value: any) => value == null ? '-' : Number(value).toFixed(2);
const dateText = (value: any) => value ? new Date(value).toLocaleString() : '-';
const isSearchNoData = (message: any) => String(message || '').toLowerCase().includes('search no data');
const pad2 = (value: number) => String(value).padStart(2, '0');
const isoWeek = (date: Date) => {
    const d = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
    d.setUTCDate(d.getUTCDate() + 4 - (d.getUTCDay() || 7));
    const yearStart = new Date(Date.UTC(d.getUTCFullYear(), 0, 1));
    return { year: d.getUTCFullYear(), week: Math.ceil((((d.getTime() - yearStart.getTime()) / 86400000) + 1) / 7) };
};
const defaultPeriodKey = (periodType: string) => {
    const now = new Date();
    if (periodType === 'DAY') return now.toISOString().slice(0, 10);
    if (periodType === 'WEEK') {
        const info = isoWeek(now);
        return `${info.year}-W${pad2(info.week)}`;
    }
    if (periodType === 'MONTH') return now.toISOString().slice(0, 7);
    if (periodType === 'QUARTER') return `${now.getFullYear()}-Q${Math.floor(now.getMonth() / 3) + 1}`;
    if (periodType === 'HALFYEAR') return `${now.getFullYear()}-H${now.getMonth() < 6 ? '1' : '2'}`;
    if (periodType === 'YEAR') return String(now.getFullYear());
    return '';
};
const validatePeriodPair = (periodType: string, periodKey: string, allowBothBlank: boolean) => {
    if (!periodType && !periodKey) return allowBothBlank;
    if (!periodType || !periodKey) {
        toast.warning('Period Type and Period must be provided together.');
        return false;
    }
    return true;
};

const clearGridConfig = () => {
    setConfigRow(queryPageStore.gridConfig, import.meta.env.VITE_DEFAULT_ROW);
    setConfigPage(queryPageStore.gridConfig, 1);
    setConfigTotal(queryPageStore.gridConfig, 0);
};

const initGridConfig = () => getGridConfig('oid', [
    {
        method: (oid: string) => {
            const signal = dsList.value.find(item => item.oid === oid);
            if (signal?.snapshotOid) confirmFire('Regenerate signals for this KPI snapshot?', generateBySnapshot, signal.snapshotOid);
        },
        icon: 'arrow-repeat',
        type: 'regenerate',
        memo: 'Regenerate this KPI snapshot signal.',
        class: 'btn btn-outline-secondary btn-sm'
    }
], [
    { label: 'Actions', field: 'oid', textAlign: 'center', labTextAlign: 'center' },
    { label: 'KPI Code', field: 'sourceCode' },
    { label: 'KPI Name', field: 'sourceName' },
    { label: 'Signal Type', field: 'signalType' },
    { label: 'Period', field: 'periodKey' },
    { label: 'Status', field: 'statusCode', colMethod: statusHtml, colHtml: true, textAlign: 'center' },
    { label: 'Risk', field: 'riskLevel', colMethod: riskHtml, colHtml: true, textAlign: 'center' },
    { label: 'Lifecycle', field: 'signalStatus', colMethod: lifecycleHtml, colHtml: true, textAlign: 'center' },
    { label: 'Score', field: 'scoreValue', colMethod: numberText, textAlign: 'right' },
    { label: 'Target', field: 'targetValue', colMethod: numberText, textAlign: 'right' },
    { label: 'Actual', field: 'actualValue', colMethod: numberText, textAlign: 'right' },
    { label: 'Generated', field: 'generatedAt', colMethod: dateText }
]);

const btnQuery = async () => {
    showLoading();
    dsList.value = [];
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findPage', {
            field: {
                sourceType: 'KPI',
                sourceCodeLike: queryPageStore.queryParam.sourceCode,
                sourceNameLike: queryPageStore.queryParam.sourceName,
                signalType: queryPageStore.queryParam.signalType,
                periodType: queryPageStore.queryParam.periodType,
                periodKey: queryPageStore.queryParam.periodKey,
                statusCode: queryPageStore.queryParam.statusCode,
                riskLevel: queryPageStore.queryParam.riskLevel,
                signalStatus: queryPageStore.queryParam.signalStatus
            },
            pageOf: { select: queryPageStore.gridConfig.page, showRow: queryPageStore.gridConfig.row }
        });
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) {
            clearGridConfig();
            if (isSearchNoData(response.data?.message)) return;
            throw new Error(response.data?.message || 'Query failed');
        }
        dsList.value = response.data.value || [];
        setConfigTotal(queryPageStore.gridConfig, response.data.pageOf?.countSize || dsList.value.length);
    } catch (e: any) {
        clearGridConfig();
        toast.warning(escapeQifuHtmlMsg(e?.message || String(e)));
    } finally {
        hideLoading();
    }
};

const generateKpi = async () => {
    showLoading();
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/generateKpi', {
            kpiOid: queryPageStore.generationParam.kpiOid,
            periodType: queryPageStore.generationParam.periodType,
            periodKey: queryPageStore.generationParam.periodKey
        });
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) throw new Error(response.data?.message || 'Generation failed');
        const value = response.data.value || {};
        toast.success(`Generated from ${value.snapshotCount || 0} snapshots; inserted ${value.insertedCount || 0}, updated ${value.updatedCount || 0}.`);
        await btnQuery();
    } catch (e: any) {
        toast.warning(escapeQifuHtmlMsg(e?.message || String(e)));
    } finally {
        hideLoading();
    }
};

const generateBySnapshot = async (snapshotOid: string) => {
    showLoading();
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/generateKpiBySnapshot', { snapshotOid });
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) throw new Error(response.data?.message || 'Generation failed');
        toast.success('KPI snapshot signals regenerated.');
        await btnQuery();
    } catch (e: any) {
        toast.warning(escapeQifuHtmlMsg(e?.message || String(e)));
    } finally {
        hideLoading();
    }
};

const loadKpiList = async () => {
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findKpiList', {});
        if (response.data?.success === import.meta.env.VITE_SUCCESS_FLAG) kpiList.value = response.data.value || [];
    } catch (e: any) {
        toast.warning(escapeQifuHtmlMsg(e?.message || String(e)));
    }
};

const btnClear = () => {
    queryPageStore.clearData();
    dsList.value = [];
    clearGridConfig();
};
const changeGridRow = (row: number) => { setConfigRow(queryPageStore.gridConfig, row); queryPageStore.gridConfig.page = 1; btnQuery(); };
const changePage = (page: number) => { setConfigPage(queryPageStore.gridConfig, page); btnQuery(); };

watch(() => queryPageStore.queryParam.periodType, (value) => {
    queryPageStore.queryParam.periodKey = value ? defaultPeriodKey(value) : '';
});

watch(() => queryPageStore.generationParam.periodType, (value) => {
    queryPageStore.generationParam.periodKey = value ? defaultPeriodKey(value) : '';
});
onMounted(() => {
    const config = initGridConfig();
    if (queryPageStore.gridConfig.column) resetConfigByOld(config, queryPageStore.gridConfig);
    queryPageStore.gridConfig = config;
    loadKpiList();
    if (queryPageStore.gridConfig.total > 0) btnQuery();
});
</script>

<template>
  <Toolbar :progId="pageProgramId" description="Performance Signal" refreshFlag="Y" @refreshMethod="btnQuery"
           queryFieldShowSwitchFlag="Y" @queryFieldShowSwitcMethod="qFieldShow = !qFieldShow" />

  <HiddenQueryFieldAlertInfo :dataSource="dsList" :queryFieldShowFlag="qFieldShow" />

  <div v-show="qFieldShow" class="card mb-3">
    <div class="card-body">
      <div class="row g-3">
        <div class="col-md-2"><input class="form-control" placeholder="KPI code" v-model="queryPageStore.queryParam.sourceCode"></div>
        <div class="col-md-3"><input class="form-control" placeholder="KPI name" v-model="queryPageStore.queryParam.sourceName"></div>
        <div class="col-md-2"><select class="form-select" v-model="queryPageStore.queryParam.signalType"><option v-for="item in signalTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option></select></div>
        <div class="col-md-2"><select class="form-select" v-model="queryPageStore.queryParam.periodType"><option v-for="item in periodTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option></select></div>
        <div class="col-md-3"><PeriodPicker label="Period" :periodType="queryPageStore.queryParam.periodType" v-model="queryPageStore.queryParam.periodKey" /></div>
        <div class="col-md-1"><select class="form-select" v-model="queryPageStore.queryParam.signalStatus"><option value="">All</option><option value="OPEN">Open</option><option value="RESOLVED">Resolved</option></select></div>
        <div class="col-md-2"><select class="form-select" v-model="queryPageStore.queryParam.riskLevel"><option value="">All risks</option><option value="LOW">Low</option><option value="MEDIUM">Medium</option><option value="HIGH">High</option><option value="CRITICAL">Critical</option></select></div>
        <div class="col-md-2"><input class="form-control" placeholder="Status code" v-model="queryPageStore.queryParam.statusCode"></div>
        <div class="col-md-3 d-flex gap-2"><button class="btn btn-primary" @click="btnQuery"><i class="bi bi-search"></i> Query</button><button class="btn btn-outline-secondary" @click="btnClear"><i class="bi bi-eraser"></i></button></div>
      </div>
    </div>
  </div>

  <div class="card mb-3 border-primary-subtle">
    <div class="card-header">Generate KPI Signals</div>
    <div class="card-body">
      <div class="row g-3 align-items-center">
        <div class="col-md-4"><select class="form-select" v-model="queryPageStore.generationParam.kpiOid"><option value="">All KPIs</option><option v-for="item in kpiList" :key="item.oid" :value="item.oid">{{ item.kpiCode }} - {{ item.kpiName }}</option></select></div>
        <div class="col-md-2"><select class="form-select" v-model="queryPageStore.generationParam.periodType"><option v-for="item in periodTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option></select></div>
        <div class="col-md-3"><PeriodPicker label="Period" :periodType="queryPageStore.generationParam.periodType" v-model="queryPageStore.generationParam.periodKey" /></div>
        <div class="col-md-3"><button class="btn btn-primary" @click="confirmFire('Generate KPI signals from matching snapshots?', generateKpi, null)"><i class="bi bi-lightning-charge"></i> Generate</button></div>
      </div>
      <div class="form-text mt-2">Generation reads existing official KPI score snapshots. It does not recalculate KPI scores.</div>
    </div>
  </div>

  <GridPagination :progId="pageProgramId" :gridConfig="queryPageStore.gridConfig"
                  :changePageSelectMethod="changePage" :changeGridConfigRowMethod="changeGridRow" />
  <Grid :progId="pageProgramId" :dataSource="dsList" :config="queryPageStore.gridConfig" />
  <div v-if="dsList.length === 0" class="text-center text-muted py-3">No performance signals</div>
</template>