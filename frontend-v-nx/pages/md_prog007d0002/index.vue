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
import { useMdProg007d0002Store } from './QueryPageStore';
import {
    getAxiosInstance,
    getProgItem,
    getUrlPrefixFromProgItem
} from '../../components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';

definePageMeta({ middleware: ['auth'] });

const router = useRouter();
const queryPageStore = useMdProg007d0002Store();
const { showLoading, hideLoading } = useSwalLoading();

const pageProgramId = ref(PageConstants.QueryId);
const dsList = ref<any[]>([]);
const workspaceList = ref<any[]>([]);
const qFieldShow = ref(true);

const workspaceName = (workspaceOid: string) => {
    const workspace = workspaceList.value.find((item: any) => item.oid === workspaceOid);
    return workspace ? `${workspace.workspaceCode} - ${workspace.workspaceName}` : workspaceOid;
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
            }
        ],
        [
            { label: '<i class="bi bi-hand-index-thumb"></i>', field: 'oid', labHtml: true },
            { label: 'Workspace', field: 'workspaceName' },
            { label: 'Theme Code', field: 'themeCode' },
            { label: 'Theme Name', field: 'themeName' },
            { label: 'Weight', field: 'weightValue' },
            { label: 'Sort No', field: 'sortNo' },
            { label: 'Description', field: 'description' }
        ]
    );
};

const loadWorkspaceList = async () => {
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findWorkspaceList', {});
        if (response.data && import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
            workspaceList.value = response.data.value || [];
        }
    } catch (e: any) {
        alert(e);
    }
};

const btnQuery = async () => {
    showLoading();
    dsList.value = [];
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findPage', {
            "field": {
                "workspaceOid"   : queryPageStore.queryParam.workspaceOid,
                "themeCodeLike"  : queryPageStore.queryParam.themeCode,
                "themeNameLike"  : queryPageStore.queryParam.themeName
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
                workspaceName: workspaceName(item.workspaceOid),
                description: item.description || ''
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
    await loadWorkspaceList();

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
        description="Strategy Theme Query"
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
          <input type="text" class="form-control" id="themeCode" placeholder="Theme Code" v-model="queryPageStore.queryParam.themeCode">
          <label for="themeCode">Theme Code</label>
        </div>
      </div>
      <div class="col-md-4">
        <div class="form-group form-floating">
          <input type="text" class="form-control" id="themeName" placeholder="Theme Name" v-model="queryPageStore.queryParam.themeName">
          <label for="themeName">Theme Name</label>
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
