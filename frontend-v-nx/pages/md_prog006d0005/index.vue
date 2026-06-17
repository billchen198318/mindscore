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
import { useMdProg006d0005Store } from './QueryPageStore';
import {
    getAxiosInstance,
    escapeQifuHtmlMsg
} from '../../components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';

definePageMeta({ middleware: ['auth'] });

const queryPageStore = useMdProg006d0005Store();
const { showLoading, hideLoading } = useSwalLoading();

const pageProgramId = ref(PageConstants.QueryId);
const dsList = ref<any[]>([]);
const cycleList = ref<any[]>([]);
const objectiveList = ref<any[]>([]);
const orgList = ref<any[]>([]);
const memberList = ref<any[]>([]);
const selectedSnapshot = ref<any>(null);
const qFieldShow = ref(true);

const scoreStatusList = [
    { value: '', label: 'All' },
    { value: 'GOOD', label: 'GOOD' },
    { value: 'WARNING', label: 'WARNING' },
    { value: 'BAD', label: 'BAD' },
    { value: 'UNKNOWN', label: 'UNKNOWN' }
];

const objectiveName = (objectiveOid: string) => {
    const item = objectiveList.value.find((objective: any) => objective.oid === objectiveOid);
    return item ? item.objectiveCode + ' - ' + item.objectiveName : objectiveOid;
};

const orgName = (orgOid: string) => {
    const item = orgList.value.find((org: any) => org.oid === orgOid);
    return item ? item.orgCode + ' - ' + item.orgName : orgOid;
};

const accountName = (account: string) => {
    const item = memberList.value.find((member: any) => member.account === account);
    return item ? item.account + (item.displayName ? ' - ' + item.displayName : '') : account;
};

const showSnapshotDetail = (oid: string) => {
    const item = dsList.value.find((row: any) => row.oid === oid);
    if (!item) {
        return;
    }
    selectedSnapshot.value = item;
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
    objectiveList.value = [];
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
                'memo'    : 'Snapshot detail'
            }
        ],
        [
            { label: '<i class="bi bi-hand-index-thumb"></i>', field: 'oid', labHtml: true },
            { label: 'Objective', field: 'objectiveOid' },
            { label: 'Period', field: 'periodKey' },
            { label: 'Progress', field: 'progressValue' },
            { label: 'Confidence', field: 'confidenceScore' },
            { label: 'Status', field: 'scoreStatus' },
            { label: 'Snapshot At', field: 'snapshotAt' }
        ]
    );
};

const loadCycleList = async () => {
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findCycleList', {});
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            cycleList.value = response.data.value || [];
        }
    } catch (e: any) {
        toast.warning(e?.message || e);
    }
};

const loadObjectiveList = async () => {
    objectiveList.value = [];
    queryPageStore.queryParam.objectiveOid = '';
    if (!queryPageStore.queryParam.cycleOid) {
        return;
    }
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findObjectiveList', {
            cycleOid: queryPageStore.queryParam.cycleOid
        });
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            objectiveList.value = response.data.value || [];
        }
    } catch (e: any) {
        toast.warning(e?.message || e);
    }
};

const loadOrgList = async () => {
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findOrgList', {});
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            orgList.value = (response.data.value || []).filter((item: any) => item.enabled === 'Y');
        }
    } catch (e: any) {
        toast.warning(e?.message || e);
    }
};

const loadMemberList = async () => {
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findMemberList', {});
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            const seen: Record<string, boolean> = {};
            memberList.value = (response.data.value || []).filter((item: any) => {
                if (item.enabled !== 'Y' || !item.account || seen[item.account]) {
                    return false;
                }
                seen[item.account] = true;
                return true;
            });
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
                "cycleOid"      : queryPageStore.queryParam.cycleOid,
                "objectiveOid"  : queryPageStore.queryParam.objectiveOid,
                "periodKeyFrom" : queryPageStore.queryParam.periodKeyFrom,
                "periodKeyTo"   : queryPageStore.queryParam.periodKeyTo,
                "scoreStatus"   : queryPageStore.queryParam.scoreStatus,
                "orgOid"        : queryPageStore.queryParam.orgOid,
                "account"       : queryPageStore.queryParam.account
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
            dsList.value = response.data.value.map((item: any) => ({ ...item, objectiveOid: objectiveName(item.objectiveOid) }));
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
    await Promise.all([loadCycleList(), loadOrgList(), loadMemberList()]);
    const newGridConfig = initQueryGridConfig();
    if (queryPageStore.gridConfig.column) {
        resetConfigByOld(newGridConfig, queryPageStore.gridConfig);
    }
    queryPageStore.gridConfig = newGridConfig;
});

watch(() => queryPageStore.queryParam.cycleOid, () => loadObjectiveList());
</script>

<template>
<div class="row">
  <div class="col-12">
    <Toolbar
        :progId="pageProgramId"
        description="OKR Snapshot"
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
        <label for="cycleOid" class="form-label">Cycle</label>
        <select class="form-select" id="cycleOid" v-model="queryPageStore.queryParam.cycleOid">
          <option value="">All</option>
          <option v-for="item in cycleList" :key="item.oid" :value="item.oid">{{ item.cycleCode }} - {{ item.cycleName }}</option>
        </select>
      </div>
      <div class="col-md-4">
        <label for="objectiveOid" class="form-label">Objective</label>
        <select class="form-select" id="objectiveOid" v-model="queryPageStore.queryParam.objectiveOid">
          <option value="">All</option>
          <option v-for="item in objectiveList" :key="item.oid" :value="item.oid">{{ item.objectiveCode }} - {{ item.objectiveName }}</option>
        </select>
      </div>
      <div class="col-md-4">
        <label for="scoreStatus" class="form-label">Status</label>
        <select class="form-select" id="scoreStatus" v-model="queryPageStore.queryParam.scoreStatus">
          <option v-for="item in scoreStatusList" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
      </div>
      <div class="col-md-4">
        <label for="orgOid" class="form-label">Organization Owner</label>
        <select class="form-select" id="orgOid" v-model="queryPageStore.queryParam.orgOid">
          <option value="">All</option>
          <option v-for="item in orgList" :key="item.oid" :value="item.oid">{{ orgName(item.oid) }}</option>
        </select>
      </div>
      <div class="col-md-4">
        <label for="account" class="form-label">Account Owner</label>
        <select class="form-select" id="account" v-model="queryPageStore.queryParam.account">
          <option value="">All</option>
          <option v-for="item in memberList" :key="item.account" :value="item.account">{{ accountName(item.account) }}</option>
        </select>
      </div>
      <div class="col-md-4">
        <div class="form-group form-floating">
          <input type="date" class="form-control" id="periodKeyFrom" v-model="queryPageStore.queryParam.periodKeyFrom">
          <label for="periodKeyFrom">Period From</label>
        </div>
      </div>
      <div class="col-md-4">
        <div class="form-group form-floating">
          <input type="date" class="form-control" id="periodKeyTo" v-model="queryPageStore.queryParam.periodKeyTo">
          <label for="periodKeyTo">Period To</label>
        </div>
      </div>
      <div class="col-md-4 d-flex align-items-end gap-2">
        <button type="button" class="btn btn-primary" @click="btnQuery"><i class="bi bi-search"></i> Query</button>
        <button type="button" class="btn btn-outline-secondary" @click="btnClear"><i class="bi bi-eraser"></i> Clear</button>
      </div>
    </div>
  </div>
</div>

<div class="row">
    <div class="col-12">
        <div v-if="selectedSnapshot" class="alert alert-secondary mb-3">
            <div class="d-flex justify-content-between align-items-start gap-3">
                <div>
                    <div class="fw-bold">{{ selectedSnapshot.objectiveOid }}</div>
                    <div class="small text-muted">{{ selectedSnapshot.periodKey }} / {{ selectedSnapshot.snapshotAt }}</div>
                </div>
                <button type="button" class="btn-close" aria-label="Close" @click="selectedSnapshot = null"></button>
            </div>
            <div class="row g-2 mt-2">
                <div class="col-md-3"><span class="text-muted">Progress</span> {{ selectedSnapshot.progressValue }}</div>
                <div class="col-md-3"><span class="text-muted">Confidence</span> {{ selectedSnapshot.confidenceScore }}</div>
                <div class="col-md-3"><span class="text-muted">Status</span> {{ selectedSnapshot.scoreStatus }}</div>
            </div>
            <pre v-if="selectedSnapshot.calculationTrace" class="mt-2 mb-0 small text-wrap">{{ selectedSnapshot.calculationTrace }}</pre>
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
