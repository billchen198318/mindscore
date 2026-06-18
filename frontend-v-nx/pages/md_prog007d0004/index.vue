<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { toast } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

import Toolbar from '@/components/Toolbar.vue';
import Grid from '@/components/Grid.vue';
import GridPagination from '@/components/GridPagination.vue';
import HiddenQueryFieldAlertInfo from '@/components/HiddenQueryFieldAlertInfo.vue';
import { PageConstants } from './config';
import { getGridConfig, setConfigRow, setConfigPage, setConfigTotal, resetConfigByOld } from '../../components/GridHelper';
import { useMdProg007d0004Store } from './QueryPageStore';
import {
    getAxiosInstance,
    getProgItem,
    getUrlPrefixFromProgItem
} from '../../components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';

definePageMeta({ middleware: ['auth'] });

const router = useRouter();
const queryPageStore = useMdProg007d0004Store();
const { showLoading, hideLoading } = useSwalLoading();

const pageProgramId = ref(PageConstants.QueryId);
const dsList = ref<any[]>([]);
const workspaceList = ref<any[]>([]);
const themeList = ref<any[]>([]);
const strategyObjectiveList = ref<any[]>([]);
const kpiList = ref<any[]>([]);
const cycleList = ref<any[]>([]);
const okrObjectiveList = ref<any[]>([]);
const qFieldShow = ref(true);

const linkTypeName = (linkType: string) => {
    const map: any = { KPI: 'KPI', OKR_OBJECTIVE: 'OKR Objective' };
    return map[linkType] || linkType;
};

const strategyObjectiveName = (oid: string) => {
    const item = strategyObjectiveList.value.find((objective: any) => objective.oid === oid);
    return item ? `${item.objectiveCode} - ${item.objectiveName}` : oid;
};

const linkTargetName = (linkType: string, linkOid: string) => {
    if (linkType === 'KPI') {
        const item = kpiList.value.find((kpi: any) => kpi.oid === linkOid);
        return item ? `${item.kpiCode} - ${item.kpiName}` : linkOid;
    }
    if (linkType === 'OKR_OBJECTIVE') {
        const item = okrObjectiveList.value.find((objective: any) => objective.oid === linkOid);
        return item ? `${item.objectiveCode} - ${item.objectiveName}` : linkOid;
    }
    return linkOid;
};

const tbRefresh = () => btnClear();
const tbCreate = () => router.push(PageConstants.frontendNamespace + '/create');
const tbQueryFieldShow = () => qFieldShow.value = !qFieldShow.value;

const btnClear = () => {
    queryPageStore.clearData();
    dsList.value = [];
    clearGridConfig();
    loadThemeList();
    loadStrategyObjectiveList();
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
            }
        ],
        [
            { label: '<i class="bi bi-hand-index-thumb"></i>', field: 'oid', labHtml: true },
            { label: 'Strategy Objective', field: 'strategyObjectiveName' },
            { label: 'Link Type', field: 'linkTypeName' },
            { label: 'Link Target', field: 'linkTargetName' },
            { label: 'Weight', field: 'weightValue' },
            { label: 'Sort No', field: 'sortNo' }
        ]
    );
};

const postList = async (url: string, payload: any = {}) => {
    const axiosInstance = getAxiosInstance();
    const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + url, payload);
    return response.data && import.meta.env.VITE_SUCCESS_FLAG == response.data.success ? response.data.value || [] : [];
};

const loadWorkspaceList = async () => workspaceList.value = await postList('/findWorkspaceList');
const loadKpiList = async () => kpiList.value = await postList('/findKpiList');
const loadCycleList = async () => cycleList.value = await postList('/findCycleList');

const loadThemeList = async () => {
    themeList.value = await postList('/findThemeList', { workspaceOid: queryPageStore.queryParam.workspaceOid });
};

const loadStrategyObjectiveList = async () => {
    strategyObjectiveList.value = await postList('/findStrategyObjectiveList', { themeOid: queryPageStore.queryParam.themeOid });
};

const loadOkrObjectiveList = async (cycleOid = '') => {
    okrObjectiveList.value = await postList('/findOkrObjectiveList', { cycleOid });
};

const btnQuery = async () => {
    showLoading();
    dsList.value = [];
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findPage', {
            "field": {
                "strategyObjectiveOid" : queryPageStore.queryParam.strategyObjectiveOid,
                "linkType"             : queryPageStore.queryParam.linkType,
                "linkOid"              : queryPageStore.queryParam.linkOid
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
                strategyObjectiveName: strategyObjectiveName(item.strategyObjectiveOid),
                linkTypeName: linkTypeName(item.linkType),
                linkTargetName: linkTargetName(item.linkType, item.linkOid)
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
    const newGridConfig = initQueryGridConfig();
    if (queryPageStore.gridConfig.column) {
        resetConfigByOld(newGridConfig, queryPageStore.gridConfig);
    }
    queryPageStore.gridConfig = newGridConfig;
    await Promise.all([loadWorkspaceList(), loadThemeList(), loadStrategyObjectiveList(), loadKpiList(), loadCycleList(), loadOkrObjectiveList()]);

    if (queryPageStore.gridConfig.total > 0) {
        btnQuery();
    }
});

watch(
    () => queryPageStore.queryParam.workspaceOid,
    async () => {
        queryPageStore.queryParam.themeOid = '';
        queryPageStore.queryParam.strategyObjectiveOid = '';
        await loadThemeList();
        await loadStrategyObjectiveList();
    }
);

watch(
    () => queryPageStore.queryParam.themeOid,
    async () => {
        queryPageStore.queryParam.strategyObjectiveOid = '';
        await loadStrategyObjectiveList();
    }
);
</script>

<template>
<div class="row">
  <div class="col-12">
    <Toolbar
        :progId="pageProgramId"
        description="Strategy Objective Link Query"
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
          <select class="form-select" id="workspaceOid" v-model="queryPageStore.queryParam.workspaceOid">
            <option value="">All</option>
            <option v-for="workspace in workspaceList" :key="workspace.oid" :value="workspace.oid">
              {{ workspace.workspaceCode }} - {{ workspace.workspaceName }}
            </option>
          </select>
          <label for="workspaceOid">Workspace</label>
        </div>
      </div>
      <div class="col-md-4">
        <div class="form-group form-floating">
          <select class="form-select" id="themeOid" v-model="queryPageStore.queryParam.themeOid">
            <option value="">All</option>
            <option v-for="theme in themeList" :key="theme.oid" :value="theme.oid">
              {{ theme.themeCode }} - {{ theme.themeName }}
            </option>
          </select>
          <label for="themeOid">Theme</label>
        </div>
      </div>
      <div class="col-md-4">
        <div class="form-group form-floating">
          <select class="form-select" id="strategyObjectiveOid" v-model="queryPageStore.queryParam.strategyObjectiveOid">
            <option value="">All</option>
            <option v-for="objective in strategyObjectiveList" :key="objective.oid" :value="objective.oid">
              {{ objective.objectiveCode }} - {{ objective.objectiveName }}
            </option>
          </select>
          <label for="strategyObjectiveOid">Strategy Objective</label>
        </div>
      </div>
      <div class="col-md-4">
        <div class="form-group form-floating">
          <select class="form-select" id="linkType" v-model="queryPageStore.queryParam.linkType">
            <option value="">All</option>
            <option value="KPI">KPI</option>
            <option value="OKR_OBJECTIVE">OKR Objective</option>
          </select>
          <label for="linkType">Link Type</label>
        </div>
      </div>
      <div class="col-md-8">
        <div class="form-group form-floating">
          <select class="form-select" id="linkOid" v-model="queryPageStore.queryParam.linkOid">
            <option value="">All</option>
            <option v-if="queryPageStore.queryParam.linkType === 'KPI'" v-for="kpi in kpiList" :key="kpi.oid" :value="kpi.oid">
              {{ kpi.kpiCode }} - {{ kpi.kpiName }}
            </option>
            <option v-if="queryPageStore.queryParam.linkType === 'OKR_OBJECTIVE'" v-for="objective in okrObjectiveList" :key="objective.oid" :value="objective.oid">
              {{ objective.objectiveCode }} - {{ objective.objectiveName }}
            </option>
          </select>
          <label for="linkOid">Link Target</label>
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
