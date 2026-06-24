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
import { useMdProg003d0001Store } from './QueryPageStore';
import {
    getAxiosInstance,
    getProgItem,
    getUrlPrefixFromProgItem,
    escapeQifuHtmlMsg
} from '../../components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';
import {
    managementModeOptions,
    compareModeOptions,
    kpiPeriodTypeOptions,
    dataTypeOptions,
    formulaSelectionModeOptions,
    withAllOption,
    optionName,
    yesNoName
} from '@/types/MindScoreOptions';

definePageMeta({ middleware: ['auth'] });

const router = useRouter();
const queryPageStore = useMdProg003d0001Store();
const { showLoading, hideLoading, confirmFire } = useSwalLoading();

const pageProgramId = ref(PageConstants.QueryId);
const dsList = ref<any[]>([]);
const qFieldShow = ref(true);
const formulaMap = ref<Record<string, string>>({});
const aggrMap = ref<Record<string, string>>({});

const dataTypeQueryOptions = withAllOption(dataTypeOptions);
const periodTypeQueryOptions = withAllOption(kpiPeriodTypeOptions);
const managementModeQueryOptions = withAllOption(managementModeOptions);
const compareModeQueryOptions = withAllOption(compareModeOptions);
const formulaSelectionModeQueryOptions = withAllOption(formulaSelectionModeOptions);

const formulaName = (oid: string) => formulaMap.value[oid] || oid;
const aggrName = (oid: string) => aggrMap.value[oid] || oid;

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
                'method'  : (val: any) => confirmFire('確定刪除?', delItem, val),
                'icon'    : 'trash',
                'type'    : 'delete',
                'memo'    : 'Delete current item.',
                'class'   : 'btn btn-danger btn-sm'
            }
        ],
        [
            { label: '<i class="bi bi-hand-index-thumb"></i>', field: 'oid', labHtml: true },
            { label: 'KPI代碼', field: 'kpiCode' },
            { label: 'KPI名稱', field: 'kpiName' },
            { label: '資料型態', field: 'dataTypeName' },
            { label: '週期', field: 'periodTypeName' },
            { label: '管理模式', field: 'managementModeName' },
            { label: '比較模式', field: 'compareModeName' },
            { label: '公式', field: 'formulaName' },
            { label: '彙總方法', field: 'aggrMethodName' },
            { label: '公式選擇', field: 'formulaSelectionModeName' },
            { label: '啟用', field: 'enabledName' }
        ]
    );
};

const loadFormulaList = async () => {
    const axiosInstance = getAxiosInstance();
    const response = await axiosInstance.post(import.meta.env.VITE_API_URL + '/MD_PROG002D0001/findList', {});
    if (response.data && import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
        const map: Record<string, string> = {};
        (response.data.value || []).forEach((item: any) => {
            map[item.oid] = item.formulaCode + ' - ' + item.formulaName;
        });
        formulaMap.value = map;
    }
};

const loadAggrList = async () => {
    const axiosInstance = getAxiosInstance();
    const response = await axiosInstance.post(import.meta.env.VITE_API_URL + '/MD_PROG002D0002/findList', {});
    if (response.data && import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
        const map: Record<string, string> = {};
        (response.data.value || []).forEach((item: any) => {
            map[item.oid] = item.aggrCode + ' - ' + item.aggrName;
        });
        aggrMap.value = map;
    }
};

const loadOptionMaps = async () => {
    try {
        await Promise.all([loadFormulaList(), loadAggrList()]);
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
                kpiCodeLike          : queryPageStore.queryParam.kpiCode,
                kpiNameLike          : queryPageStore.queryParam.kpiName,
                dataType             : queryPageStore.queryParam.dataType,
                periodType           : queryPageStore.queryParam.periodType,
                managementMode       : queryPageStore.queryParam.managementMode,
                compareMode          : queryPageStore.queryParam.compareMode,
                formulaSelectionMode : queryPageStore.queryParam.formulaSelectionMode,
                enabled              : queryPageStore.queryParam.enabled
            },
            pageOf: {
                select  : queryPageStore.gridConfig.page,
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
            dsList.value = response.data.value.map((item: any) => ({
                ...item,
                dataTypeName : optionName(dataTypeOptions, item.dataType),
                periodTypeName : optionName(kpiPeriodTypeOptions, item.periodType),
                managementModeName : optionName(managementModeOptions, item.managementMode),
                compareModeName : optionName(compareModeOptions, item.compareMode),
                formulaSelectionModeName : optionName(formulaSelectionModeOptions, item.formulaSelectionMode),
                formulaName : formulaName(item.formulaOid),
                aggrMethodName : aggrName(item.aggrMethodOid),
                enabledName : yesNoName(item.enabled)
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
    await loadOptionMaps();
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
        description="KPI基本資料查詢"
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
          <input type="text" class="form-control" id="kpiCode" placeholder="KPI代碼" v-model="queryPageStore.queryParam.kpiCode">
          <label for="kpiCode">KPI代碼</label>
        </div>
      </div>
      <div class="col-md-3">
        <div class="form-group form-floating">
          <input type="text" class="form-control" id="kpiName" placeholder="KPI名稱" v-model="queryPageStore.queryParam.kpiName">
          <label for="kpiName">KPI名稱</label>
        </div>
      </div>
      <div class="col-md-3">
        <div class="form-group form-floating">
          <select class="form-select" id="dataType" v-model="queryPageStore.queryParam.dataType">
            <option v-for="item in dataTypeQueryOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
          </select>
          <label for="dataType">資料型態</label>
        </div>
      </div>
      <div class="col-md-3">
        <div class="form-group form-floating">
          <select class="form-select" id="periodType" v-model="queryPageStore.queryParam.periodType">
            <option v-for="item in periodTypeQueryOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
          </select>
          <label for="periodType">週期</label>
        </div>
      </div>
      <div class="col-md-3">
        <div class="form-group form-floating">
          <select class="form-select" id="managementMode" v-model="queryPageStore.queryParam.managementMode">
            <option v-for="item in managementModeQueryOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
          </select>
          <label for="managementMode">管理模式</label>
        </div>
      </div>
      <div class="col-md-3">
        <div class="form-group form-floating">
          <select class="form-select" id="compareMode" v-model="queryPageStore.queryParam.compareMode">
            <option v-for="item in compareModeQueryOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
          </select>
          <label for="compareMode">比較模式</label>
        </div>
      </div>
      <div class="col-md-3">
        <div class="form-group form-floating">
          <select class="form-select" id="formulaSelectionMode" v-model="queryPageStore.queryParam.formulaSelectionMode">
            <option v-for="item in formulaSelectionModeQueryOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
          </select>
          <label for="formulaSelectionMode">公式選擇</label>
        </div>
      </div>
      <div class="col-md-3">
        <div class="form-group form-floating">
          <select class="form-select" id="enabled" v-model="queryPageStore.queryParam.enabled">
            <option value="">全部</option>
            <option value="Y">是</option>
            <option value="N">否</option>
          </select>
          <label for="enabled">啟用</label>
        </div>
      </div>
    </div>
    <div class="row mt-3">
      <div class="col-12 d-flex gap-2">
        <button type="button" class="btn btn-primary" @click="btnQuery"><i class="bi bi-search"></i> 查詢</button>
        <button type="button" class="btn btn-outline-secondary" @click="btnClear"><i class="bi bi-eraser"></i> 清除</button>
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
