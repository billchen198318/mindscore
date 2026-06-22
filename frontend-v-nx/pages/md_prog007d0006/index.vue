<script setup lang="ts">
import { computed, ref, onMounted, watch } from 'vue';
import { toast } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

import Toolbar from '@/components/Toolbar.vue';
import HiddenQueryFieldAlertInfo from '@/components/HiddenQueryFieldAlertInfo.vue';
import PeriodPicker from '@/components/PeriodPicker.vue';
import { PageConstants } from './config';
import { useMdProg007d0006Store } from './QueryPageStore';
import {
    getAxiosInstance,
    escapeQifuHtmlMsg
} from '../../components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';
import { useActionSourceNavigation } from '@/composables/useActionSourceNavigation';

definePageMeta({ middleware: ['auth'] });

const queryPageStore = useMdProg007d0006Store();
const { showLoading, hideLoading } = useSwalLoading();
const { createActionFromSource } = useActionSourceNavigation();

const pageProgramId = ref(PageConstants.QueryId);
const workspaceList = ref<any[]>([]);
const orgList = ref<any[]>([]);
const memberList = ref<any[]>([]);
const report = ref<any>(null);
const qFieldShow = ref(true);

const periodTypeList = [
    { value: 'DAY', label: 'DAY' },
    { value: 'WEEK', label: 'WEEK' },
    { value: 'MONTH', label: 'MONTH' },
    { value: 'QUARTER', label: 'QUARTER' },
    { value: 'HALFYEAR', label: 'HALFYEAR' },
    { value: 'YEAR', label: 'YEAR' }
];
const dataForTypeOptions = [
    { value: 'GLOBAL', label: 'Global' },
    { value: 'ORG', label: 'Organization' },
    { value: 'ACCOUNT', label: 'Account' }
];

const tbRefresh = () => btnClear();
const tbQueryFieldShow = () => qFieldShow.value = !qFieldShow.value;
const showAccountFilter = computed(() => queryPageStore.queryParam.dataForType === 'ACCOUNT');
const showOrgFilter = computed(() => queryPageStore.queryParam.dataForType === 'ORG');
const periodPickerType = computed(() => queryPageStore.queryParam.periodType);

const pad2 = (value: number) => String(value).padStart(2, '0');
const weekOfYear = (date: Date) => {
    const d = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
    d.setUTCDate(d.getUTCDate() + 4 - (d.getUTCDay() || 7));
    const yearStart = new Date(Date.UTC(d.getUTCFullYear(), 0, 1));
    return {
        year : d.getUTCFullYear(),
        week : Math.ceil((((d.getTime() - yearStart.getTime()) / 86400000) + 1) / 7)
    };
};
const defaultPeriodKey = () => {
    const now = new Date();
    if (periodPickerType.value === 'DAY') {
        return now.toISOString().slice(0, 10);
    }
    if (periodPickerType.value === 'WEEK') {
        const info = weekOfYear(now);
        return info.year + '-W' + pad2(info.week);
    }
    if (periodPickerType.value === 'MONTH') {
        return now.toISOString().slice(0, 7);
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

const formatScore = (value: any) => {
    if (value === null || value === undefined || value === '') {
        return '-';
    }
    const numberValue = Number(value);
    return Number.isNaN(numberValue) ? value : numberValue.toFixed(2);
};
const accountName = (account: string) => {
    const item = memberList.value.find((member: any) => member.account === account);
    return item ? item.account + (item.displayName ? ' - ' + item.displayName : '') : account;
};
const orgName = (oid: string) => {
    const item = orgList.value.find((org: any) => org.oid === oid);
    return item ? item.orgCode + ' - ' + item.orgName : oid;
};
const snapshotScopeText = (linkView: any) => {
    if (!linkView || !linkView.dataForType) {
        return '';
    }
    if (linkView.dataForType === 'ACCOUNT') {
        return 'Account: ' + accountName(linkView.account);
    }
    if (linkView.dataForType === 'ORG') {
        return 'Organization: ' + orgName(linkView.orgOid);
    }
    return 'Global';
};
const calculatedAtText = (value: any) => value ? String(value).replace('T', ' ').slice(0, 19) : '';
const createStrategyAction = async (objective: any) => {
    const name = objective.objectiveCode + ' - ' + objective.objectiveName;
    if (!await createActionFromSource('STRATEGY', objective.oid, name)) {
        toast.warning('You do not have permission to create an Action Plan.');
    }
};

const btnClear = () => {
    queryPageStore.clearData();
    queryPageStore.queryParam.periodKey = defaultPeriodKey();
    report.value = null;
};

const loadWorkspaceList = async () => {
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findWorkspaceList', {});
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            workspaceList.value = response.data.value || [];
        }
    } catch (e: any) {
        toast.warning(e?.message || e);
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

const btnGenerate = async () => {
    if (!queryPageStore.queryParam.periodKey) {
        toast.warning('Please select period.');
        return;
    }
    if (queryPageStore.queryParam.dataForType === 'ACCOUNT' && !queryPageStore.queryParam.account) {
        toast.warning('Please select account.');
        return;
    }
    if (queryPageStore.queryParam.dataForType === 'ORG' && !queryPageStore.queryParam.orgOid) {
        toast.warning('Please select organization.');
        return;
    }
    showLoading();
    report.value = null;
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/generate', {
            workspaceOid : queryPageStore.queryParam.workspaceOid,
            periodType   : queryPageStore.queryParam.periodType,
            periodKey    : queryPageStore.queryParam.periodKey,
            dataForType  : queryPageStore.queryParam.dataForType,
            account      : queryPageStore.queryParam.dataForType === 'ACCOUNT' ? queryPageStore.queryParam.account : '',
            orgOid       : queryPageStore.queryParam.dataForType === 'ORG' ? queryPageStore.queryParam.orgOid : ''
        });
        hideLoading();
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            report.value = response.data.value;
        }
    } catch (e: any) {
        hideLoading();
        toast.warning(e?.message || e);
    }
};

onMounted(async () => {
    if (!queryPageStore.queryParam.periodKey) {
        queryPageStore.queryParam.periodKey = defaultPeriodKey();
    }
    await Promise.all([loadWorkspaceList(), loadOrgList(), loadMemberList()]);
});

watch(() => queryPageStore.queryParam.dataForType, (value) => {
    if (value !== 'ACCOUNT') {
        queryPageStore.queryParam.account = '';
    }
    if (value !== 'ORG') {
        queryPageStore.queryParam.orgOid = '';
    }
});

watch(periodPickerType, () => {
    queryPageStore.queryParam.periodKey = defaultPeriodKey();
});
</script>

<template>
<div class="row">
  <div class="col-12">
    <Toolbar
        :progId="pageProgramId"
        description="Strategy / BSC Report"
        refreshFlag="Y"
        @refreshMethod="tbRefresh"
        queryFieldShowSwitchFlag="Y"
        @queryFieldShowSwitcMethod="tbQueryFieldShow"
    />
  </div>
</div>

<HiddenQueryFieldAlertInfo :dataSource="report ? [report] : []" :queryFieldShowFlag="qFieldShow" />

<div v-show="qFieldShow" class="card mb-4">
  <div class="card-body">
    <div class="row g-3">
      <div class="col-md-4">
        <label for="workspaceOid" class="form-label">Workspace</label>
        <select class="form-select" id="workspaceOid" v-model="queryPageStore.queryParam.workspaceOid">
          <option value="">Please select</option>
          <option v-for="item in workspaceList" :key="item.oid" :value="item.oid">{{ item.workspaceCode }} - {{ item.workspaceName }}</option>
        </select>
      </div>
      <div class="col-md-3">
        <label for="periodType" class="form-label">Period Type</label>
        <select class="form-select" id="periodType" v-model="queryPageStore.queryParam.periodType">
          <option v-for="item in periodTypeList" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
      </div>
      <div class="col-md-3">
        <PeriodPicker label="Period" :periodType="queryPageStore.queryParam.periodType" v-model="queryPageStore.queryParam.periodKey" />
        <div class="form-text">Generated as the period key used by report snapshots.</div>
      </div>
      <div class="col-md-3">
        <label for="dataForType" class="form-label">Data For</label>
        <select class="form-select" id="dataForType" v-model="queryPageStore.queryParam.dataForType">
          <option v-for="item in dataForTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
      </div>
      <div v-if="showAccountFilter" class="col-md-3">
        <label for="account" class="form-label">Account</label>
        <select class="form-select" id="account" v-model="queryPageStore.queryParam.account">
          <option value="">Please select</option>
          <option v-for="item in memberList" :key="item.account" :value="item.account">{{ accountName(item.account) }}</option>
        </select>
      </div>
      <div v-if="showOrgFilter" class="col-md-3">
        <label for="orgOid" class="form-label">Organization</label>
        <select class="form-select" id="orgOid" v-model="queryPageStore.queryParam.orgOid">
          <option value="">Please select</option>
          <option v-for="item in orgList" :key="item.oid" :value="item.oid">{{ orgName(item.oid) }}</option>
        </select>
      </div>
      <div class="col-md-2 d-flex align-items-end gap-2">
        <button type="button" class="btn btn-primary w-100" @click="btnGenerate"><i class="bi bi-bar-chart-line"></i> Generate</button>
      </div>
    </div>
  </div>
</div>

<div v-if="report" class="row g-3">
    <div class="col-12">
        <div class="border rounded p-3 bg-light">
            <div class="d-flex justify-content-between align-items-start gap-3">
                <div>
                    <div class="fw-bold">{{ report.workspace.workspaceCode }} - {{ report.workspace.workspaceName }}</div>
                    <div class="small text-muted">{{ report.snapshot.periodType }} / {{ report.snapshot.periodKey }} / {{ report.snapshot.snapshotAt }}</div>
                </div>
                <div class="text-end">
                    <div class="display-6">{{ formatScore(report.snapshot.scoreValue) }}</div>
                    <div class="small text-muted">Strategy Score</div>
                </div>
            </div>
            <div class="row g-2 mt-3">
                <div class="col-md-3"><span class="text-muted">KPI Links</span> {{ report.snapshot.kpiCount }}</div>
                <div class="col-md-3"><span class="text-muted">OKR Links</span> {{ report.snapshot.okrCount }}</div>
                <div class="col-md-3"><span class="text-muted">Themes</span> {{ report.themeList.length }}</div>
                <div class="col-md-3"><span class="text-muted">Snapshot OID</span> {{ report.snapshot.oid }}</div>
            </div>
        </div>
    </div>

    <div class="col-12" v-for="theme in report.themeList" :key="theme.theme.oid">
        <div class="border rounded p-3">
            <div class="d-flex justify-content-between align-items-start gap-3 mb-3">
                <div>
                    <div class="fw-bold">{{ theme.theme.themeCode }} - {{ theme.theme.themeName }}</div>
                    <div class="small text-muted">Weight {{ theme.theme.weightValue }} / Objectives {{ theme.objectiveCount }}</div>
                </div>
                <div class="text-end">
                    <div class="h4 mb-0">{{ formatScore(theme.scoreValue) }}</div>
                    <div class="small text-muted">Theme Score</div>
                </div>
            </div>

            <div class="table-responsive">
                <table class="table table-sm align-middle">
                    <thead>
                        <tr>
                            <th>Objective</th>
                            <th>Weight</th>
                            <th>Score</th>
                            <th>KPI</th>
                            <th>OKR</th>
                            <th>Links</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="objective in theme.objectiveList" :key="objective.objective.oid">
                            <td>
                                <div class="fw-semibold">{{ objective.objective.objectiveCode }} - {{ objective.objective.objectiveName }}</div>
                                <div class="small text-muted" v-for="linkView in objective.linkList" :key="linkView.link.oid">
                                    {{ linkView.link.linkType }} / {{ linkView.sourceCode }} - {{ linkView.sourceName || 'Missing source' }}
                                    / score {{ formatScore(linkView.scoreValue) }}
                                    <span v-if="linkView.scoreStatus">/ {{ linkView.scoreStatus }}</span>
                                    <span v-if="snapshotScopeText(linkView)">/ {{ snapshotScopeText(linkView) }}</span>
                                    <span v-if="linkView.calculatedAt">/ {{ calculatedAtText(linkView.calculatedAt) }}</span>
                                    <span v-if="linkView.missingScore" class="text-danger">missing score</span>
                                </div>
                            </td>
                            <td>{{ objective.objective.weightValue }}</td>
                            <td>{{ formatScore(objective.scoreValue) }}</td>
                            <td>{{ objective.kpiCount }}</td>
                            <td>{{ objective.okrCount }}</td>
                            <td>{{ objective.linkList.length }}</td>
                            <td>
                                <button type="button" class="btn btn-sm btn-outline-success" @click="createStrategyAction(objective.objective)">
                                    <i class="bi bi-clipboard-plus"></i> Create
                                </button>
                            </td>
                        </tr>
                        <tr v-if="theme.objectiveList.length < 1">
                            <td colspan="7" class="text-muted">No objectives.</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div class="col-12">
        <div class="border rounded p-3 bg-light">
            <div class="small text-muted mb-2">Calculation Trace</div>
            <pre class="mb-0 small text-wrap">{{ report.snapshot.calculationTrace }}</pre>
        </div>
    </div>
</div>
</template>
