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
import { PageConstants } from './config';
import { getGridConfig, resetConfigByOld, setConfigPage, setConfigRow, setConfigTotal } from '../../components/GridHelper';
import { useMdProg005d0001Store } from './QueryPageStore';
import { escapeQifuHtmlMsg, getAxiosInstance } from '../../components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';
import { optionName, periodTypeOptions, withAllOption } from '@/types/MindScoreOptions';

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

const pageProgramId = ref(PageConstants.QueryId);
const dsList = ref<any[]>([]);
const kpiList = ref<any[]>([]);
const orgList = ref<any[]>([]);
const memberList = ref<any[]>([]);
const qFieldShow = ref(true);
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
const periodTypeQueryOptions = withAllOption(periodTypeOptions);
const dataForTypeQueryOptions = withAllOption(dataForTypeOptions);

type PeriodField = 'periodKey' | 'periodKeyFrom' | 'periodKeyTo';

const periodFieldLabels: Record<PeriodField, string> = {
    periodKey : 'Period',
    periodKeyFrom : 'Period From',
    periodKeyTo : 'Period To'
};
const quarterOptions = [
    { value: '1', label: 'Q1' },
    { value: '2', label: 'Q2' },
    { value: '3', label: 'Q3' },
    { value: '4', label: 'Q4' }
];
const halfYearOptions = [
    { value: '1', label: 'H1' },
    { value: '2', label: 'H2' }
];
const pad2 = (value: number) => String(value).padStart(2, '0');
const currentDateValue = () => new Date().toISOString().slice(0, 10);
const currentMonthValue = () => new Date().toISOString().slice(0, 7);
const yearOptions = computed(() => {
    const currentYear = new Date().getFullYear();
    const years = [];
    for (let year = currentYear + 5; year >= currentYear - 10; year--) {
        years.push(String(year));
    }
    return years;
});
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
const statusClass = (value: string) => {
    if (value === 'GOOD') return 'text-bg-success';
    if (value === 'WARNING') return 'text-bg-warning';
    if (value === 'BAD') return 'text-bg-danger';
    return 'text-bg-secondary';
};
const firstScore = computed(() => dsList.value.length > 0 ? dsList.value[0] : null);
const showOrgFilter = computed(() => queryPageStore.queryParam.dataForType === 'ORG');
const showAccountFilter = computed(() => queryPageStore.queryParam.dataForType === 'ACCOUNT');
const hasPeriodRange = computed(() => !!queryPageStore.queryParam.periodKeyFrom && !!queryPageStore.queryParam.periodKeyTo);
const periodPickerType = computed(() => queryPageStore.queryParam.periodType);

const weekOfYear = (date: Date) => {
    const d = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
    d.setUTCDate(d.getUTCDate() + 4 - (d.getUTCDay() || 7));
    const yearStart = new Date(Date.UTC(d.getUTCFullYear(), 0, 1));
    return {
        year : d.getUTCFullYear(),
        week : Math.ceil((((d.getTime() - yearStart.getTime()) / 86400000) + 1) / 7)
    };
};
const dateFromIsoWeek = (periodKey: string) => {
    const match = /^(\d{4})-W(\d{2})$/.exec(periodKey || '');
    if (!match) {
        return currentDateValue();
    }
    const year = Number(match[1]);
    const week = Number(match[2]);
    const simple = new Date(Date.UTC(year, 0, 4));
    const day = simple.getUTCDay() || 7;
    simple.setUTCDate(simple.getUTCDate() - day + 1 + (week - 1) * 7);
    return simple.toISOString().slice(0, 10);
};
const periodInputValue = (field: PeriodField) => {
    const key = queryPageStore.queryParam[field] || '';
    if (periodPickerType.value === 'DAY') {
        return /^\d{4}-\d{2}-\d{2}$/.test(key) ? key : '';
    }
    if (periodPickerType.value === 'WEEK') {
        return key ? dateFromIsoWeek(key) : '';
    }
    if (periodPickerType.value === 'MONTH') {
        return /^\d{4}-\d{2}$/.test(key) ? key : '';
    }
    if (periodPickerType.value === 'YEAR') {
        return /^\d{4}$/.test(key) ? key : '';
    }
    return key;
};
const periodYearValue = (field: PeriodField) => {
    const key = queryPageStore.queryParam[field] || '';
    const match = /^(\d{4})-[QH]([1-4])$/.exec(key);
    return match ? match[1] : '';
};
const periodPartValue = (field: PeriodField) => {
    const key = queryPageStore.queryParam[field] || '';
    const match = /^(\d{4})-[QH]([1-4])$/.exec(key);
    return match ? match[2] : '';
};
const setPeriodKey = (field: PeriodField, value: string) => {
    queryPageStore.queryParam[field] = value;
};
const eventValue = (event: Event) => (event.target as HTMLInputElement | HTMLSelectElement).value;
const updatePeriodFromInput = (field: PeriodField, value: string) => {
    if (!value) {
        setPeriodKey(field, '');
        return;
    }
    if (periodPickerType.value === 'WEEK') {
        const info = weekOfYear(new Date(value + 'T00:00:00'));
        setPeriodKey(field, info.year + '-W' + pad2(info.week));
        return;
    }
    setPeriodKey(field, value);
};
const updateStructuredPeriod = (field: PeriodField, part: 'year' | 'period', value: string) => {
    const year = part === 'year' ? value : (periodYearValue(field) || String(new Date().getFullYear()));
    const period = part === 'period' ? value : periodPartValue(field);
    if (!year || !period) {
        setPeriodKey(field, '');
        return;
    }
    setPeriodKey(field, year + (periodPickerType.value === 'QUARTER' ? '-Q' : '-H') + period);
};
const defaultPeriodKey = () => {
    const now = new Date();
    const dateValue = currentDateValue();
    if (periodPickerType.value === 'DAY') {
        return dateValue;
    }
    if (periodPickerType.value === 'WEEK') {
        const info = weekOfYear(now);
        return info.year + '-W' + pad2(info.week);
    }
    if (periodPickerType.value === 'MONTH') {
        return currentMonthValue();
    }
    if (periodPickerType.value === 'QUARTER') {
        return now.getFullYear() + '-Q' + Math.floor(now.getMonth() / 3 + 1);
    }
    if (periodPickerType.value === 'HALFYEAR') {
        return now.getFullYear() + '-H' + (now.getMonth() < 6 ? '1' : '2');
    }
    if (periodPickerType.value === 'YEAR') {
        return String(now.getFullYear());
    }
    return '';
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
        periodKey : hasPeriodRange.value ? '' : queryPageStore.queryParam.periodKey,
        periodKeyFrom : queryPageStore.queryParam.periodKeyFrom,
        periodKeyTo : queryPageStore.queryParam.periodKeyTo,
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
    targetDisplay : numberText(item.rawTarget),
    actualDisplay : numberText(item.rawActual),
    statusDisplay : statusName(item.scoreStatus),
    statusHtml : '<span class="badge ' + statusClass(item.scoreStatus) + '">' + statusName(item.scoreStatus) + '</span>',
    calculatedAtText : item.calculatedAt ? String(item.calculatedAt).replace('T', ' ').slice(0, 19) : ''
});

const initQueryGridConfig = () => getGridConfig(
    'oid',
    [],
    [
        { label: 'KPI', field: 'kpiDisplay' },
        { label: 'Period', field: 'periodKey' },
        { label: 'Data For', field: 'dataForType' },
        { label: 'Owner', field: 'ownerDisplay' },
        { label: 'Formula', field: 'formulaDisplay' },
        { label: 'Aggregation', field: 'aggrDisplay' },
        { label: 'Target', field: 'targetDisplay' },
        { label: 'Actual', field: 'actualDisplay' },
        { label: 'Score', field: 'scoreDisplay' },
        { label: 'Status', field: 'statusHtml', colHtml: true, colMethod: (val: any) => val },
        { label: 'Calculated At', field: 'calculatedAtText' }
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
    if (!!queryPageStore.queryParam.periodKeyFrom !== !!queryPageStore.queryParam.periodKeyTo) {
        toast.warning('Trend From and Trend To must be entered together.');
        return;
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

watch(hasPeriodRange, (value) => {
    if (value) {
        queryPageStore.queryParam.periodKey = '';
    }
});

watch(periodPickerType, (value) => {
    queryPageStore.queryParam.periodKeyFrom = '';
    queryPageStore.queryParam.periodKeyTo = '';
    queryPageStore.queryParam.periodKey = value ? defaultPeriodKey() : '';
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
            <option v-for="item in periodTypeQueryOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
          </select>
          <label for="queryPeriodType">Period Type</label>
        </div>
      </div>
      <div class="col-md-2">
        <div v-if="periodPickerType === 'DAY'" class="form-group form-floating">
          <input type="date" class="form-control" id="queryPeriodKey" :value="periodInputValue('periodKey')" @input="updatePeriodFromInput('periodKey', eventValue($event))">
          <label for="queryPeriodKey">{{ periodFieldLabels.periodKey }}</label>
        </div>
        <div v-else-if="periodPickerType === 'WEEK'" class="form-group form-floating">
          <input type="date" class="form-control" id="queryPeriodKey" :value="periodInputValue('periodKey')" @input="updatePeriodFromInput('periodKey', eventValue($event))">
          <label for="queryPeriodKey">{{ periodFieldLabels.periodKey }}</label>
        </div>
        <div v-else-if="periodPickerType === 'MONTH'" class="form-group form-floating">
          <input type="month" class="form-control" id="queryPeriodKey" :value="periodInputValue('periodKey')" @input="updatePeriodFromInput('periodKey', eventValue($event))">
          <label for="queryPeriodKey">{{ periodFieldLabels.periodKey }}</label>
        </div>
        <div v-else-if="periodPickerType === 'QUARTER' || periodPickerType === 'HALFYEAR'" class="form-group">
          <label class="form-label" for="queryPeriodKeyYear">{{ periodFieldLabels.periodKey }}</label>
          <div class="input-group">
            <select class="form-select" id="queryPeriodKeyYear" :value="periodYearValue('periodKey')" @change="updateStructuredPeriod('periodKey', 'year', eventValue($event))">
              <option value=""></option>
              <option v-for="year in yearOptions" :key="year" :value="year">{{ year }}</option>
            </select>
            <select class="form-select" :value="periodPartValue('periodKey')" @change="updateStructuredPeriod('periodKey', 'period', eventValue($event))">
              <option value=""></option>
              <option v-for="item in periodPickerType === 'QUARTER' ? quarterOptions : halfYearOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
            </select>
          </div>
        </div>
        <div v-else-if="periodPickerType === 'YEAR'" class="form-group form-floating">
          <select class="form-select" id="queryPeriodKey" :value="periodInputValue('periodKey')" @change="updatePeriodFromInput('periodKey', eventValue($event))">
            <option value=""></option>
            <option v-for="year in yearOptions" :key="year" :value="year">{{ year }}</option>
          </select>
          <label for="queryPeriodKey">{{ periodFieldLabels.periodKey }}</label>
        </div>
        <div v-else class="form-group form-floating">
          <input type="text" class="form-control" id="queryPeriodKey" disabled>
          <label for="queryPeriodKey">{{ periodFieldLabels.periodKey }}</label>
        </div>
      </div>
      <div class="col-md-2">
        <div v-if="periodPickerType === 'DAY'" class="form-group form-floating">
          <input type="date" class="form-control" id="periodKeyFrom" :value="periodInputValue('periodKeyFrom')" @input="updatePeriodFromInput('periodKeyFrom', eventValue($event))">
          <label for="periodKeyFrom">{{ periodFieldLabels.periodKeyFrom }}</label>
        </div>
        <div v-else-if="periodPickerType === 'WEEK'" class="form-group form-floating">
          <input type="date" class="form-control" id="periodKeyFrom" :value="periodInputValue('periodKeyFrom')" @input="updatePeriodFromInput('periodKeyFrom', eventValue($event))">
          <label for="periodKeyFrom">{{ periodFieldLabels.periodKeyFrom }}</label>
        </div>
        <div v-else-if="periodPickerType === 'MONTH'" class="form-group form-floating">
          <input type="month" class="form-control" id="periodKeyFrom" :value="periodInputValue('periodKeyFrom')" @input="updatePeriodFromInput('periodKeyFrom', eventValue($event))">
          <label for="periodKeyFrom">{{ periodFieldLabels.periodKeyFrom }}</label>
        </div>
        <div v-else-if="periodPickerType === 'QUARTER' || periodPickerType === 'HALFYEAR'" class="form-group">
          <label class="form-label" for="periodKeyFromYear">{{ periodFieldLabels.periodKeyFrom }}</label>
          <div class="input-group">
            <select class="form-select" id="periodKeyFromYear" :value="periodYearValue('periodKeyFrom')" @change="updateStructuredPeriod('periodKeyFrom', 'year', eventValue($event))">
              <option value=""></option>
              <option v-for="year in yearOptions" :key="year" :value="year">{{ year }}</option>
            </select>
            <select class="form-select" :value="periodPartValue('periodKeyFrom')" @change="updateStructuredPeriod('periodKeyFrom', 'period', eventValue($event))">
              <option value=""></option>
              <option v-for="item in periodPickerType === 'QUARTER' ? quarterOptions : halfYearOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
            </select>
          </div>
        </div>
        <div v-else-if="periodPickerType === 'YEAR'" class="form-group form-floating">
          <select class="form-select" id="periodKeyFrom" :value="periodInputValue('periodKeyFrom')" @change="updatePeriodFromInput('periodKeyFrom', eventValue($event))">
            <option value=""></option>
            <option v-for="year in yearOptions" :key="year" :value="year">{{ year }}</option>
          </select>
          <label for="periodKeyFrom">{{ periodFieldLabels.periodKeyFrom }}</label>
        </div>
        <div v-else class="form-group form-floating">
          <input type="text" class="form-control" id="periodKeyFrom" disabled>
          <label for="periodKeyFrom">{{ periodFieldLabels.periodKeyFrom }}</label>
        </div>
      </div>
      <div class="col-md-2">
        <div v-if="periodPickerType === 'DAY'" class="form-group form-floating">
          <input type="date" class="form-control" id="periodKeyTo" :value="periodInputValue('periodKeyTo')" @input="updatePeriodFromInput('periodKeyTo', eventValue($event))">
          <label for="periodKeyTo">{{ periodFieldLabels.periodKeyTo }}</label>
        </div>
        <div v-else-if="periodPickerType === 'WEEK'" class="form-group form-floating">
          <input type="date" class="form-control" id="periodKeyTo" :value="periodInputValue('periodKeyTo')" @input="updatePeriodFromInput('periodKeyTo', eventValue($event))">
          <label for="periodKeyTo">{{ periodFieldLabels.periodKeyTo }}</label>
        </div>
        <div v-else-if="periodPickerType === 'MONTH'" class="form-group form-floating">
          <input type="month" class="form-control" id="periodKeyTo" :value="periodInputValue('periodKeyTo')" @input="updatePeriodFromInput('periodKeyTo', eventValue($event))">
          <label for="periodKeyTo">{{ periodFieldLabels.periodKeyTo }}</label>
        </div>
        <div v-else-if="periodPickerType === 'QUARTER' || periodPickerType === 'HALFYEAR'" class="form-group">
          <label class="form-label" for="periodKeyToYear">{{ periodFieldLabels.periodKeyTo }}</label>
          <div class="input-group">
            <select class="form-select" id="periodKeyToYear" :value="periodYearValue('periodKeyTo')" @change="updateStructuredPeriod('periodKeyTo', 'year', eventValue($event))">
              <option value=""></option>
              <option v-for="year in yearOptions" :key="year" :value="year">{{ year }}</option>
            </select>
            <select class="form-select" :value="periodPartValue('periodKeyTo')" @change="updateStructuredPeriod('periodKeyTo', 'period', eventValue($event))">
              <option value=""></option>
              <option v-for="item in periodPickerType === 'QUARTER' ? quarterOptions : halfYearOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
            </select>
          </div>
        </div>
        <div v-else-if="periodPickerType === 'YEAR'" class="form-group form-floating">
          <select class="form-select" id="periodKeyTo" :value="periodInputValue('periodKeyTo')" @change="updatePeriodFromInput('periodKeyTo', eventValue($event))">
            <option value=""></option>
            <option v-for="year in yearOptions" :key="year" :value="year">{{ year }}</option>
          </select>
          <label for="periodKeyTo">{{ periodFieldLabels.periodKeyTo }}</label>
        </div>
        <div v-else class="form-group form-floating">
          <input type="text" class="form-control" id="periodKeyTo" disabled>
          <label for="periodKeyTo">{{ periodFieldLabels.periodKeyTo }}</label>
        </div>
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
