<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { toast } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

import Toolbar from '@/components/Toolbar.vue';
import Grid from '@/components/Grid.vue';
import GridPagination from '@/components/GridPagination.vue';
import HiddenQueryFieldAlertInfo from '@/components/HiddenQueryFieldAlertInfo.vue';
import { PageConstants } from './config';
import { getGridConfig, setConfigRow, setConfigPage, setConfigTotal, resetConfigByOld } from '../../components/GridHelper';
import { useMdProg007d0005Store } from './QueryPageStore';
import {
    getAxiosInstance,
    escapeQifuHtmlMsg
} from '../../components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';

definePageMeta({ middleware: ['auth'] });

const queryPageStore = useMdProg007d0005Store();
const { showLoading, hideLoading } = useSwalLoading();

const pageProgramId = ref(PageConstants.QueryId);
const dsList = ref<any[]>([]);
const workspaceList = ref<any[]>([]);
const selectedSnapshot = ref<any>(null);
const qFieldShow = ref(true);

const periodTypeList = [
    { value: '', label: 'All' },
    { value: 'MONTH', label: 'MONTH' },
    { value: 'QUARTER', label: 'QUARTER' },
    { value: 'YEAR', label: 'YEAR' },
    { value: 'CUSTOM', label: 'CUSTOM' }
];

const workspaceName = (workspaceOid: string) => {
    const item = workspaceList.value.find((workspace: any) => workspace.oid === workspaceOid);
    return item ? `${item.workspaceCode} - ${item.workspaceName}` : workspaceOid;
};

const showSnapshotDetail = async (oid: string) => {
    selectedSnapshot.value = null;
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
            selectedSnapshot.value = {
                ...response.data.value,
                workspaceName: workspaceName(response.data.value.workspaceOid)
            };
        }
    } catch (e: any) {
        hideLoading();
        toast.warning(e?.message || e);
    }
};

const tbRefresh = () => btnClear();
const tbQueryFieldShow = () => qFieldShow.value = !qFieldShow.value;

const clearGridConfig = () => {
    setConfigRow(queryPageStore.gridConfig, import.meta.env.VITE_DEFAULT_ROW);
    setConfigPage(queryPageStore.gridConfig, 1);
    setConfigTotal(queryPageStore.gridConfig, 0);
};

const btnClear = () => {
    queryPageStore.clearData();
    dsList.value = [];
    selectedSnapshot.value = null;
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

const initQueryGridConfig = () => {
    return getGridConfig(
        'oid',
        [
            {
                'type'    : 'detail',
                'method'  : showSnapshotDetail,
                'icon'    : 'diagram-3',
                'class'   : 'btn btn-sm btn-outline-primary',
                'memo'    : 'Snapshot evidence'
            }
        ],
        [
            { label: '<i class="bi bi-hand-index-thumb"></i>', field: 'oid', labHtml: true },
            { label: 'Workspace', field: 'workspaceName' },
            { label: 'Period Type', field: 'periodType' },
            { label: 'Period Key', field: 'periodKey' },
            { label: 'Score', field: 'scoreValue' },
            { label: 'KPI Count', field: 'kpiCount' },
            { label: 'OKR Count', field: 'okrCount' },
            { label: 'Snapshot At', field: 'snapshotAt' }
        ]
    );
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

const btnQuery = async () => {
    showLoading();
    dsList.value = [];
    selectedSnapshot.value = null;
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findPage', {
            "field": {
                "workspaceOid" : queryPageStore.queryParam.workspaceOid,
                "periodType"   : queryPageStore.queryParam.periodType,
                "periodKey"    : queryPageStore.queryParam.periodKey
            },
            "pageOf": {
                "select"  : queryPageStore.gridConfig.page,
                "showRow" : queryPageStore.gridConfig.row
            }
        });
        hideLoading();
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                clearGridConfig();
                toast.warning(response.data.message);
                return;
            }
            dsList.value = response.data.value.map((item: any) => ({
                ...item,
                workspaceName: workspaceName(item.workspaceOid)
            }));
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

onMounted(async () => {
    await loadWorkspaceList();
    const newGridConfig = initQueryGridConfig();
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
        description="Strategy Snapshot Evidence"
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
        <label for="workspaceOid" class="form-label">Workspace</label>
        <select class="form-select" id="workspaceOid" v-model="queryPageStore.queryParam.workspaceOid">
          <option value="">All</option>
          <option v-for="item in workspaceList" :key="item.oid" :value="item.oid">{{ item.workspaceCode }} - {{ item.workspaceName }}</option>
        </select>
      </div>
      <div class="col-md-4">
        <label for="periodType" class="form-label">Period Type</label>
        <select class="form-select" id="periodType" v-model="queryPageStore.queryParam.periodType">
          <option v-for="item in periodTypeList" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
      </div>
      <div class="col-md-4">
        <label for="periodKey" class="form-label">Period Key</label>
        <input type="text" class="form-control" id="periodKey" v-model="queryPageStore.queryParam.periodKey">
      </div>
      <div class="col-12 d-flex gap-2">
        <button type="button" class="btn btn-primary" @click="btnQuery"><i class="bi bi-search"></i> Query</button>
        <button type="button" class="btn btn-outline-secondary" @click="btnClear"><i class="bi bi-eraser"></i> Clear</button>
      </div>
    </div>
  </div>
</div>

<div class="row">
    <div class="col-12">
        <div v-if="selectedSnapshot" class="border rounded p-3 mb-3 bg-light">
            <div class="d-flex justify-content-between align-items-start gap-3 mb-3">
                <div>
                    <div class="fw-bold">{{ selectedSnapshot.workspaceName }}</div>
                    <div class="small text-muted">{{ selectedSnapshot.periodType }} / {{ selectedSnapshot.periodKey }} / {{ selectedSnapshot.snapshotAt }}</div>
                </div>
                <button type="button" class="btn-close" aria-label="Close" @click="selectedSnapshot = null"></button>
            </div>
            <div class="row g-2 mb-3">
                <div class="col-md-3"><span class="text-muted">Score</span> {{ selectedSnapshot.scoreValue }}</div>
                <div class="col-md-3"><span class="text-muted">KPI Count</span> {{ selectedSnapshot.kpiCount }}</div>
                <div class="col-md-3"><span class="text-muted">OKR Count</span> {{ selectedSnapshot.okrCount }}</div>
                <div class="col-md-3"><span class="text-muted">Created By</span> {{ selectedSnapshot.cuserid }}</div>
            </div>
            <div class="small text-muted mb-2">Calculation Trace</div>
            <pre class="mb-0 small text-wrap">{{ selectedSnapshot.calculationTrace || 'No calculation trace.' }}</pre>
        </div>
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
