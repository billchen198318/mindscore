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
import { useMdProg003d0002Store } from './QueryPageStore';
import {
    getAxiosInstance,
    getProgItem,
    getUrlPrefixFromProgItem,
    escapeQifuHtmlMsg
} from '../../components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';
import { yesNoName } from '@/types/MindScoreOptions';

definePageMeta({ middleware: ['auth'] });

const router = useRouter();
const queryPageStore = useMdProg003d0002Store();
const { showLoading, hideLoading, confirmFire } = useSwalLoading();

const pageProgramId = ref(PageConstants.QueryId);
const dsList = ref<any[]>([]);
const qFieldShow = ref(true);
const kpiList = ref<any[]>([]);

const scopeTypeOptions = [
    { value: 'GLOBAL', label: 'Global' },
    { value: 'KPI', label: 'KPI override' }
];
const colorTypeOptions = [
    { value: 'CUSTOM', label: 'Score range' },
    { value: 'DEFAULT', label: 'Default fallback' }
];
const scoreStatusOptions = [
    { value: 'GOOD', label: 'Good' },
    { value: 'WARNING', label: 'Warning' },
    { value: 'BAD', label: 'Bad' },
    { value: 'UNKNOWN', label: 'Unknown' }
];
const withAll = (options: any[]) => [{ value: '', label: 'All' }, ...options];
const optionName = (options: any[], value: string) => options.find((item: any) => item.value === value)?.label || value;
const kpiName = (oid: string) => {
    if (!oid) return '';
    const item = kpiList.value.find((kpi: any) => kpi.oid === oid);
    return item ? item.kpiCode + ' - ' + item.kpiName : oid;
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
        { label: 'Scope', field: 'scopeName' },
        { label: 'KPI', field: 'kpiName' },
        { label: 'Type', field: 'colorTypeName' },
        { label: 'Code', field: 'colorCode' },
        { label: 'Name', field: 'colorName' },
        { label: 'Range', field: 'rangeText' },
        { label: 'Status', field: 'scoreStatusName' },
        { label: 'Preview', field: 'previewHtml', labHtml: true },
        { label: 'Sort', field: 'sortNo' },
        { label: 'Enabled', field: 'enabledName' }
    ]
);

const rowView = (item: any) => ({
    ...item,
    scopeName : optionName(scopeTypeOptions, item.scopeType),
    kpiName : item.scopeType === 'KPI' ? kpiName(item.kpiOid) : 'Global',
    colorTypeName : optionName(colorTypeOptions, item.colorType),
    scoreStatusName : optionName(scoreStatusOptions, item.scoreStatus),
    rangeText : item.colorType === 'CUSTOM' ? item.scoreMin + ' - ' + item.scoreMax : 'Default',
    enabledName : yesNoName(item.enabled),
    previewHtml : '<span style="display:inline-block;min-width:64px;padding:2px 8px;border-radius:4px;color:' + item.fontColor + ';background:' + item.bgColor + ';">TEST</span>'
});

const loadKpiList = async () => {
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findKpiList', {});
        if (response.data && import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
            kpiList.value = (response.data.value || []).filter((item: any) => item.enabled === 'Y');
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
            field: {
                scopeType : queryPageStore.queryParam.scopeType,
                kpiOid : queryPageStore.queryParam.kpiOid,
                colorType : queryPageStore.queryParam.colorType,
                colorCodeLike : queryPageStore.queryParam.colorCode,
                colorNameLike : queryPageStore.queryParam.colorName,
                scoreStatus : queryPageStore.queryParam.scoreStatus,
                enabled : queryPageStore.queryParam.enabled
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
        }
    } catch (e: any) {
        hideLoading();
        alert(e);
    }
};

onMounted(async () => {
    await loadKpiList();
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
        description="KPI Score Color"
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
      <div class="col-md-2">
        <div class="form-group form-floating">
          <select class="form-select" id="scopeType" v-model="queryPageStore.queryParam.scopeType">
            <option v-for="item in withAll(scopeTypeOptions)" :key="item.value" :value="item.value">{{ item.label }}</option>
          </select>
          <label for="scopeType">Scope</label>
        </div>
      </div>
      <div class="col-md-3">
        <div class="form-group form-floating">
          <select class="form-select" id="kpiOid" v-model="queryPageStore.queryParam.kpiOid">
            <option value="">All</option>
            <option v-for="item in kpiList" :key="item.oid" :value="item.oid">{{ item.kpiCode }} - {{ item.kpiName }}</option>
          </select>
          <label for="kpiOid">KPI</label>
        </div>
      </div>
      <div class="col-md-2">
        <div class="form-group form-floating">
          <select class="form-select" id="colorType" v-model="queryPageStore.queryParam.colorType">
            <option v-for="item in withAll(colorTypeOptions)" :key="item.value" :value="item.value">{{ item.label }}</option>
          </select>
          <label for="colorType">Type</label>
        </div>
      </div>
      <div class="col-md-2">
        <div class="form-group form-floating">
          <select class="form-select" id="scoreStatus" v-model="queryPageStore.queryParam.scoreStatus">
            <option v-for="item in withAll(scoreStatusOptions)" :key="item.value" :value="item.value">{{ item.label }}</option>
          </select>
          <label for="scoreStatus">Status</label>
        </div>
      </div>
      <div class="col-md-3">
        <div class="form-group form-floating">
          <input type="text" class="form-control" id="colorCode" placeholder="Code" v-model="queryPageStore.queryParam.colorCode">
          <label for="colorCode">Code</label>
        </div>
      </div>
      <div class="col-md-3">
        <div class="form-group form-floating">
          <input type="text" class="form-control" id="colorName" placeholder="Name" v-model="queryPageStore.queryParam.colorName">
          <label for="colorName">Name</label>
        </div>
      </div>
      <div class="col-md-2">
        <div class="form-group form-floating">
          <select class="form-select" id="enabled" v-model="queryPageStore.queryParam.enabled">
            <option value="">All</option>
            <option value="Y">Yes</option>
            <option value="N">No</option>
          </select>
          <label for="enabled">Enabled</label>
        </div>
      </div>
      <div class="col-md-2 d-flex align-items-end">
        <button type="button" class="btn btn-primary w-100" @click="btnQuery"><i class="bi bi-search"></i> Query</button>
      </div>
      <div class="col-md-2 d-flex align-items-end">
        <button type="button" class="btn btn-outline-secondary w-100" @click="btnClear"><i class="bi bi-eraser"></i> Clear</button>
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
