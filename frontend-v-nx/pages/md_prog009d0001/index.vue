<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { toast } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

import Toolbar from '@/components/Toolbar.vue';
import Grid from '@/components/Grid.vue';
import HiddenQueryFieldAlertInfo from '@/components/HiddenQueryFieldAlertInfo.vue';
import { PageConstants } from './config';
import { getGridConfig, setConfigTotal, resetConfigByOld } from '../../components/GridHelper';
import { useMdProg009d0001Store } from './QueryPageStore';
import {
    getAxiosInstance,
    escapeQifuHtmlMsg
} from '../../components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';

definePageMeta({ middleware: ['auth'] });

const queryPageStore = useMdProg009d0001Store();
const { showLoading, hideLoading } = useSwalLoading();

const pageProgramId = ref(PageConstants.QueryId);
const qFieldShow = ref(true);
const dashboard = ref<any>(null);
const alertList = ref<any[]>([]);
const cycleList = ref<any[]>([]);
const workspaceList = ref<any[]>([]);
const orgList = ref<any[]>([]);
const memberList = ref<any[]>([]);

const periodTypeOptions = [
    { value: '', label: 'All' },
    { value: 'DAY', label: 'Day' },
    { value: 'WEEK', label: 'Week' },
    { value: 'MONTH', label: 'Month' },
    { value: 'QUARTER', label: 'Quarter' },
    { value: 'HALFYEAR', label: 'Half Year' },
    { value: 'YEAR', label: 'Year' }
];
const dataForTypeOptions = [
    { value: '', label: 'All' },
    { value: 'GLOBAL', label: 'Global' },
    { value: 'ACCOUNT', label: 'Account' },
    { value: 'ORG', label: 'Organization' }
];

const numberDisplay = (value: any) => value == null ? '0' : value;
const scoreDisplay = (value: any) => value == null ? '0' : Number(value).toFixed(2);

const requestBody = () => ({
    periodType : queryPageStore.queryParam.periodType,
    periodKey : queryPageStore.queryParam.periodKey,
    periodKeyFrom : queryPageStore.queryParam.periodKeyFrom,
    periodKeyTo : queryPageStore.queryParam.periodKeyTo,
    dataForType : queryPageStore.queryParam.dataForType,
    account : queryPageStore.queryParam.account,
    orgOid : queryPageStore.queryParam.orgOid,
    cycleOid : queryPageStore.queryParam.cycleOid,
    workspaceOid : queryPageStore.queryParam.workspaceOid
});

const tbRefresh = () => btnQuery();
const tbQueryFieldShow = () => qFieldShow.value = !qFieldShow.value;

const btnClear = () => {
    queryPageStore.clearData();
    dashboard.value = null;
    alertList.value = [];
    setConfigTotal(queryPageStore.gridConfig, 0);
};

const initAlertGridConfig = () => getGridConfig(
    'sourceOid',
    [],
    [
        { label: 'Domain', field: 'domain' },
        { label: 'Severity', field: 'severity' },
        { label: 'Title', field: 'title' },
        { label: 'Summary', field: 'summary' },
        { label: 'Period', field: 'periodKey' },
        { label: 'Score', field: 'scoreDisplay' }
    ]
);

const rowView = (item: any) => ({
    ...item,
    scoreDisplay : item.scoreValue == null ? '' : scoreDisplay(item.scoreValue)
});

const loadOptionList = async (path: string, target: any) => {
    const axiosInstance = getAxiosInstance();
    const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + path, {});
    if (response.data && import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
        target.value = response.data.value || [];
    }
};

const loadOptionLists = async () => {
    await Promise.all([
        loadOptionList('/findCycleList', cycleList),
        loadOptionList('/findWorkspaceList', workspaceList),
        loadOptionList('/findOrgList', orgList),
        loadOptionList('/findMemberList', memberList)
    ]);
};

const btnQuery = async () => {
    showLoading();
    dashboard.value = null;
    alertList.value = [];
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/dashboard', requestBody());
        hideLoading();
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                setConfigTotal(queryPageStore.gridConfig, 0);
                return;
            }
            dashboard.value = response.data.value || {};
            alertList.value = (dashboard.value.alerts || []).map(rowView);
            setConfigTotal(queryPageStore.gridConfig, alertList.value.length);
        } else {
            toast.error('error, null');
            setConfigTotal(queryPageStore.gridConfig, 0);
        }
    } catch (e: any) {
        hideLoading();
        setConfigTotal(queryPageStore.gridConfig, 0);
        toast.warning(e?.message || e);
    }
};

onMounted(async () => {
    await loadOptionLists();
    const newGridConfig = initAlertGridConfig();
    if (queryPageStore.gridConfig.column) {
        resetConfigByOld(newGridConfig, queryPageStore.gridConfig);
    }
    queryPageStore.gridConfig = newGridConfig;
});
</script>

<template>
<div class="row">
  <div class="col-12">
    <Toolbar
        :progId="pageProgramId"
        description="Management Dashboard"
        refreshFlag="Y"
        @refreshMethod="tbRefresh"
        queryFieldShowSwitchFlag="Y"
        @queryFieldShowSwitcMethod="tbQueryFieldShow"
    />
  </div>
</div>

<HiddenQueryFieldAlertInfo :dataSource="alertList" :queryFieldShowFlag="qFieldShow" />

<div v-show="qFieldShow" class="card mb-4">
  <div class="card-body">
    <div class="row g-3">
      <div class="col-md-2">
        <div class="form-group form-floating">
          <select class="form-select" id="periodType" v-model="queryPageStore.queryParam.periodType">
            <option v-for="item in periodTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
          </select>
          <label for="periodType">Period Type</label>
        </div>
      </div>
      <div class="col-md-2">
        <div class="form-group form-floating">
          <input type="text" class="form-control" id="periodKey" placeholder="Period Key" v-model="queryPageStore.queryParam.periodKey">
          <label for="periodKey">Period Key</label>
        </div>
      </div>
      <div class="col-md-2">
        <div class="form-group form-floating">
          <input type="text" class="form-control" id="periodKeyFrom" placeholder="Period From" v-model="queryPageStore.queryParam.periodKeyFrom">
          <label for="periodKeyFrom">Period From</label>
        </div>
      </div>
      <div class="col-md-2">
        <div class="form-group form-floating">
          <input type="text" class="form-control" id="periodKeyTo" placeholder="Period To" v-model="queryPageStore.queryParam.periodKeyTo">
          <label for="periodKeyTo">Period To</label>
        </div>
      </div>
      <div class="col-md-2">
        <div class="form-group form-floating">
          <select class="form-select" id="dataForType" v-model="queryPageStore.queryParam.dataForType">
            <option v-for="item in dataForTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
          </select>
          <label for="dataForType">Data For</label>
        </div>
      </div>
      <div class="col-md-2">
        <div class="form-group form-floating">
          <select class="form-select" id="account" v-model="queryPageStore.queryParam.account">
            <option value="">All</option>
            <option v-for="item in memberList" :key="item.oid" :value="item.account">{{ item.account }} - {{ item.displayName }}</option>
          </select>
          <label for="account">Account</label>
        </div>
      </div>
      <div class="col-md-3">
        <div class="form-group form-floating">
          <select class="form-select" id="orgOid" v-model="queryPageStore.queryParam.orgOid">
            <option value="">All</option>
            <option v-for="item in orgList" :key="item.oid" :value="item.oid">{{ item.orgCode }} - {{ item.orgName }}</option>
          </select>
          <label for="orgOid">Organization</label>
        </div>
      </div>
      <div class="col-md-3">
        <div class="form-group form-floating">
          <select class="form-select" id="cycleOid" v-model="queryPageStore.queryParam.cycleOid">
            <option value="">All</option>
            <option v-for="item in cycleList" :key="item.oid" :value="item.oid">{{ item.cycleCode }} - {{ item.cycleName }}</option>
          </select>
          <label for="cycleOid">OKR Cycle</label>
        </div>
      </div>
      <div class="col-md-4">
        <div class="form-group form-floating">
          <select class="form-select" id="workspaceOid" v-model="queryPageStore.queryParam.workspaceOid">
            <option value="">All</option>
            <option v-for="item in workspaceList" :key="item.oid" :value="item.oid">{{ item.workspaceCode }} - {{ item.workspaceName }}</option>
          </select>
          <label for="workspaceOid">Strategy Workspace</label>
        </div>
      </div>
      <div class="col-md-2 d-flex gap-2 align-items-center">
        <button type="button" class="btn btn-primary" @click="btnQuery"><i class="bi bi-search"></i> Query</button>
        <button type="button" class="btn btn-outline-secondary" @click="btnClear"><i class="bi bi-eraser"></i> Clear</button>
      </div>
    </div>
  </div>
</div>

<div v-if="dashboard" class="row g-3 mb-4">
  <div class="col-lg-3 col-md-6">
    <div class="border rounded p-3 h-100">
      <div class="small text-muted">KPI</div>
      <div class="fs-4 fw-semibold">{{ scoreDisplay(dashboard.kpi?.avgScore) }}</div>
      <div class="small">Snapshots: {{ numberDisplay(dashboard.kpi?.totalCount) }}</div>
      <div class="small">Good / Warning / Bad: {{ numberDisplay(dashboard.kpi?.goodCount) }} / {{ numberDisplay(dashboard.kpi?.warningCount) }} / {{ numberDisplay(dashboard.kpi?.badCount) }}</div>
    </div>
  </div>
  <div class="col-lg-3 col-md-6">
    <div class="border rounded p-3 h-100">
      <div class="small text-muted">OKR</div>
      <div class="fs-4 fw-semibold">{{ scoreDisplay(dashboard.okr?.avgScore) }}%</div>
      <div class="small">Objectives: {{ numberDisplay(dashboard.okr?.totalCount) }}</div>
      <div class="small">KRs / Initiatives: {{ numberDisplay(dashboard.okr?.secondaryCount) }} / {{ numberDisplay(dashboard.okr?.tertiaryCount) }}</div>
    </div>
  </div>
  <div class="col-lg-3 col-md-6">
    <div class="border rounded p-3 h-100">
      <div class="small text-muted">Strategy</div>
      <div class="fs-4 fw-semibold">{{ scoreDisplay(dashboard.strategy?.avgScore) }}</div>
      <div class="small">Workspaces: {{ numberDisplay(dashboard.strategy?.totalCount) }}</div>
      <div class="small">Themes / Objectives: {{ numberDisplay(dashboard.strategy?.secondaryCount) }} / {{ numberDisplay(dashboard.strategy?.tertiaryCount) }}</div>
    </div>
  </div>
  <div class="col-lg-3 col-md-6">
    <div class="border rounded p-3 h-100">
      <div class="small text-muted">Action / PDCA</div>
      <div class="fs-4 fw-semibold">{{ scoreDisplay(dashboard.action?.avgScore) }}%</div>
      <div class="small">Items: {{ numberDisplay(dashboard.action?.totalCount) }}</div>
      <div class="small">Overdue / Completed: {{ numberDisplay(dashboard.action?.overdueCount) }} / {{ numberDisplay(dashboard.action?.completedCount) }}</div>
    </div>
  </div>
</div>

<div class="row">
  <div class="col-12">
    <div class="d-flex justify-content-between align-items-center mb-2">
      <div class="fw-semibold">Management Alerts</div>
      <div class="small text-muted">{{ alertList.length }} records</div>
    </div>
    <Grid :progId="pageProgramId" :dataSource="alertList" :config="queryPageStore.gridConfig" />
  </div>
</div>
</template>
