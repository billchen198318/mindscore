<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { toast } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

import Toolbar from '@/components/Toolbar.vue';
import Grid from '@/components/Grid.vue';
import GridPagination from '@/components/GridPagination.vue';
import HiddenQueryFieldAlertInfo from '@/components/HiddenQueryFieldAlertInfo.vue';
import { PageConstants } from './config';
import { getGridConfig, setConfigRow, setConfigPage, setConfigTotal, resetConfigByOld } from '../../components/GridHelper';
import { useMdProg006d0002Store } from './QueryPageStore';
import {
    getAxiosInstance,
    getProgItem,
    getUrlPrefixFromProgItem,
    escapeQifuHtmlMsg
} from '../../components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';

definePageMeta({ middleware: ['auth'] });

const router = useRouter();
const queryPageStore = useMdProg006d0002Store();
const { showLoading, hideLoading, confirmFire } = useSwalLoading();

const pageProgramId = ref(PageConstants.QueryId);
const dsList = ref<any[]>([]);
const cycleList = ref<any[]>([]);
const qFieldShow = ref(true);

const cycleName = (cycleOid: string) => {
    const item = cycleList.value.find((cycle: any) => cycle.oid === cycleOid);
    return item ? item.cycleCode + ' - ' + item.cycleName : cycleOid;
};

const statusName = (status: string) => {
    const map: any = { DRAFT: 'Draft', ACTIVE: 'Active', CLOSED: 'Closed', ARCHIVED: 'Archived' };
    return map[status] || status;
};

const tbRefresh = () => btnClear();
const tbCreate = () => router.push(PageConstants.frontendNamespace + '/create');
const tbQueryFieldShow = () => qFieldShow.value = !qFieldShow.value;

const btnClear = () => {
    queryPageStore.clearData();
    dsList.value = [];
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

const initQueryGridConfig = () => {
    return getGridConfig(
        'oid',
        [
            {
                'method'  : (val: any) => {
                    const url = getUrlPrefixFromProgItem(getProgItem(PageConstants.EditId)) + '/' + val;
                    router.push(url);
                },
                'icon'    : 'pen',
                'type'    : 'edit',
                'memo'    : 'Edit current item.',
                'class'   : 'btn btn-info btn-sm'
            },
            {
                'method'  : (val: any) => {
                    confirmFire('Delete?', delItem, val);
                },
                'icon'    : 'trash',
                'type'    : 'delete',
                'memo'    : 'Delete current item.',
                'class'   : 'btn btn-danger btn-sm'
            }
        ],
        [
            { label: '<i class="bi bi-hand-index-thumb"></i>', field: 'oid', labHtml: true },
            { label: 'Cycle', field: 'cycleOid' },
            { label: 'Objective Code', field: 'objectiveCode' },
            { label: 'Objective Name', field: 'objectiveName' },
            { label: 'Progress', field: 'progressValue' },
            { label: 'Confidence', field: 'confidenceScore' },
            { label: 'Status', field: 'status' }
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

const btnQuery = async () => {
    showLoading();
    dsList.value = [];
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findPage', {
            "field": {
                "cycleOid"          : queryPageStore.queryParam.cycleOid,
                "objectiveCodeLike" : queryPageStore.queryParam.objectiveCode,
                "objectiveNameLike" : queryPageStore.queryParam.objectiveName,
                "status"            : queryPageStore.queryParam.status
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
                cycleOid: cycleName(item.cycleOid),
                status: statusName(item.status)
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

const delItem = async (oid: string) => {
    showLoading();
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/delete', { "oid": oid });
        hideLoading();
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
                toast.success(response.data.message);
            } else {
                toast.warning(response.data.message);
            }
            btnQuery();
        } else {
            toast.error('error, null');
            clearGridConfig();
        }
    } catch (e: any) {
        hideLoading();
        btnQuery();
        alert(e);
    }
};

onMounted(async () => {
    await loadCycleList();
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
        description="OKR Objective Query"
        refreshFlag="Y"
        @refreshMethod="tbRefresh"
        createFlag="Y"
        @createMethod="tbCreate"
        queryFieldShowSwitchFlag="Y"
        @queryFieldShowSwitcMethod="tbQueryFieldShow"
    />
  </div>
</div>

<HiddenQueryFieldAlertInfo :dataSource="dsList" :queryFieldShowFlag="qFieldShow" />

<div v-show="qFieldShow" class="card mb-4">
  <div class="card-body">
    <div class="row g-3">
      <div class="col-md-3">
        <div class="form-group form-floating">
          <select class="form-select" id="cycleOid" v-model="queryPageStore.queryParam.cycleOid">
            <option value="">All</option>
            <option v-for="item in cycleList" :key="item.oid" :value="item.oid">{{ item.cycleCode }} - {{ item.cycleName }}</option>
          </select>
          <label for="cycleOid">Cycle</label>
        </div>
      </div>
      <div class="col-md-3">
        <div class="form-group form-floating">
          <input type="text" class="form-control" id="objectiveCode" placeholder="Objective Code" v-model="queryPageStore.queryParam.objectiveCode">
          <label for="objectiveCode">Objective Code</label>
        </div>
      </div>
      <div class="col-md-3">
        <div class="form-group form-floating">
          <input type="text" class="form-control" id="objectiveName" placeholder="Objective Name" v-model="queryPageStore.queryParam.objectiveName">
          <label for="objectiveName">Objective Name</label>
        </div>
      </div>
      <div class="col-md-3">
        <div class="form-group form-floating">
          <select class="form-select" id="status" v-model="queryPageStore.queryParam.status">
            <option value="">All</option>
            <option value="DRAFT">Draft</option>
            <option value="ACTIVE">Active</option>
            <option value="CLOSED">Closed</option>
            <option value="ARCHIVED">Archived</option>
          </select>
          <label for="status">Status</label>
        </div>
      </div>
    </div>
    <div class="row mt-3">
      <div class="col-12 d-flex gap-2">
        <button type="button" class="btn btn-primary" @click="btnQuery"><i class="bi bi-search"></i> Query</button>
        <button type="button" class="btn btn-outline-secondary" @click="btnClear"><i class="bi bi-eraser"></i> Clear</button>
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
