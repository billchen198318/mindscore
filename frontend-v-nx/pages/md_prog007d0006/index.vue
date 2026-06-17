<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { toast } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

import Toolbar from '@/components/Toolbar.vue';
import HiddenQueryFieldAlertInfo from '@/components/HiddenQueryFieldAlertInfo.vue';
import { PageConstants } from './config';
import { useMdProg007d0006Store } from './QueryPageStore';
import {
    getAxiosInstance,
    escapeQifuHtmlMsg
} from '../../components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';

definePageMeta({ middleware: ['auth'] });

const queryPageStore = useMdProg007d0006Store();
const { showLoading, hideLoading } = useSwalLoading();

const pageProgramId = ref(PageConstants.QueryId);
const workspaceList = ref<any[]>([]);
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

const tbRefresh = () => btnClear();
const tbQueryFieldShow = () => qFieldShow.value = !qFieldShow.value;

const formatScore = (value: any) => {
    if (value === null || value === undefined || value === '') {
        return '-';
    }
    const numberValue = Number(value);
    return Number.isNaN(numberValue) ? value : numberValue.toFixed(2);
};

const btnClear = () => {
    queryPageStore.clearData();
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

const btnGenerate = async () => {
    showLoading();
    report.value = null;
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/generate', {
            workspaceOid : queryPageStore.queryParam.workspaceOid,
            periodType   : queryPageStore.queryParam.periodType,
            periodKey    : queryPageStore.queryParam.periodKey
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
    await loadWorkspaceList();
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
        <label for="periodKey" class="form-label">Period Key</label>
        <input type="text" class="form-control" id="periodKey" v-model="queryPageStore.queryParam.periodKey">
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
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="objective in theme.objectiveList" :key="objective.objective.oid">
                            <td>
                                <div class="fw-semibold">{{ objective.objective.objectiveCode }} - {{ objective.objective.objectiveName }}</div>
                                <div class="small text-muted" v-for="linkView in objective.linkList" :key="linkView.link.oid">
                                    {{ linkView.link.linkType }} / {{ linkView.sourceCode }} - {{ linkView.sourceName || 'Missing source' }}
                                    / score {{ formatScore(linkView.scoreValue) }}
                                    <span v-if="linkView.missingScore" class="text-danger">missing score</span>
                                </div>
                            </td>
                            <td>{{ objective.objective.weightValue }}</td>
                            <td>{{ formatScore(objective.scoreValue) }}</td>
                            <td>{{ objective.kpiCount }}</td>
                            <td>{{ objective.okrCount }}</td>
                            <td>{{ objective.linkList.length }}</td>
                        </tr>
                        <tr v-if="theme.objectiveList.length < 1">
                            <td colspan="6" class="text-muted">No objectives.</td>
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
