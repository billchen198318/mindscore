<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { toast } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

import Toolbar from '@/components/Toolbar.vue';
import Grid from '@/components/Grid.vue';
import GridPagination from '@/components/GridPagination.vue';
import HiddenQueryFieldAlertInfo from '@/components/HiddenQueryFieldAlertInfo.vue';
import { PageConstants } from './config';
import { getGridConfig, setConfigRow, setConfigPage, setConfigTotal, resetConfigByOld } from '../../components/GridHelper';
import { useMdProg004d0001Store } from './QueryPageStore';
import {
    getAxiosInstance,
    invalidFeedback,
    checkInvalid,
    escapeQifuHtmlMsg
} from '../../components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';
import {
    periodTypeOptions,
    yesNoOptions,
    optionName,
    yesNoName,
    withAllOption
} from '@/types/MindScoreOptions';

definePageMeta({ middleware: ['auth'] });

const queryPageStore = useMdProg004d0001Store();
const { showLoading, hideLoading, confirmFire } = useSwalLoading();

const pageProgramId = ref(PageConstants.QueryId);
const checkFields = ref<any>({});
const dsList = ref<any[]>([]);
const qFieldShow = ref(true);
const loadingByKey = ref(false);
const kpiList = ref<any[]>([]);
const orgList = ref<any[]>([]);
const memberList = ref<any[]>([]);
const pleaseSelectId = import.meta.env.VITE_PLEASE_SELECT_ID;
const today = new Date().toISOString().slice(0, 10);

const dataForTypeOptions = [
    { value: 'GLOBAL', label: 'Global' },
    { value: 'ORG', label: 'Organization' },
    { value: 'ACCOUNT', label: 'Account' }
];
const sourceTypeOptions = [
    { value: 'MANUAL', label: 'Manual' },
    { value: 'API', label: 'API' },
    { value: 'CONNECTOR', label: 'Connector' },
    { value: 'IMPORT', label: 'Import' }
];
const periodTypeQueryOptions = withAllOption(periodTypeOptions);
const dataForTypeQueryOptions = withAllOption(dataForTypeOptions);

const defaultForm = () => ({
    oid : '',
    kpiOid : pleaseSelectId,
    periodType : 'MONTH',
    periodKey : '',
    measureDate : today,
    targetValue : null,
    actualValue : null,
    minValue : null,
    maxValue : null,
    dataForType : 'GLOBAL',
    account : pleaseSelectId,
    orgOid : pleaseSelectId,
    sourceType : 'MANUAL',
    sourceRef : '',
    evidenceText : '',
    locked : 'N'
});

const formParam = ref<any>(defaultForm());

const selectedKpi = () => kpiList.value.find((item: any) => item.oid === formParam.value.kpiOid) || null;
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
const dataForTypeName = (value: string) => optionName(dataForTypeOptions, value);
const sourceTypeName = (value: string) => optionName(sourceTypeOptions, value);

const pad2 = (value: number) => String(value).padStart(2, '0');
const parsePeriodDate = () => {
    const key = formParam.value.periodKey;
    const now = new Date();
    if (formParam.value.periodType === 'DAY' && /^\d{4}-\d{2}-\d{2}$/.test(key)) {
        return new Date(key + 'T00:00:00');
    }
    if (formParam.value.periodType === 'WEEK') {
        const match = /^(\d{4})-W(\d{2})$/.exec(key);
        if (match) {
            const date = new Date(Number(match[1]), 0, 1 + (Number(match[2]) - 1) * 7);
            return date;
        }
    }
    if (formParam.value.periodType === 'MONTH' && /^\d{4}-\d{2}$/.test(key)) {
        const [year, month] = key.split('-').map(Number);
        return new Date(year, month - 1, 1);
    }
    if (formParam.value.periodType === 'QUARTER') {
        const match = /^(\d{4})-Q([1-4])$/.exec(key);
        if (match) {
            return new Date(Number(match[1]), (Number(match[2]) - 1) * 3, 1);
        }
    }
    if (formParam.value.periodType === 'HALFYEAR') {
        const match = /^(\d{4})-H([1-2])$/.exec(key);
        if (match) {
            return new Date(Number(match[1]), (Number(match[2]) - 1) * 6, 1);
        }
    }
    if (formParam.value.periodType === 'YEAR' && /^\d{4}$/.test(key)) {
        return new Date(Number(key), 0, 1);
    }
    return now;
};
const weekOfYear = (date: Date) => {
    const d = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
    d.setUTCDate(d.getUTCDate() + 4 - (d.getUTCDay() || 7));
    const yearStart = new Date(Date.UTC(d.getUTCFullYear(), 0, 1));
    return Math.ceil((((d.getTime() - yearStart.getTime()) / 86400000) + 1) / 7);
};
const buildPeriodKey = (date: Date) => {
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    if (formParam.value.periodType === 'DAY') {
        return year + '-' + pad2(month) + '-' + pad2(date.getDate());
    }
    if (formParam.value.periodType === 'WEEK') {
        return year + '-W' + pad2(weekOfYear(date));
    }
    if (formParam.value.periodType === 'MONTH') {
        return year + '-' + pad2(month);
    }
    if (formParam.value.periodType === 'QUARTER') {
        return year + '-Q' + Math.floor((month - 1) / 3 + 1);
    }
    if (formParam.value.periodType === 'HALFYEAR') {
        return year + '-H' + (month <= 6 ? '1' : '2');
    }
    return String(year);
};
const shiftPeriod = (offset: number) => {
    const date = parsePeriodDate();
    if (formParam.value.periodType === 'DAY') {
        date.setDate(date.getDate() + offset);
    } else if (formParam.value.periodType === 'WEEK') {
        date.setDate(date.getDate() + offset * 7);
    } else if (formParam.value.periodType === 'MONTH') {
        date.setMonth(date.getMonth() + offset);
    } else if (formParam.value.periodType === 'QUARTER') {
        date.setMonth(date.getMonth() + offset * 3);
    } else if (formParam.value.periodType === 'HALFYEAR') {
        date.setMonth(date.getMonth() + offset * 6);
    } else {
        date.setFullYear(date.getFullYear() + offset);
    }
    formParam.value.periodKey = buildPeriodKey(date);
    syncMeasureDate();
};
const measureDateFromPeriod = () => {
    const date = parsePeriodDate();
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    if (formParam.value.periodType === 'DAY') {
        return buildPeriodKey(date);
    }
    if (formParam.value.periodType === 'WEEK') {
        const day = date.getDay() || 7;
        date.setDate(date.getDate() - day + 1);
        return date.getFullYear() + '-' + pad2(date.getMonth() + 1) + '-' + pad2(date.getDate());
    }
    if (formParam.value.periodType === 'MONTH') {
        return year + '-' + pad2(month) + '-01';
    }
    if (formParam.value.periodType === 'QUARTER') {
        return year + '-' + pad2((Math.floor((month - 1) / 3) * 3) + 1) + '-01';
    }
    if (formParam.value.periodType === 'HALFYEAR') {
        return year + '-' + (month <= 6 ? '01' : '07') + '-01';
    }
    return year + '-01-01';
};
const syncMeasureDate = () => {
    formParam.value.measureDate = measureDateFromPeriod();
};
const syncPeriodKey = () => {
    formParam.value.periodKey = buildPeriodKey(new Date(formParam.value.measureDate || today));
    syncMeasureDate();
};

const normalizePayload = () => ({
    ...formParam.value,
    measureDate : measureDateFromPeriod(),
    account : formParam.value.dataForType === 'ACCOUNT' ? formParam.value.account : null,
    orgOid : formParam.value.dataForType === 'ORG' ? formParam.value.orgOid : null,
    sourceRef : formParam.value.sourceRef || null,
    evidenceText : formParam.value.evidenceText || null
});

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

const tbRefresh = () => btnClear();
const tbQueryFieldShow = () => qFieldShow.value = !qFieldShow.value;

const btnClear = () => {
    checkFields.value = {};
    formParam.value = defaultForm();
    const kpi = kpiList.value[0];
    if (kpi) {
        formParam.value.kpiOid = kpi.oid;
        formParam.value.periodType = kpi.periodType || 'MONTH';
    }
    syncPeriodKey();
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

const initQueryGridConfig = () => getGridConfig(
    'oid',
    [
        {
            method : (val: any) => loadRow(val),
            icon : 'pen',
            type : 'edit',
            memo : 'Load current item.',
            class : 'btn btn-info btn-sm'
        },
        {
            method : (val: any) => confirmFire('Delete current item?', delItem, val),
            icon : 'trash',
            type : 'delete',
            memo : 'Delete current item.',
            class : 'btn btn-danger btn-sm'
        }
    ],
    [
        { label: '<i class="bi bi-hand-index-thumb"></i>', field: 'oid', labHtml: true },
        { label: 'KPI', field: 'kpiName' },
        { label: 'Period', field: 'periodKey' },
        { label: 'Data For', field: 'dataForTypeName' },
        { label: 'Owner', field: 'ownerName' },
        { label: 'Target', field: 'targetValue' },
        { label: 'Actual', field: 'actualValue' },
        { label: 'Locked', field: 'lockedName' },
        { label: 'Source', field: 'sourceTypeName' }
    ]
);

const rowView = (item: any) => ({
    ...item,
    kpiName : kpiName(item.kpiOid),
    dataForTypeName : dataForTypeName(item.dataForType),
    ownerName : item.dataForType === 'ORG' ? orgName(item.orgOid) : item.dataForType === 'ACCOUNT' ? accountName(item.account) : 'Global',
    lockedName : yesNoName(item.locked),
    sourceTypeName : sourceTypeName(item.sourceType)
});

const btnQuery = async () => {
    showLoading();
    dsList.value = [];
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findPage', {
            field: {
                kpiOid : queryPageStore.queryParam.kpiOid,
                periodType : queryPageStore.queryParam.periodType,
                periodKey : queryPageStore.queryParam.periodKey,
                dataForType : queryPageStore.queryParam.dataForType,
                account : queryPageStore.queryParam.account,
                orgOid : queryPageStore.queryParam.orgOid
            },
            pageOf: {
                select : queryPageStore.gridConfig.page,
                showRow : queryPageStore.gridConfig.row
            }
        });
        hideLoading();
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                clearGridConfig();
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            dsList.value = (response.data.value || []).map(rowView);
            setConfigTotal(queryPageStore.gridConfig, response.data.pageOf.countSize);
        } else {
            toast.error('error, null');
            clearGridConfig();
        }
    } catch (e: any) {
        hideLoading();
        clearGridConfig();
        alert(e);
    }
};

const loadRow = async (oid: string) => {
    showLoading();
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/load', { oid });
        hideLoading();
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            formParam.value = {
                ...defaultForm(),
                ...response.data.value,
                account : response.data.value.account || pleaseSelectId,
                orgOid : response.data.value.orgOid || pleaseSelectId,
                measureDate : response.data.value.measureDate ? String(response.data.value.measureDate).slice(0, 10) : today
            };
        }
    } catch (e: any) {
        hideLoading();
        alert(e);
    }
};

const loadByKey = async () => {
    if (loadingByKey.value) {
        return;
    }
    if (!formParam.value.kpiOid || formParam.value.kpiOid === pleaseSelectId || !formParam.value.periodKey) {
        return;
    }
    if (formParam.value.dataForType === 'ORG' && (!formParam.value.orgOid || formParam.value.orgOid === pleaseSelectId)) {
        return;
    }
    if (formParam.value.dataForType === 'ACCOUNT' && (!formParam.value.account || formParam.value.account === pleaseSelectId)) {
        return;
    }
    try {
        loadingByKey.value = true;
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/loadByKey', normalizePayload());
        if (response.data && import.meta.env.VITE_SUCCESS_FLAG == response.data.success && response.data.value) {
            formParam.value = {
                ...formParam.value,
                ...response.data.value,
                account : response.data.value.account || pleaseSelectId,
                orgOid : response.data.value.orgOid || pleaseSelectId,
                measureDate : response.data.value.measureDate ? String(response.data.value.measureDate).slice(0, 10) : formParam.value.measureDate
            };
        }
    } catch (e: any) {
        toast.warning(e?.message || e);
    } finally {
        loadingByKey.value = false;
    }
};

const btnSave = async () => {
    checkFields.value = {};
    showLoading();
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/saveOrUpdate', normalizePayload());
        hideLoading();
        if (response.data) {
            checkFields.value = response.data.checkFields || {};
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            toast.success(response.data.message);
            formParam.value = {
                ...formParam.value,
                ...response.data.value,
                account : response.data.value.account || pleaseSelectId,
                orgOid : response.data.value.orgOid || pleaseSelectId,
                measureDate : response.data.value.measureDate ? String(response.data.value.measureDate).slice(0, 10) : formParam.value.measureDate
            };
            btnQuery();
        } else {
            toast.error('error, null');
        }
    } catch (e: any) {
        hideLoading();
        alert(e);
    }
};

const delItem = async (oid: string) => {
    showLoading();
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/delete', { oid });
        hideLoading();
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
                toast.success(response.data.message);
            } else {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
            }
            btnQuery();
        }
    } catch (e: any) {
        hideLoading();
        alert(e);
    }
};

watch(() => formParam.value.kpiOid, () => {
    const kpi = selectedKpi();
    if (kpi) {
        formParam.value.periodType = kpi.periodType || formParam.value.periodType;
        formParam.value.targetValue = formParam.value.targetValue ?? kpi.targetValue;
        formParam.value.minValue = formParam.value.minValue ?? kpi.minValue;
        formParam.value.maxValue = formParam.value.maxValue ?? kpi.maxValue;
        syncPeriodKey();
    }
});
watch(() => formParam.value.periodType, () => syncPeriodKey());
watch(() => formParam.value.periodKey, () => syncMeasureDate());
watch(() => [formParam.value.kpiOid, formParam.value.periodType, formParam.value.periodKey, formParam.value.dataForType, formParam.value.account, formParam.value.orgOid], () => loadByKey());
watch(() => formParam.value.dataForType, () => {
    if (formParam.value.dataForType !== 'ACCOUNT') {
        formParam.value.account = pleaseSelectId;
    }
    if (formParam.value.dataForType !== 'ORG') {
        formParam.value.orgOid = pleaseSelectId;
    }
});

onMounted(async () => {
    try {
        await Promise.all([loadKpiList(), loadOrgList(), loadMemberList()]);
    } catch (e: any) {
        toast.warning(e?.message || e);
    }
    btnClear();
    const newGridConfig = initQueryGridConfig();
    if (queryPageStore.gridConfig.column) {
        resetConfigByOld(newGridConfig, queryPageStore.gridConfig);
    }
    queryPageStore.gridConfig = newGridConfig;
    if (queryPageStore.gridConfig.total > 0) {
        btnQuery();
    }
});
</script>

<template>
<div class="row">
  <div class="col-12">
    <Toolbar
        :progId="pageProgramId"
        description="KPI Measure Data"
        refreshFlag="Y"
        @refreshMethod="tbRefresh"
        saveFlag="Y"
        @saveMethod="btnSave"
        queryFieldShowSwitchFlag="Y"
        @queryFieldShowSwitcMethod="tbQueryFieldShow"
    />
  </div>
</div>

<div class="card mb-4">
  <div class="card-body">
    <div class="row g-3">
      <div class="col-md-6">
        <label for="kpiOid" class="form-label">KPI</label>
        <select :class="['form-select', checkInvalid('kpiOid', checkFields) ? 'is-invalid' : '']" id="kpiOid" v-model="formParam.kpiOid">
          <option :value="pleaseSelectId">Please select</option>
          <option v-for="item in kpiList" :key="item.oid" :value="item.oid">{{ item.kpiCode }} - {{ item.kpiName }}</option>
        </select>
        <div v-if="checkInvalid('kpiOid', checkFields)" class="invalid-feedback">{{ invalidFeedback('kpiOid', checkFields) }}</div>
      </div>
      <div class="col-md-2">
        <label for="periodType" class="form-label">Period Type</label>
        <select :class="['form-select', checkInvalid('periodType', checkFields) ? 'is-invalid' : '']" id="periodType" v-model="formParam.periodType">
          <option v-for="item in periodTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
        <div v-if="checkInvalid('periodType', checkFields)" class="invalid-feedback">{{ invalidFeedback('periodType', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="periodKey" class="form-label">Period Key</label>
        <div class="input-group">
          <button type="button" class="btn btn-outline-secondary" @click="shiftPeriod(-1)"><i class="bi bi-chevron-left"></i></button>
          <input type="text" :class="['form-control', checkInvalid('periodKey', checkFields) ? 'is-invalid' : '']" id="periodKey" v-model="formParam.periodKey">
          <button type="button" class="btn btn-outline-secondary" @click="shiftPeriod(1)"><i class="bi bi-chevron-right"></i></button>
        </div>
        <div v-if="checkInvalid('periodKey', checkFields)" class="invalid-feedback d-block">{{ invalidFeedback('periodKey', checkFields) }}</div>
      </div>

      <div class="col-md-3">
        <label for="dataForType" class="form-label">Data For</label>
        <select :class="['form-select', checkInvalid('dataForType', checkFields) ? 'is-invalid' : '']" id="dataForType" v-model="formParam.dataForType">
          <option v-for="item in dataForTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
        <div v-if="checkInvalid('dataForType', checkFields)" class="invalid-feedback">{{ invalidFeedback('dataForType', checkFields) }}</div>
      </div>
      <div v-if="formParam.dataForType === 'ORG'" class="col-md-5">
        <label for="orgOid" class="form-label">Organization</label>
        <select class="form-select" id="orgOid" v-model="formParam.orgOid">
          <option :value="pleaseSelectId">Please select</option>
          <option v-for="item in orgList" :key="item.oid" :value="item.oid">{{ item.orgCode }} - {{ item.orgName }}</option>
        </select>
      </div>
      <div v-if="formParam.dataForType === 'ACCOUNT'" class="col-md-5">
        <label for="account" class="form-label">Account</label>
        <select class="form-select" id="account" v-model="formParam.account">
          <option :value="pleaseSelectId">Please select</option>
          <option v-for="item in memberList" :key="item.account" :value="item.account">{{ item.account }}<template v-if="item.displayName"> - {{ item.displayName }}</template></option>
        </select>
      </div>
      <div class="col-md-2">
        <label for="measureDate" class="form-label">Measure Date</label>
        <input type="date" class="form-control" id="measureDate" v-model="formParam.measureDate" readonly disabled>
      </div>
      <div class="col-md-2">
        <label for="locked" class="form-label">Locked</label>
        <select :class="['form-select', checkInvalid('locked', checkFields) ? 'is-invalid' : '']" id="locked" v-model="formParam.locked">
          <option v-for="item in yesNoOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
        <div v-if="checkInvalid('locked', checkFields)" class="invalid-feedback">{{ invalidFeedback('locked', checkFields) }}</div>
      </div>

      <div class="col-md-3">
        <label for="targetValue" class="form-label">Target</label>
        <input type="number" step="0.0001" class="form-control" id="targetValue" v-model.number="formParam.targetValue">
      </div>
      <div class="col-md-3">
        <label for="actualValue" class="form-label">Actual</label>
        <input type="number" step="0.0001" :class="['form-control', checkInvalid('actualValue', checkFields) ? 'is-invalid' : '']" id="actualValue" v-model.number="formParam.actualValue">
        <div v-if="checkInvalid('actualValue', checkFields)" class="invalid-feedback">{{ invalidFeedback('actualValue', checkFields) }}</div>
      </div>
      <div class="col-md-3">
        <label for="minValue" class="form-label">Min</label>
        <input type="number" step="0.0001" class="form-control" id="minValue" v-model.number="formParam.minValue">
      </div>
      <div class="col-md-3">
        <label for="maxValue" class="form-label">Max</label>
        <input type="number" step="0.0001" class="form-control" id="maxValue" v-model.number="formParam.maxValue">
      </div>

      <div class="col-md-3">
        <label for="sourceType" class="form-label">Source</label>
        <select class="form-select" id="sourceType" v-model="formParam.sourceType">
          <option v-for="item in sourceTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
      </div>
      <div class="col-md-9">
        <label for="sourceRef" class="form-label">Source Ref</label>
        <input type="text" class="form-control" id="sourceRef" v-model="formParam.sourceRef">
      </div>
      <div class="col-md-12">
        <label for="evidenceText" class="form-label">Evidence</label>
        <textarea class="form-control" id="evidenceText" rows="3" v-model="formParam.evidenceText"></textarea>
      </div>
    </div>
    <div class="mt-4 d-flex gap-2">
      <button type="button" class="btn btn-primary" @click="btnSave"><i class="bi bi-save"></i> Save</button>
      <button type="button" class="btn btn-outline-secondary" @click="btnClear"><i class="bi bi-eraser"></i> Clear</button>
    </div>
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
        <div class="form-group form-floating">
          <input type="text" class="form-control" id="queryPeriodKey" placeholder="Period Key" v-model="queryPageStore.queryParam.periodKey">
          <label for="queryPeriodKey">Period Key</label>
        </div>
      </div>
      <div class="col-md-2">
        <div class="form-group form-floating">
          <select class="form-select" id="queryDataForType" v-model="queryPageStore.queryParam.dataForType">
            <option v-for="item in dataForTypeQueryOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
          </select>
          <label for="queryDataForType">Data For</label>
        </div>
      </div>
      <div class="col-md-2 d-flex align-items-end">
        <button type="button" class="btn btn-primary w-100" @click="btnQuery"><i class="bi bi-search"></i> Query</button>
      </div>
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
