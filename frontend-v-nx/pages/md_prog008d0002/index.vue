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
import { useMdProg008d0002Store } from './QueryPageStore';
import {
    getAxiosInstance,
    getProgItem,
    getUrlPrefixFromProgItem,
    escapeQifuHtmlMsg
} from '../../components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';

definePageMeta({ middleware: ['auth'] });

const router = useRouter();
const queryPageStore = useMdProg008d0002Store();
const { showLoading, hideLoading, confirmFire } = useSwalLoading();

const pageProgramId = ref(PageConstants.QueryId);
const dsList = ref<any[]>([]);
const planList = ref<any[]>([]);
const qFieldShow = ref(true);

const statusOptions = [
    { value: 'DRAFT', label: 'Draft' },
    { value: 'ACTIVE', label: 'Active' },
    { value: 'CLOSED', label: 'Closed' },
    { value: 'ARCHIVED', label: 'Archived' }
];
const stageOptions = [
    { value: 'PLAN', label: 'Plan' },
    { value: 'DO', label: 'Do' },
    { value: 'CHECK', label: 'Check' },
    { value: 'ACT', label: 'Act' }
];
const statusQueryOptions = [{ value: '', label: 'All' }, ...statusOptions];
const stageQueryOptions = [{ value: '', label: 'All' }, ...stageOptions];
const statusName = (value: string) => (statusOptions.find((item) => item.value === value)?.label || value);
const stageName = (value: string) => (stageOptions.find((item) => item.value === value)?.label || value);
const planName = (oid: string) => {
    const item = planList.value.find((plan: any) => plan.oid === oid);
    return item ? item.planCode + ' - ' + item.planName : oid;
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

const initQueryGridConfig = () => getGridConfig(
    'oid',
    [
        {
            method : (val: any) => {
                const url = getUrlPrefixFromProgItem(getProgItem(PageConstants.EditId)) + '/' + val;
                router.push(url);
            },
            icon : 'pen',
            type : 'edit',
            memo : 'Edit current item.',
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
        { label: 'Action Plan', field: 'planName' },
        { label: 'Item Name', field: 'itemName' },
        { label: 'Stage', field: 'stageName' },
        { label: 'Status', field: 'statusName' },
        { label: 'Progress', field: 'progressDisplay' },
        { label: 'End Date', field: 'endDateDisplay' },
        { label: 'Done Date', field: 'doneDateDisplay' }
    ]
);

const rowView = (item: any) => ({
    ...item,
    planName : planName(item.planOid),
    stageName : stageName(item.actionStage),
    statusName : statusName(item.status),
    progressDisplay : item.progressValue == null ? '' : item.progressValue + '%',
    endDateDisplay : item.endDate ? String(item.endDate).slice(0, 10) : '',
    doneDateDisplay : item.doneDate ? String(item.doneDate).slice(0, 10) : ''
});

const loadPlanList = async () => {
    const axiosInstance = getAxiosInstance();
    const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findPlanList', {});
    if (response.data && import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
        planList.value = response.data.value || [];
    }
};

const btnQuery = async () => {
    showLoading();
    dsList.value = [];
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findPage', {
            field: {
                planOid : queryPageStore.queryParam.planOid,
                itemNameLike : queryPageStore.queryParam.itemName,
                actionStage : queryPageStore.queryParam.actionStage,
                status : queryPageStore.queryParam.status
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
    await loadPlanList();
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
        description="Action Item"
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
      <div class="col-md-4">
        <div class="form-group form-floating">
          <select class="form-select" id="planOid" v-model="queryPageStore.queryParam.planOid">
            <option value="">All</option>
            <option v-for="item in planList" :key="item.oid" :value="item.oid">{{ item.planCode }} - {{ item.planName }}</option>
          </select>
          <label for="planOid">Action Plan</label>
        </div>
      </div>
      <div class="col-md-3">
        <div class="form-group form-floating">
          <input type="text" class="form-control" id="itemName" placeholder="Item Name" v-model="queryPageStore.queryParam.itemName">
          <label for="itemName">Item Name</label>
        </div>
      </div>
      <div class="col-md-2">
        <div class="form-group form-floating">
          <select class="form-select" id="actionStage" v-model="queryPageStore.queryParam.actionStage">
            <option v-for="item in stageQueryOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
          </select>
          <label for="actionStage">Stage</label>
        </div>
      </div>
      <div class="col-md-2">
        <div class="form-group form-floating">
          <select class="form-select" id="status" v-model="queryPageStore.queryParam.status">
            <option v-for="item in statusQueryOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
          </select>
          <label for="status">Status</label>
        </div>
      </div>
      <div class="col-md-2 d-flex align-items-end gap-2">
        <button type="button" class="btn btn-primary flex-fill" @click="btnQuery"><i class="bi bi-search"></i> Query</button>
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
