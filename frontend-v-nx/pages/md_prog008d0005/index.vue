<script setup lang="ts">
import { computed, ref, onMounted, watch } from 'vue';
import { toast } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

import Toolbar from '@/components/Toolbar.vue';
import Grid from '@/components/Grid.vue';
import GridPagination from '@/components/GridPagination.vue';
import HiddenQueryFieldAlertInfo from '@/components/HiddenQueryFieldAlertInfo.vue';
import { PageConstants } from './config';
import { getGridConfig, setConfigRow, setConfigPage, setConfigTotal, resetConfigByOld } from '../../components/GridHelper';
import { useMdProg008d0005Store } from './QueryPageStore';
import {
    getAxiosInstance,
    escapeQifuHtmlMsg
} from '../../components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';

definePageMeta({ middleware: ['auth'] });

const queryPageStore = useMdProg008d0005Store();
const { showLoading, hideLoading } = useSwalLoading();

const pageProgramId = ref(PageConstants.QueryId);
const dsList = ref<any[]>([]);
const planList = ref<any[]>([]);
const memberList = ref<any[]>([]);
const orgList = ref<any[]>([]);
const kpiList = ref<any[]>([]);
const okrObjectiveList = ref<any[]>([]);
const okrKeyResultList = ref<any[]>([]);
const strategyObjectiveList = ref<any[]>([]);
const reportSummary = ref<any>(null);
const qFieldShow = ref(true);

const statusOptions = [
    { value: '', label: 'All' },
    { value: 'DRAFT', label: 'Draft' },
    { value: 'ACTIVE', label: 'Active' },
    { value: 'CLOSED', label: 'Closed' },
    { value: 'ARCHIVED', label: 'Archived' }
];
const stageOptions = [
    { value: '', label: 'All' },
    { value: 'PLAN', label: 'Plan' },
    { value: 'DO', label: 'Do' },
    { value: 'CHECK', label: 'Check' },
    { value: 'ACT', label: 'Act' }
];
const ownerTypeOptions = [
    { value: '', label: 'All' },
    { value: 'ACCOUNT', label: 'Account' },
    { value: 'ORG', label: 'Organization' }
];
const sourceTypeOptions = [
    { value: '', label: 'All' },
    { value: 'KPI', label: 'KPI' },
    { value: 'OKR_OBJECTIVE', label: 'OKR Objective' },
    { value: 'OKR_KR', label: 'OKR Key Result' },
    { value: 'STRATEGY', label: 'Strategy' },
    { value: 'INSIGHT', label: 'Insight' }
];

const statusName = (value: string) => (statusOptions.find((item) => item.value === value)?.label || value);
const stageName = (value: string) => (stageOptions.find((item) => item.value === value)?.label || value);
const dateDisplay = (value: any) => value ? String(value).slice(0, 10) : '';
const percentDisplay = (value: any) => value == null ? '' : `${value}%`;
const currentSourceOptions = computed(() => {
    if (queryPageStore.queryParam.sourceType === 'KPI') {
        return kpiList.value.map((item: any) => ({ oid: item.oid, label: item.kpiCode + ' - ' + item.kpiName }));
    }
    if (queryPageStore.queryParam.sourceType === 'OKR_OBJECTIVE') {
        return okrObjectiveList.value.map((item: any) => ({ oid: item.oid, label: item.objectiveCode + ' - ' + item.objectiveName }));
    }
    if (queryPageStore.queryParam.sourceType === 'OKR_KR') {
        return okrKeyResultList.value.map((item: any) => ({ oid: item.oid, label: item.krCode + ' - ' + item.krName }));
    }
    if (queryPageStore.queryParam.sourceType === 'STRATEGY') {
        return strategyObjectiveList.value.map((item: any) => ({ oid: item.oid, label: item.objectiveCode + ' - ' + item.objectiveName }));
    }
    return [];
});
const sourceSelectDisabled = computed(() => !queryPageStore.queryParam.sourceType || currentSourceOptions.value.length < 1);

const requestBody = () => ({
    field: {
        planOid : queryPageStore.queryParam.planOid,
        actionStage : queryPageStore.queryParam.actionStage,
        status : queryPageStore.queryParam.status,
        ownerType : queryPageStore.queryParam.ownerType,
        account : queryPageStore.queryParam.account,
        orgOid : queryPageStore.queryParam.orgOid,
        sourceType : queryPageStore.queryParam.sourceType,
        sourceOid : queryPageStore.queryParam.sourceOid,
        startDateFrom : queryPageStore.queryParam.startDateFrom,
        startDateTo : queryPageStore.queryParam.startDateTo,
        endDateFrom : queryPageStore.queryParam.endDateFrom,
        endDateTo : queryPageStore.queryParam.endDateTo,
        overdueOnly : queryPageStore.queryParam.overdueOnly
    },
    pageOf: {
        select : queryPageStore.gridConfig.page,
        showRow : queryPageStore.gridConfig.row
    }
});

const tbRefresh = () => btnClear();
const tbQueryFieldShow = () => qFieldShow.value = !qFieldShow.value;

const btnClear = () => {
    queryPageStore.clearData();
    dsList.value = [];
    reportSummary.value = null;
    clearGridConfig();
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
    [],
    [
        { label: 'Plan', field: 'planDisplay' },
        { label: 'Item', field: 'itemName' },
        { label: 'Stage', field: 'stageDisplay' },
        { label: 'Status', field: 'statusDisplay' },
        { label: 'Progress', field: 'progressDisplay' },
        { label: 'End Date', field: 'endDateDisplay' },
        { label: 'Overdue', field: 'overdueDisplay' },
        { label: 'Owners', field: 'ownerSummary' },
        { label: 'Sources', field: 'sourceSummary' }
    ]
);

const rowView = (item: any) => ({
    ...item,
    planDisplay : item.planCode ? `${item.planCode} - ${item.planName}` : item.planOid,
    stageDisplay : stageName(item.actionStage),
    statusDisplay : statusName(item.status),
    progressDisplay : percentDisplay(item.progressValue),
    startDateDisplay : dateDisplay(item.startDate),
    endDateDisplay : dateDisplay(item.endDate),
    doneDateDisplay : dateDisplay(item.doneDate),
    overdueDisplay : item.overdue ? 'Y' : 'N'
});

const loadPlanList = async () => {
    const axiosInstance = getAxiosInstance();
    const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findPlanList', {});
    if (response.data && import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
        planList.value = response.data.value || [];
    }
};

const loadMemberList = async () => {
    const axiosInstance = getAxiosInstance();
    const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findMemberList', {});
    if (response.data && import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
        memberList.value = response.data.value || [];
    }
};

const loadOrgList = async () => {
    const axiosInstance = getAxiosInstance();
    const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findOrgList', {});
    if (response.data && import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
        orgList.value = response.data.value || [];
    }
};

const loadOptionList = async (path: string, target: any, filterEnabled = true) => {
    const axiosInstance = getAxiosInstance();
    const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + path, {});
    if (response.data && import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
        target.value = filterEnabled ? (response.data.value || []).filter((item: any) => item.enabled !== 'N') : (response.data.value || []);
    }
};

const loadSummary = async () => {
    const axiosInstance = getAxiosInstance();
    const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/summary', requestBody());
    if (response.data) {
        if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
            toast.warning(escapeQifuHtmlMsg(response.data.message));
            return;
        }
        reportSummary.value = response.data.value?.summary || null;
    }
};

const btnQuery = async () => {
    showLoading();
    dsList.value = [];
    reportSummary.value = null;
    try {
        const axiosInstance = getAxiosInstance();
        await loadSummary();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findPage', requestBody());
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
        toast.warning(e?.message || e);
    }
};

onMounted(async () => {
    await Promise.all([
        loadPlanList(),
        loadMemberList(),
        loadOrgList(),
        loadOptionList('/findKpiList', kpiList),
        loadOptionList('/findOkrObjectiveList', okrObjectiveList, false),
        loadOptionList('/findKrList', okrKeyResultList, false),
        loadOptionList('/findStrategyObjectiveList', strategyObjectiveList, false)
    ]);
    const newGridConfig = initQueryGridConfig();
    if (queryPageStore.gridConfig.column) {
        resetConfigByOld(newGridConfig, queryPageStore.gridConfig);
    }
    queryPageStore.gridConfig = newGridConfig;
});

watch(() => queryPageStore.queryParam.sourceType, () => {
    queryPageStore.queryParam.sourceOid = '';
});
</script>

<template>
<div class="row">
  <div class="col-12">
    <Toolbar
        :progId="pageProgramId"
        description="Action / PDCA Report"
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
          <select class="form-select" id="planOid" v-model="queryPageStore.queryParam.planOid">
            <option value="">All</option>
            <option v-for="item in planList" :key="item.oid" :value="item.oid">{{ item.planCode }} - {{ item.planName }}</option>
          </select>
          <label for="planOid">Action Plan</label>
        </div>
      </div>
      <div class="col-md-2">
        <div class="form-group form-floating">
          <select class="form-select" id="actionStage" v-model="queryPageStore.queryParam.actionStage">
            <option v-for="item in stageOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
          </select>
          <label for="actionStage">Stage</label>
        </div>
      </div>
      <div class="col-md-2">
        <div class="form-group form-floating">
          <select class="form-select" id="status" v-model="queryPageStore.queryParam.status">
            <option v-for="item in statusOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
          </select>
          <label for="status">Status</label>
        </div>
      </div>
      <div class="col-md-2">
        <div class="form-group form-floating">
          <select class="form-select" id="ownerType" v-model="queryPageStore.queryParam.ownerType">
            <option v-for="item in ownerTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
          </select>
          <label for="ownerType">Owner Type</label>
        </div>
      </div>
      <div class="col-md-2 d-flex align-items-center">
        <div class="form-check">
          <input class="form-check-input" type="checkbox" id="overdueOnly" true-value="Y" false-value="" v-model="queryPageStore.queryParam.overdueOnly">
          <label class="form-check-label" for="overdueOnly">Overdue only</label>
        </div>
      </div>
      <div class="col-md-3">
        <div class="form-group form-floating">
          <select class="form-select" id="account" v-model="queryPageStore.queryParam.account">
            <option value="">All</option>
            <option v-for="item in memberList" :key="item.oid" :value="item.account">{{ item.account }} - {{ item.displayName }}</option>
          </select>
          <label for="account">Account Owner</label>
        </div>
      </div>
      <div class="col-md-3">
        <div class="form-group form-floating">
          <select class="form-select" id="orgOid" v-model="queryPageStore.queryParam.orgOid">
            <option value="">All</option>
            <option v-for="item in orgList" :key="item.oid" :value="item.oid">{{ item.orgCode }} - {{ item.orgName }}</option>
          </select>
          <label for="orgOid">Org Owner</label>
        </div>
      </div>
      <div class="col-md-3">
        <div class="form-group form-floating">
          <select class="form-select" id="sourceType" v-model="queryPageStore.queryParam.sourceType">
            <option v-for="item in sourceTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
          </select>
          <label for="sourceType">Source Type</label>
        </div>
      </div>
      <div class="col-md-3">
        <div class="form-group form-floating">
          <select class="form-select" id="sourceOid" v-model="queryPageStore.queryParam.sourceOid" :disabled="sourceSelectDisabled">
            <option value="">{{ queryPageStore.queryParam.sourceType ? 'All' : 'Select source type first' }}</option>
            <option v-for="item in currentSourceOptions" :key="item.oid" :value="item.oid">{{ item.label }}</option>
          </select>
          <label for="sourceOid">Source</label>
        </div>
      </div>
      <div class="col-md-3">
        <div class="form-group form-floating">
          <input type="date" class="form-control" id="startDateFrom" v-model="queryPageStore.queryParam.startDateFrom">
          <label for="startDateFrom">Start From</label>
        </div>
      </div>
      <div class="col-md-3">
        <div class="form-group form-floating">
          <input type="date" class="form-control" id="startDateTo" v-model="queryPageStore.queryParam.startDateTo">
          <label for="startDateTo">Start To</label>
        </div>
      </div>
      <div class="col-md-3">
        <div class="form-group form-floating">
          <input type="date" class="form-control" id="endDateFrom" v-model="queryPageStore.queryParam.endDateFrom">
          <label for="endDateFrom">End From</label>
        </div>
      </div>
      <div class="col-md-3">
        <div class="form-group form-floating">
          <input type="date" class="form-control" id="endDateTo" v-model="queryPageStore.queryParam.endDateTo">
          <label for="endDateTo">End To</label>
        </div>
      </div>
      <div class="col-12 d-flex gap-2">
        <button type="button" class="btn btn-primary" @click="btnQuery"><i class="bi bi-search"></i> Query</button>
        <button type="button" class="btn btn-outline-secondary" @click="btnClear"><i class="bi bi-eraser"></i> Clear</button>
      </div>
    </div>
  </div>
</div>

<div v-if="reportSummary" class="row g-3 mb-4">
  <div class="col-md-2 col-6">
    <div class="border rounded p-3 h-100">
      <div class="small text-muted">Plans</div>
      <div class="fs-4 fw-semibold">{{ reportSummary.planCount }}</div>
    </div>
  </div>
  <div class="col-md-2 col-6">
    <div class="border rounded p-3 h-100">
      <div class="small text-muted">Items</div>
      <div class="fs-4 fw-semibold">{{ reportSummary.itemCount }}</div>
    </div>
  </div>
  <div class="col-md-2 col-6">
    <div class="border rounded p-3 h-100">
      <div class="small text-muted">Overdue</div>
      <div class="fs-4 fw-semibold">{{ reportSummary.overdueCount }}</div>
    </div>
  </div>
  <div class="col-md-2 col-6">
    <div class="border rounded p-3 h-100">
      <div class="small text-muted">Completed</div>
      <div class="fs-4 fw-semibold">{{ reportSummary.completedCount }}</div>
    </div>
  </div>
  <div class="col-md-2 col-6">
    <div class="border rounded p-3 h-100">
      <div class="small text-muted">Avg Progress</div>
      <div class="fs-4 fw-semibold">{{ reportSummary.avgProgress }}%</div>
    </div>
  </div>
  <div class="col-md-2 col-6">
    <div class="border rounded p-3 h-100">
      <div class="small text-muted">Links</div>
      <div class="fs-4 fw-semibold">{{ reportSummary.sourceLinkCount }}</div>
    </div>
  </div>
  <div class="col-12">
    <div class="border rounded p-3">
      <div class="small text-muted mb-2">PDCA Stage Distribution</div>
      <div class="d-flex flex-wrap gap-3">
        <span>Plan: <strong>{{ reportSummary.planStageCount }}</strong></span>
        <span>Do: <strong>{{ reportSummary.doStageCount }}</strong></span>
        <span>Check: <strong>{{ reportSummary.checkStageCount }}</strong></span>
        <span>Act: <strong>{{ reportSummary.actStageCount }}</strong></span>
        <span>Owners: <strong>{{ reportSummary.ownerCount }}</strong></span>
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
