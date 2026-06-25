<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { toast } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { BarChart, GaugeChart, LineChart } from 'echarts/charts';
import {
    GridComponent,
    LegendComponent,
    TitleComponent,
    TooltipComponent
} from 'echarts/components';
import VChart from 'vue-echarts';

import Toolbar from '@/components/Toolbar.vue';
import Grid from '@/components/Grid.vue';
import GridPagination from '@/components/GridPagination.vue';
import HiddenQueryFieldAlertInfo from '@/components/HiddenQueryFieldAlertInfo.vue';
import PeriodPicker from '@/components/PeriodPicker.vue';
import { PageConstants } from './config';
import { getGridConfig, resetConfigByOld, setConfigPage, setConfigRow, setConfigTotal } from '../../components/GridHelper';
import { useMdProg005d0001Store } from './QueryPageStore';
import { escapeQifuHtmlMsg, getAxiosInstance } from '../../components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';
import { useActionSourceNavigation } from '@/composables/useActionSourceNavigation';
import { optionName, periodTypeOptions, withAllOption } from '@/types/MindScoreOptions';
import { countPeriodRange, defaultPeriodKey as getDefaultPeriodKey } from '@/types/Period';

definePageMeta({ middleware: ['auth'] });

use([
    CanvasRenderer,
    BarChart,
    GaugeChart,
    LineChart,
    GridComponent,
    LegendComponent,
    TitleComponent,
    TooltipComponent
]);

const queryPageStore = useMdProg005d0001Store();
const { showLoading, hideLoading } = useSwalLoading();
const { createActionFromSource } = useActionSourceNavigation();

const pageProgramId = ref(PageConstants.QueryId);
const dsList = ref<any[]>([]);
const kpiList = ref<any[]>([]);
const orgList = ref<any[]>([]);
const memberList = ref<any[]>([]);
const qFieldShow = ref(true);
const showTraceModal = ref(false);
const selectedTraceRow = ref<any>(null);
const summary = ref<any>({
    kpiCount : 0,
    avgScore : 0,
    goodCount : 0,
    warningCount : 0,
    badCount : 0,
    unknownCount : 0
});

const dataForTypeOptions = [
    { value: 'GLOBAL', label: 'Global' },
    { value: 'ORG', label: 'Organization' },
    { value: 'ACCOUNT', label: 'Account' }
];
const scoreStatusOptions = [
    { value: 'GOOD', label: 'Good' },
    { value: 'WARNING', label: 'Warning' },
    { value: 'BAD', label: 'Bad' },
    { value: 'UNKNOWN', label: 'Unknown' }
];
const dataForTypeQueryOptions = withAllOption(dataForTypeOptions);

const periodRangeLimits: Record<string, { max: number; unit: string }> = {
    DAY : { max: 365, unit: 'days' },
    WEEK : { max: 104, unit: 'weeks' },
    MONTH : { max: 32, unit: 'months' },
    QUARTER : { max: 12, unit: 'quarters' },
    HALFYEAR : { max: 12, unit: 'half-years' },
    YEAR : { max: 6, unit: 'years' }
};
const numberText = (value: any) => value === null || value === undefined || value === '' ? '' : Number(value).toLocaleString(undefined, { maximumFractionDigits: 4 });
const kpiName = (oid: string) => {
    const item = kpiList.value.find((kpi: any) => kpi.oid === oid);
    return item ? item.kpiCode + ' - ' + item.kpiName : oid;
};
const orgName = (oid: string) => {
    const item = orgList.value.find((org: any) => org.oid === oid);
    return item ? item.orgCode + ' - ' + item.orgName : oid;
};
const accountName = (account: string) => {
    const item = memberList.value.find((member: any) => member.account === account);
    return item ? item.account + (item.displayName ? ' - ' + item.displayName : '') : account;
};
const statusName = (value: string) => optionName(scoreStatusOptions, value || 'UNKNOWN');
const fallbackStatusColors: Record<string, { font: string; background: string }> = {
    GOOD: { font: '#ffffff', background: '#198754' },
    WARNING: { font: '#212529', background: '#ffc107' },
    BAD: { font: '#ffffff', background: '#dc3545' },
    UNKNOWN: { font: '#ffffff', background: '#6c757d' }
};
const safeCssColor = (value: any, fallback: string) => {
    const color = String(value || '').trim();
    if (!color) {
        return fallback;
    }
    if (typeof CSS !== 'undefined' && CSS.supports('color', color)) {
        return color;
    }
    return fallback;
};
const escapeHtml = (value: any) => String(value ?? '')
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#039;');
const scoreColor = (item: any) => {
    const fallback = fallbackStatusColors[item.scoreStatus] || fallbackStatusColors.UNKNOWN;
    return {
        font: safeCssColor(item.fontColor, fallback.font),
        background: safeCssColor(item.bgColor, fallback.background)
    };
};
const scoreBadgeHtml = (item: any, text: string) => {
    const color = scoreColor(item);
    const title = item.colorName ? ' title="' + escapeHtml(item.colorName) + '"' : '';
    return '<span class="badge score-color-badge" style="color:' + color.font
        + ';background-color:' + color.background + '"' + title + '>' + escapeHtml(text) + '</span>';
};
const formattedTrace = computed(() => {
    const trace = selectedTraceRow.value?.calculationTrace;
    if (!trace) {
        return '';
    }
    try {
        return JSON.stringify(JSON.parse(trace), null, 2);
    } catch {
        return String(trace);
    }
});
const openTrace = (oid: string) => {
    selectedTraceRow.value = dsList.value.find((item: any) => item.oid === oid) || null;
    showTraceModal.value = true;
};
const closeTrace = () => {
    showTraceModal.value = false;
    selectedTraceRow.value = null;
};
const createActionForKpi = async (snapshotOid: string) => {
    const row = dsList.value.find((item: any) => item.oid === snapshotOid);
    if (!row) {
        return;
    }
    if (!await createActionFromSource('KPI', row.kpiOid, row.kpiDisplay)) {
        toast.warning('You do not have permission to create an Action Plan.');
    }
};
const firstScore = computed(() => dsList.value.length > 0 ? dsList.value[0] : null);
const showOrgFilter = computed(() => queryPageStore.queryParam.dataForType === 'ORG');
const showAccountFilter = computed(() => queryPageStore.queryParam.dataForType === 'ACCOUNT');
const isPeriodRangeMode = computed(() => queryPageStore.queryParam.periodMode === 'RANGE');
const periodPickerType = computed(() => queryPageStore.queryParam.periodType);

const defaultPeriodKey = () => {
    return getDefaultPeriodKey(periodPickerType.value);
};
const periodRangeCount = (periodType: string, from: string, to: string) => {
    const limit = periodRangeLimits[periodType];
    return limit ? countPeriodRange(periodType, from, to, limit.max) : null;
};

const trendOption = reactive<any>({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '8%', containLabel: true },
    xAxis: { type: 'category', data: [] },
    yAxis: { type: 'value' },
    series: [{ name: 'Score', type: 'line', smooth: true, data: [], areaStyle: {} }]
});
const targetActualOption = reactive<any>({
    tooltip: { trigger: 'axis' },
    legend: { top: 0 },
    grid: { left: '3%', right: '4%', bottom: '8%', containLabel: true },
    xAxis: { type: 'category', data: [] },
    yAxis: { type: 'value' },
    series: [
        { name: 'Target', type: 'bar', data: [] },
        { name: 'Actual', type: 'bar', data: [] }
    ]
});
const gaugeOption = reactive<any>({
    series: [{
        type: 'gauge',
        min: 0,
        max: 100,
        progress: { show: true, width: 12 },
        axisLine: { lineStyle: { width: 12 } },
        axisTick: { show: false },
        splitLine: { length: 8, lineStyle: { width: 1 } },
        pointer: { width: 4 },
        detail: { valueAnimation: true, formatter: '{value}' },
        data: [{ value: 0, name: 'Score' }]
    }]
});

const normalizedQuery = () => {
    return {
        kpiOid : queryPageStore.queryParam.kpiOid,
        periodType : queryPageStore.queryParam.periodType,
        periodKey : isPeriodRangeMode.value ? '' : queryPageStore.queryParam.periodKey,
        periodKeyFrom : isPeriodRangeMode.value ? queryPageStore.queryParam.periodKeyFrom : '',
        periodKeyTo : isPeriodRangeMode.value ? queryPageStore.queryParam.periodKeyTo : '',
        dataForType : queryPageStore.queryParam.dataForType,
        account : queryPageStore.queryParam.dataForType === 'ACCOUNT' ? queryPageStore.queryParam.account : '',
        orgOid : queryPageStore.queryParam.dataForType === 'ORG' ? queryPageStore.queryParam.orgOid : ''
    };
};
const chartPayload = () => ({
    ...normalizedQuery(),
    limit : 24
});

const rowView = (item: any) => ({
    ...item,
    kpiDisplay : item.kpiCode ? item.kpiCode + ' - ' + item.kpiName : kpiName(item.kpiOid),
    formulaDisplay : item.formulaCode ? item.formulaCode + ' v' + (item.formulaVersionNo || '') + ' - ' + (item.formulaName || '') : '',
    aggrDisplay : item.aggrCode ? item.aggrCode + ' - ' + (item.aggrName || '') : '',
    ownerDisplay : item.ownerName || 'Global',
    scoreDisplay : numberText(item.scoreValue),
    scoreHtml : scoreBadgeHtml(item, numberText(item.scoreValue)),
    targetDisplay : numberText(item.rawTarget),
    actualDisplay : numberText(item.rawActual),
    statusDisplay : statusName(item.scoreStatus),
    statusHtml : scoreBadgeHtml(item, statusName(item.scoreStatus)),
    calculatedAtText : item.calculatedAt ? String(item.calculatedAt).replace('T', ' ').slice(0, 19) : ''
});

const initQueryGridConfig = () => getGridConfig(
    'oid',
    [
        { type: 'trace', method: openTrace, icon: 'card-list', class: 'btn btn-outline-primary btn-sm', memo: 'Calculation trace' },
        { type: 'action', method: createActionForKpi, icon: 'clipboard-plus', class: 'btn btn-outline-success btn-sm', memo: 'Create Action Plan' }
    ],
    [
        { label: 'KPI', field: 'kpiDisplay' },
        { label: 'Period', field: 'periodKey' },
        { label: 'Data For', field: 'dataForType' },
        { label: 'Owner', field: 'ownerDisplay' },
        { label: 'Formula', field: 'formulaDisplay' },
        { label: 'Aggregation', field: 'aggrDisplay' },
        { label: 'Target', field: 'targetDisplay' },
        { label: 'Actual', field: 'actualDisplay' },
        { label: 'Score', field: 'scoreHtml', colHtml: true, colMethod: (val: any) => val },
        { label: 'Status', field: 'statusHtml', colHtml: true, colMethod: (val: any) => val },
        { label: 'Calculated At', field: 'calculatedAtText' },
        { label: 'Actions', field: 'oid', textAlign: 'center' }
    ]
);

const loadKpiList = async () => {
    const axiosInstance = getAxiosInstance();
    const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findKpiList', {});
    if (response.data && import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
        kpiList.value = (response.data.value || []).filter((item: any) => item.enabled === 'Y');
    }
};
const loadOrgList = async () => {
    const axiosInstance = getAxiosInstance();
    const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findOrgList', {});
    if (response.data && import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
        orgList.value = (response.data.value || []).filter((item: any) => item.enabled === 'Y');
    }
};
const loadMemberList = async () => {
    const axiosInstance = getAxiosInstance();
    const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findMemberList', {});
    if (response.data && import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
        const seen: Record<string, boolean> = {};
        memberList.value = (response.data.value || []).filter((item: any) => {
            if (!item.account || seen[item.account]) {
                return false;
            }
            seen[item.account] = true;
            return true;
        });
    }
};

const changeQueryGridRow = (row: number) => {
    setConfigRow(queryPageStore.gridConfig, row);
    queryPageStore.gridConfig.page = 1;
    btnQuery();
};
const changePageSelect = (page: number) => {
    setConfigPage(queryPageStore.gridConfig, page);
    btnQuery();
};
const clearGridConfig = () => {
    setConfigRow(queryPageStore.gridConfig, import.meta.env.VITE_DEFAULT_ROW);
    setConfigPage(queryPageStore.gridConfig, 1);
    setConfigTotal(queryPageStore.gridConfig, 0);
};
const tbRefresh = () => btnQuery();
const tbQueryFieldShow = () => qFieldShow.value = !qFieldShow.value;
const btnClear = () => {
    closeTrace();
    queryPageStore.clearData();
    dsList.value = [];
    summary.value = { kpiCount : 0, avgScore : 0, goodCount : 0, warningCount : 0, badCount : 0, unknownCount : 0 };
    trendOption.xAxis.data = [];
    trendOption.series[0].data = [];
    targetActualOption.xAxis.data = [];
    targetActualOption.series[0].data = [];
    targetActualOption.series[1].data = [];
    gaugeOption.series[0].data[0].value = 0;
    clearGridConfig();
};

const btnQuery = async () => {
    if (!queryPageStore.queryParam.periodType) {
        toast.warning('Please select period type.');
        return;
    }
    if (isPeriodRangeMode.value && (!queryPageStore.queryParam.periodKeyFrom || !queryPageStore.queryParam.periodKeyTo)) {
        toast.warning('Period From and Period To must be entered together.');
        return;
    }
    if (isPeriodRangeMode.value) {
        const count = periodRangeCount(queryPageStore.queryParam.periodType, queryPageStore.queryParam.periodKeyFrom, queryPageStore.queryParam.periodKeyTo);
        if (count === null) {
            toast.warning('Invalid period range.');
            return;
        }
        if (count === 0) {
            toast.warning('Period From must be earlier than or equal to Period To.');
            return;
        }
        const limit = periodRangeLimits[queryPageStore.queryParam.periodType];
        if (limit && count > limit.max) {
            toast.warning('Period range cannot exceed ' + limit.max + ' ' + limit.unit + '.');
            return;
        }
    }
    showLoading();
    dsList.value = [];
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/reportQuery', {
            field: normalizedQuery(),
            pageOf: {
                select : queryPageStore.gridConfig.page,
                showRow : queryPageStore.gridConfig.row
            }
        });
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                clearGridConfig();
                toast.warning(escapeQifuHtmlMsg(response.data.message));
            } else {
                dsList.value = (response.data.value || []).map(rowView);
                setConfigTotal(queryPageStore.gridConfig, response.data.pageOf.countSize);
                updateGauge();
            }
        }
        await Promise.all([loadSummary(), loadTrend(), loadTargetActual()]);
    } catch (e: any) {
        clearGridConfig();
        alert(e);
    } finally {
        hideLoading();
    }
};

const loadSummary = async () => {
    const axiosInstance = getAxiosInstance();
    const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/summary', chartPayload());
        if (response.data && import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
            summary.value = response.data.value || summary.value;
        } else if (response.data && response.data.message) {
            toast.warning(escapeQifuHtmlMsg(response.data.message));
        }
};
const loadTrend = async () => {
    const axiosInstance = getAxiosInstance();
    const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/trend', chartPayload());
    if (response.data && import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
        const rows = response.data.value || [];
        trendOption.xAxis.data = rows.map((item: any) => item.periodKey);
        trendOption.series[0].data = rows.map((item: any) => Number(item.scoreValue || 0));
    } else if (response.data && response.data.message) {
        toast.warning(escapeQifuHtmlMsg(response.data.message));
    }
};
const loadTargetActual = async () => {
    const axiosInstance = getAxiosInstance();
    const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/targetActual', chartPayload());
    if (response.data && import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
        const rows = response.data.value || [];
        targetActualOption.xAxis.data = rows.map((item: any) => item.periodKey);
        targetActualOption.series[0].data = rows.map((item: any) => Number(item.rawTarget || 0));
        targetActualOption.series[1].data = rows.map((item: any) => Number(item.rawActual || 0));
    } else if (response.data && response.data.message) {
        toast.warning(escapeQifuHtmlMsg(response.data.message));
    }
};
const updateGauge = () => {
    gaugeOption.series[0].data[0].value = firstScore.value ? Number(firstScore.value.scoreValue || 0) : Number(summary.value.avgScore || 0);
    const color = firstScore.value ? scoreColor(firstScore.value).background : fallbackStatusColors.UNKNOWN.background;
    gaugeOption.series[0].itemStyle = { color };
};

onMounted(async () => {
    try {
        await Promise.all([loadKpiList(), loadOrgList(), loadMemberList()]);
    } catch (e: any) {
        toast.warning(e?.message || e);
    }
    const newGridConfig = initQueryGridConfig();
    if (queryPageStore.gridConfig.column) {
        resetConfigByOld(newGridConfig, queryPageStore.gridConfig);
    }
    queryPageStore.gridConfig = newGridConfig;
    btnQuery();
});

watch(() => queryPageStore.queryParam.dataForType, (value) => {
    if (value !== 'ACCOUNT') {
        queryPageStore.queryParam.account = '';
    }
    if (value !== 'ORG') {
        queryPageStore.queryParam.orgOid = '';
    }
});

watch(periodPickerType, (value) => {
    queryPageStore.queryParam.periodKeyFrom = '';
    queryPageStore.queryParam.periodKeyTo = '';
    queryPageStore.queryParam.periodKey = value ? defaultPeriodKey() : '';
});

watch(() => queryPageStore.queryParam.periodMode, (value) => {
    if (value === 'RANGE') {
        queryPageStore.queryParam.periodKey = '';
        if (!queryPageStore.queryParam.periodKeyFrom) {
            queryPageStore.queryParam.periodKeyFrom = defaultPeriodKey();
        }
        if (!queryPageStore.queryParam.periodKeyTo) {
            queryPageStore.queryParam.periodKeyTo = defaultPeriodKey();
        }
        return;
    }
    queryPageStore.queryParam.periodKeyFrom = '';
    queryPageStore.queryParam.periodKeyTo = '';
    if (!queryPageStore.queryParam.periodKey) {
        queryPageStore.queryParam.periodKey = defaultPeriodKey();
    }
});
</script>

<template>
<div class="row">
  <div class="col-12">
    <Toolbar
        :progId="pageProgramId"
        description="KPI Report"
        refreshFlag="Y"
        @refreshMethod="tbRefresh"
        queryFieldShowSwitchFlag="Y"
        @queryFieldShowSwitcMethod="tbQueryFieldShow"
    />
  </div>
</div>

<HiddenQueryFieldAlertInfo :dataSource="dsList" :queryFieldShowFlag="qFieldShow" />

<div v-show="qFieldShow" class="card mb-4">
  <div class="card-body">
    <div class="row g-3">
      <div class="col-md-4">
        <div class="form-group form-floating">
          <select class="form-select" id="queryKpiOid" v-model="queryPageStore.queryParam.kpiOid">
            <option value="">All</option>
            <option v-for="item in kpiList" :key="item.oid" :value="item.oid">{{ item.kpiCode }} - {{ item.kpiName }}</option>
          </select>
          <label for="queryKpiOid">KPI</label>
        </div>
      </div>
      <div class="col-md-2">
        <div class="form-group form-floating">
          <select class="form-select" id="queryPeriodType" v-model="queryPageStore.queryParam.periodType">
            <option v-for="item in periodTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
          </select>
          <label for="queryPeriodType">Period Type</label>
        </div>
      </div>
      <div class="col-md-3">
        <label class="form-label">Period Mode</label>
        <div class="btn-group w-100" role="group" aria-label="Period Mode">
          <input type="radio" class="btn-check" id="periodModeSingle" value="SINGLE" v-model="queryPageStore.queryParam.periodMode">
          <label class="btn btn-outline-secondary" for="periodModeSingle">Single</label>
          <input type="radio" class="btn-check" id="periodModeRange" value="RANGE" v-model="queryPageStore.queryParam.periodMode">
          <label class="btn btn-outline-secondary" for="periodModeRange">Range</label>
        </div>
      </div>
      <div v-if="!isPeriodRangeMode" class="col-lg-4 col-md-6">
        <PeriodPicker label="Period" :periodType="queryPageStore.queryParam.periodType" v-model="queryPageStore.queryParam.periodKey" />
      </div>
      <div v-if="isPeriodRangeMode" class="col-lg-4 col-md-6">
        <PeriodPicker label="Period From" :periodType="queryPageStore.queryParam.periodType" v-model="queryPageStore.queryParam.periodKeyFrom" />
      </div>
      <div v-if="isPeriodRangeMode" class="col-lg-4 col-md-6">
        <PeriodPicker label="Period To" :periodType="queryPageStore.queryParam.periodType" v-model="queryPageStore.queryParam.periodKeyTo" />
      </div>
      <div class="col-md-3">
        <div class="form-group form-floating">
          <select class="form-select" id="queryDataForType" v-model="queryPageStore.queryParam.dataForType">
            <option v-for="item in dataForTypeQueryOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
          </select>
          <label for="queryDataForType">Data For</label>
        </div>
      </div>
      <div v-if="showAccountFilter" class="col-md-3">
        <div class="form-group form-floating">
          <select class="form-select" id="queryAccount" v-model="queryPageStore.queryParam.account">
            <option value="">All</option>
            <option v-for="item in memberList" :key="item.account" :value="item.account">{{ accountName(item.account) }}</option>
          </select>
          <label for="queryAccount">Account</label>
        </div>
      </div>
      <div v-if="showOrgFilter" class="col-md-3">
        <div class="form-group form-floating">
          <select class="form-select" id="queryOrgOid" v-model="queryPageStore.queryParam.orgOid">
            <option value="">All</option>
            <option v-for="item in orgList" :key="item.oid" :value="item.oid">{{ orgName(item.oid) }}</option>
          </select>
          <label for="queryOrgOid">Organization</label>
        </div>
      </div>
      <div class="col-md-3 d-flex gap-2 align-items-end">
        <button type="button" class="btn btn-primary flex-fill" @click="btnQuery"><i class="bi bi-search"></i> Query</button>
        <button type="button" class="btn btn-outline-secondary flex-fill" @click="btnClear"><i class="bi bi-eraser"></i> Clear</button>
      </div>
    </div>
  </div>
</div>

<div class="row g-3 mb-4">
  <div class="col-md-2">
    <div class="kpi-stat">
      <div class="kpi-stat-label">KPI Count</div>
      <div class="kpi-stat-value">{{ summary.kpiCount }}</div>
    </div>
  </div>
  <div class="col-md-2">
    <div class="kpi-stat">
      <div class="kpi-stat-label">Average Score</div>
      <div class="kpi-stat-value">{{ numberText(summary.avgScore) }}</div>
    </div>
  </div>
  <div class="col-md-2">
    <div class="kpi-stat good">
      <div class="kpi-stat-label">Good</div>
      <div class="kpi-stat-value">{{ summary.goodCount }}</div>
    </div>
  </div>
  <div class="col-md-2">
    <div class="kpi-stat warning">
      <div class="kpi-stat-label">Warning</div>
      <div class="kpi-stat-value">{{ summary.warningCount }}</div>
    </div>
  </div>
  <div class="col-md-2">
    <div class="kpi-stat bad">
      <div class="kpi-stat-label">Bad</div>
      <div class="kpi-stat-value">{{ summary.badCount }}</div>
    </div>
  </div>
  <div class="col-md-2">
    <div class="kpi-stat">
      <div class="kpi-stat-label">Unknown</div>
      <div class="kpi-stat-value">{{ summary.unknownCount }}</div>
    </div>
  </div>
</div>

<div class="row g-3 mb-4">
  <div class="col-lg-4">
    <div class="report-panel">
      <div class="report-panel-title">Score Gauge</div>
      <v-chart class="chart-box" :option="gaugeOption" autoresize />
    </div>
  </div>
  <div class="col-lg-8">
    <div class="report-panel">
      <div class="report-panel-title">Trend</div>
      <v-chart class="chart-box" :option="trendOption" autoresize />
    </div>
  </div>
  <div class="col-lg-12">
    <div class="report-panel">
      <div class="report-panel-title">Target vs Actual</div>
      <v-chart class="chart-wide" :option="targetActualOption" autoresize />
    </div>
  </div>
</div>

<div class="row">
  <div class="col-12">
    <GridPagination
        :progId="pageProgramId"
        :gridConfig="queryPageStore.gridConfig"
        :changePageSelectMethod="changePageSelect"
        :changeGridConfigRowMethod="changeQueryGridRow"
    />
    <Grid :progId="pageProgramId" :dataSource="dsList" :config="queryPageStore.gridConfig" />
  </div>
</div>

<div v-if="showTraceModal" class="modal-overlay" @click.self="closeTrace">
  <div class="trace-modal" role="dialog" aria-modal="true" aria-labelledby="traceModalTitle">
    <div class="modal-header">
      <div>
        <h5 id="traceModalTitle" class="modal-title">KPI Calculation Trace</h5>
        <div v-if="selectedTraceRow" class="small text-muted mt-1">
          {{ selectedTraceRow.kpiDisplay }} / {{ selectedTraceRow.periodKey }} / {{ selectedTraceRow.ownerDisplay }}
        </div>
      </div>
      <button type="button" class="btn-close" aria-label="Close" @click="closeTrace"></button>
    </div>
    <div v-if="selectedTraceRow" class="modal-body">
      <div class="row g-3 mb-3">
        <div class="col-md-6"><span class="trace-label">Formula</span>{{ selectedTraceRow.formulaDisplay || '-' }}</div>
        <div class="col-md-6"><span class="trace-label">Aggregation</span>{{ selectedTraceRow.aggrDisplay || '-' }}</div>
        <div class="col-md-3"><span class="trace-label">Target</span>{{ selectedTraceRow.targetDisplay || '-' }}</div>
        <div class="col-md-3"><span class="trace-label">Actual</span>{{ selectedTraceRow.actualDisplay || '-' }}</div>
        <div class="col-md-3"><span class="trace-label">Score</span><span v-html="selectedTraceRow.scoreHtml"></span></div>
        <div class="col-md-3"><span class="trace-label">Color Rule</span>{{ selectedTraceRow.colorName || '-' }}</div>
      </div>
      <div class="trace-label">Calculation Trace</div>
      <pre v-if="formattedTrace" class="trace-content"><code>{{ formattedTrace }}</code></pre>
      <div v-else class="alert alert-secondary mb-0">No calculation trace is available for this score snapshot.</div>
    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-secondary" @click="closeTrace">Close</button>
    </div>
  </div>
</div>
</template>

<style scoped>
.kpi-stat {
    border: 1px solid #d8dee6;
    border-left: 4px solid #6c757d;
    border-radius: 6px;
    padding: 12px 14px;
    background: #ffffff;
    min-height: 82px;
}
.modal-overlay {
    position: fixed;
    inset: 0;
    z-index: 1050;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 1rem;
    background: rgba(0, 0, 0, 0.5);
}
.trace-modal {
    width: min(900px, 100%);
    max-height: 90vh;
    overflow-y: auto;
    border-radius: 0.5rem;
    background: #ffffff;
    box-shadow: 0 0.5rem 1.5rem rgba(0, 0, 0, 0.25);
}
.modal-header,
.modal-footer {
    padding: 1rem 1.25rem;
}
.modal-body {
    padding: 1.25rem;
}
.trace-label {
    display: block;
    margin-bottom: 0.25rem;
    color: #6c757d;
    font-size: 0.8rem;
    font-weight: 600;
    text-transform: uppercase;
}
.trace-content {
    max-height: 420px;
    margin: 0;
    overflow: auto;
    border: 1px solid #dee2e6;
    border-radius: 0.375rem;
    padding: 1rem;
    background: #f8f9fa;
    white-space: pre-wrap;
    word-break: break-word;
}
:deep(.score-color-badge) {
    min-width: 4.5rem;
}
.kpi-stat.good {
    border-left-color: #198754;
}
.kpi-stat.warning {
    border-left-color: #ffc107;
}
.kpi-stat.bad {
    border-left-color: #dc3545;
}
.kpi-stat-label {
    color: #5b6776;
    font-size: 0.82rem;
}
.kpi-stat-value {
    font-size: 1.45rem;
    font-weight: 600;
    line-height: 1.8;
}
.report-panel {
    border: 1px solid #d8dee6;
    border-radius: 6px;
    background: #ffffff;
    padding: 12px;
}
.report-panel-title {
    font-weight: 600;
    margin-bottom: 8px;
}
.chart-box {
    width: 100%;
    height: 280px;
}
.chart-wide {
    width: 100%;
    height: 320px;
}
</style>
