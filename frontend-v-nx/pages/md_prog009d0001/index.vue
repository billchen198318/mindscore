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
const activeTab = ref('overview');
const dashboard = ref<any>(null);
const alertList = ref<any[]>([]);
const orgSummaryList = ref<any[]>([]);
const strategyScorecardList = ref<any[]>([]);
const delayedActionList = ref<any[]>([]);
const atRiskObjectiveList = ref<any[]>([]);
const cycleList = ref<any[]>([]);
const workspaceList = ref<any[]>([]);
const orgList = ref<any[]>([]);
const memberList = ref<any[]>([]);
const alertGridConfig = ref<any>(null);
const orgGridConfig = ref<any>(null);
const scorecardGridConfig = ref<any>(null);
const delayedActionGridConfig = ref<any>(null);
const atRiskObjectiveGridConfig = ref<any>(null);

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
    orgSummaryList.value = [];
    strategyScorecardList.value = [];
    delayedActionList.value = [];
    atRiskObjectiveList.value = [];
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

const orgSummaryRowView = (item: any) => ({
    ...item,
    orgDisplay : item.orgCode ? `${item.orgCode} - ${item.orgName}` : item.orgOid,
    avgKpiScoreDisplay : scoreDisplay(item.avgKpiScore)
});

const scorecardRowView = (item: any) => ({
    ...item,
    workspaceDisplay : item.workspaceCode ? `${item.workspaceCode} - ${item.workspaceName}` : item.workspaceOid,
    scoreDisplay : scoreDisplay(item.scoreValue)
});

const dateDisplay = (value: any) => value ? String(value).slice(0, 10) : '';

const delayedActionRowView = (item: any) => ({
    ...item,
    planDisplay : item.planCode ? `${item.planCode} - ${item.planName}` : item.planOid,
    progressDisplay : item.progressValue == null ? '' : `${item.progressValue}%`,
    endDateDisplay : dateDisplay(item.endDate)
});

const atRiskObjectiveRowView = (item: any) => ({
    ...item,
    progressDisplay : `${scoreDisplay(item.progressValue)}%`,
    confidenceDisplay : scoreDisplay(item.confidenceScore)
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
            orgSummaryList.value = (dashboard.value.organizationSummaries || []).map(orgSummaryRowView);
            strategyScorecardList.value = (dashboard.value.strategyScorecards || []).map(scorecardRowView);
            delayedActionList.value = (dashboard.value.delayedActions || []).map(delayedActionRowView);
            atRiskObjectiveList.value = (dashboard.value.atRiskObjectives || []).map(atRiskObjectiveRowView);
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
    alertGridConfig.value = newGridConfig;
    orgGridConfig.value = getGridConfig(
        'orgOid',
        [],
        [
            { label: 'Organization', field: 'orgDisplay' },
            { label: 'KPI Snapshots', field: 'kpiSnapshotCount' },
            { label: 'Avg KPI Score', field: 'avgKpiScoreDisplay' },
            { label: 'Good', field: 'goodCount' },
            { label: 'Warning', field: 'warningCount' },
            { label: 'Bad', field: 'badCount' },
            { label: 'Unknown', field: 'unknownCount' }
        ]
    );
    scorecardGridConfig.value = getGridConfig(
        'workspaceOid',
        [],
        [
            { label: 'Workspace', field: 'workspaceDisplay' },
            { label: 'Period Type', field: 'periodType' },
            { label: 'Period', field: 'periodKey' },
            { label: 'Score', field: 'scoreDisplay' },
            { label: 'KPI Count', field: 'kpiCount' },
            { label: 'OKR Count', field: 'okrCount' },
            { label: 'Themes', field: 'themeCount' },
            { label: 'Objectives', field: 'objectiveCount' }
        ]
    );
    delayedActionGridConfig.value = getGridConfig(
        'oid',
        [],
        [
            { label: 'Plan', field: 'planDisplay' },
            { label: 'Item', field: 'itemName' },
            { label: 'Stage', field: 'actionStage' },
            { label: 'Status', field: 'status' },
            { label: 'Progress', field: 'progressDisplay' },
            { label: 'End Date', field: 'endDateDisplay' },
            { label: 'Owners', field: 'ownerSummary' },
            { label: 'Sources', field: 'sourceSummary' }
        ]
    );
    atRiskObjectiveGridConfig.value = getGridConfig(
        'objectiveOid',
        [],
        [
            { label: 'Objective Code', field: 'objectiveCode' },
            { label: 'Objective Name', field: 'objectiveName' },
            { label: 'Period', field: 'periodKey' },
            { label: 'Progress', field: 'progressDisplay' },
            { label: 'Confidence', field: 'confidenceDisplay' },
            { label: 'Status', field: 'scoreStatus' },
            { label: 'Cycle OID', field: 'cycleOid' }
        ]
    );
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

<div v-if="dashboard">
  <ul class="nav nav-tabs mb-3">
    <li class="nav-item">
      <button type="button" class="nav-link" :class="{ active: activeTab === 'overview' }" @click="activeTab = 'overview'">Overview</button>
    </li>
    <li class="nav-item">
      <button type="button" class="nav-link" :class="{ active: activeTab === 'organization' }" @click="activeTab = 'organization'">Organization</button>
    </li>
    <li class="nav-item">
      <button type="button" class="nav-link" :class="{ active: activeTab === 'scorecard' }" @click="activeTab = 'scorecard'">Scorecard</button>
    </li>
    <li class="nav-item">
      <button type="button" class="nav-link" :class="{ active: activeTab === 'delayed' }" @click="activeTab = 'delayed'">Delayed Actions</button>
    </li>
    <li class="nav-item">
      <button type="button" class="nav-link" :class="{ active: activeTab === 'risk' }" @click="activeTab = 'risk'">At-risk Objectives</button>
    </li>
  </ul>

  <div v-show="activeTab === 'overview'">
    <div class="row g-3 mb-4">
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
    <div class="d-flex justify-content-between align-items-center mb-2">
      <div class="fw-semibold">Management Alerts</div>
      <div class="small text-muted">{{ alertList.length }} records</div>
    </div>
    <Grid v-if="alertGridConfig" :progId="pageProgramId" :dataSource="alertList" :config="alertGridConfig" />
  </div>

  <div v-show="activeTab === 'organization'">
    <div class="d-flex justify-content-between align-items-center mb-2">
      <div class="fw-semibold">Organization Dashboard</div>
      <div class="small text-muted">{{ orgSummaryList.length }} records</div>
    </div>
    <Grid v-if="orgGridConfig" :progId="pageProgramId" :dataSource="orgSummaryList" :config="orgGridConfig" />
  </div>

  <div v-show="activeTab === 'scorecard'">
    <div class="d-flex justify-content-between align-items-center mb-2">
      <div class="fw-semibold">Scorecard / Strategy Report</div>
      <div class="small text-muted">{{ strategyScorecardList.length }} records</div>
    </div>
    <Grid v-if="scorecardGridConfig" :progId="pageProgramId" :dataSource="strategyScorecardList" :config="scorecardGridConfig" />
  </div>

  <div v-show="activeTab === 'delayed'">
    <div class="d-flex justify-content-between align-items-center mb-2">
      <div class="fw-semibold">Delayed Action View</div>
      <div class="small text-muted">{{ delayedActionList.length }} records</div>
    </div>
    <Grid v-if="delayedActionGridConfig" :progId="pageProgramId" :dataSource="delayedActionList" :config="delayedActionGridConfig" />
  </div>

  <div v-show="activeTab === 'risk'">
    <div class="d-flex justify-content-between align-items-center mb-2">
      <div class="fw-semibold">At-risk Objective View</div>
      <div class="small text-muted">{{ atRiskObjectiveList.length }} records</div>
    </div>
    <Grid v-if="atRiskObjectiveGridConfig" :progId="pageProgramId" :dataSource="atRiskObjectiveList" :config="atRiskObjectiveGridConfig" />
  </div>
</div>
</template>
