<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { toast } from 'vue3-toastify';
import Toolbar from '@/components/Toolbar.vue';
import Grid from '@/components/Grid.vue';
import GridPagination from '@/components/GridPagination.vue';
import HiddenQueryFieldAlertInfo from '@/components/HiddenQueryFieldAlertInfo.vue';
import { getGridConfig, resetConfigByOld, setConfigPage, setConfigRow, setConfigTotal } from '@/components/GridHelper';
import { getAxiosInstance, escapeQifuHtmlMsg } from '@/components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';
import { useMdProg010d0003Store } from './QueryPageStore';
import { PageConstants, ruleTypeOptions, sourceTypeOptions, severityOptions } from './config';

definePageMeta({ middleware: ['auth'] });
const router = useRouter();
const queryPageStore = useMdProg010d0003Store();
const { showLoading, hideLoading, confirmFire } = useSwalLoading();
const pageProgramId = ref(PageConstants.QueryId);
const qFieldShow = ref(true);
const dsList = ref<any[]>([]);

const badgeHtml = (value: string, dangerValues: string[] = []) => {
    const badge = dangerValues.includes(value) ? 'text-bg-danger' : value === 'HIGH' ? 'text-bg-warning' : value === 'Y' ? 'text-bg-success' : 'text-bg-secondary';
    return `<span class="badge ${badge}">${value || '-'}</span>`;
};
const enabledHtml = (value: string) => badgeHtml(value, ['N']);
const isSearchNoData = (message: any) => String(message || '').toLowerCase().includes('search no data');

const clearGridConfig = () => {
    setConfigRow(queryPageStore.gridConfig, import.meta.env.VITE_DEFAULT_ROW);
    setConfigPage(queryPageStore.gridConfig, 1);
    setConfigTotal(queryPageStore.gridConfig, 0);
};

const queryRules = async () => {
    showLoading();
    dsList.value = [];
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findPage', {
            field: {
                ruleCodeLike: queryPageStore.queryParam.ruleCode,
                ruleNameLike: queryPageStore.queryParam.ruleName,
                ruleType: queryPageStore.queryParam.ruleType,
                sourceType: queryPageStore.queryParam.sourceType,
                severity: queryPageStore.queryParam.severity,
                enabledFlag: queryPageStore.queryParam.enabledFlag
            },
            pageOf: { select: queryPageStore.gridConfig.page, showRow: queryPageStore.gridConfig.row }
        });
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) {
            clearGridConfig();
            if (isSearchNoData(response.data?.message)) return;
            throw new Error(response.data?.message || 'Query failed');
        }
        dsList.value = response.data.value || [];
        setConfigTotal(queryPageStore.gridConfig, response.data.pageOf?.countSize || dsList.value.length);
    } catch (e: any) {
        toast.warning(escapeQifuHtmlMsg(e?.message || String(e)));
    } finally {
        hideLoading();
    }
};

const deleteRule = async (oid: string) => {
    showLoading();
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/delete', { oid });
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG || response.data?.value !== true) throw new Error(response.data?.message || 'Delete failed');
        toast.success('Rule deleted');
        await queryRules();
    } catch (e: any) {
        toast.warning(escapeQifuHtmlMsg(e?.message || String(e)));
    } finally {
        hideLoading();
    }
};
const evaluateRules = async () => {
    showLoading();
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/evaluate', {
            sourceType: queryPageStore.queryParam.sourceType,
            signalStatus: 'OPEN'
        });
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) throw new Error(response.data?.message || 'Evaluation failed');
        const value = response.data?.value || {};
        toast.success(`Evaluation completed. Matched: ${value.matchedCount || 0}, inserted: ${value.insertedCount || 0}, updated: ${value.updatedCount || 0}`);
    } catch (e: any) {
        toast.warning(escapeQifuHtmlMsg(e?.message || String(e)));
    } finally {
        hideLoading();
    }
};

const btnClear = () => {
    queryPageStore.clearData();
    dsList.value = [];
    clearGridConfig();
};
const changeGridRow = (row: number) => { setConfigRow(queryPageStore.gridConfig, row); queryPageStore.gridConfig.page = 1; queryRules(); };
const changePage = (page: number) => { setConfigPage(queryPageStore.gridConfig, page); queryRules(); };

const initGridConfig = () => getGridConfig('oid', [
    { method: (oid: string) => router.push(`${PageConstants.frontendNamespace}/edit/${oid}`), icon: 'pencil-square', type: 'edit', memo: 'Edit current item.', class: 'btn btn-outline-primary btn-sm' },
    { method: (oid: string) => confirmFire('Delete this rule?', deleteRule, oid), icon: 'trash', type: 'delete', memo: 'Delete current item.', class: 'btn btn-outline-danger btn-sm' }
], [
    { label: 'Actions', field: 'oid', textAlign: 'center', labTextAlign: 'center' },
    { label: 'Code', field: 'ruleCode' },
    { label: 'Name', field: 'ruleName' },
    { label: 'Type', field: 'ruleType' },
    { label: 'Source', field: 'sourceType' },
    { label: 'Severity', field: 'severity', colMethod: (value: string) => badgeHtml(value, ['CRITICAL']), colHtml: true, textAlign: 'center' },
    { label: 'Enabled', field: 'enabledFlag', colMethod: enabledHtml, colHtml: true, textAlign: 'center' },
    { label: 'Priority', field: 'priorityNo', textAlign: 'right' }
]);

onMounted(() => {
    const config = initGridConfig();
    if (queryPageStore.gridConfig.column) resetConfigByOld(config, queryPageStore.gridConfig);
    queryPageStore.gridConfig = config;
    if (queryPageStore.gridConfig.total > 0) queryRules();
});
</script>

<template>
  <Toolbar :progId="pageProgramId" description="Interpretation Rule" refreshFlag="Y" @refreshMethod="queryRules"
           queryFieldShowSwitchFlag="Y" @queryFieldShowSwitcMethod="qFieldShow = !qFieldShow"
           createFlag="Y" @createMethod="router.push(PageConstants.frontendNamespace + '/create')" />
  <HiddenQueryFieldAlertInfo :dataSource="dsList" :queryFieldShowFlag="qFieldShow" />
  <div v-show="qFieldShow" class="card mb-3">
    <div class="card-body">
      <div class="row g-3">
        <div class="col-md-2"><input class="form-control" placeholder="Rule code" v-model="queryPageStore.queryParam.ruleCode"></div>
        <div class="col-md-3"><input class="form-control" placeholder="Rule name" v-model="queryPageStore.queryParam.ruleName"></div>
        <div class="col-md-2"><select class="form-select" v-model="queryPageStore.queryParam.ruleType"><option value="">All types</option><option v-for="item in ruleTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option></select></div>
        <div class="col-md-2"><select class="form-select" v-model="queryPageStore.queryParam.sourceType"><option v-for="item in sourceTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option></select></div>
        <div class="col-md-2"><select class="form-select" v-model="queryPageStore.queryParam.severity"><option v-for="item in severityOptions" :key="item.value" :value="item.value">{{ item.label }}</option></select></div>
        <div class="col-md-1"><select class="form-select" v-model="queryPageStore.queryParam.enabledFlag"><option value="">All</option><option value="Y">Y</option><option value="N">N</option></select></div>
        <div class="col-md-4 d-flex gap-2"><button class="btn btn-primary" @click="queryRules"><i class="bi bi-search"></i> Query</button><button class="btn btn-outline-success" @click="evaluateRules"><i class="bi bi-lightning-charge"></i> Evaluate</button><button class="btn btn-outline-secondary" @click="btnClear"><i class="bi bi-eraser"></i></button></div>
      </div>
    </div>
  </div>
  <GridPagination :progId="pageProgramId" :gridConfig="queryPageStore.gridConfig"
                  :changePageSelectMethod="changePage" :changeGridConfigRowMethod="changeGridRow" />
  <Grid :progId="pageProgramId" :dataSource="dsList" :config="queryPageStore.gridConfig" />
  <div v-if="dsList.length === 0" class="text-center text-muted py-3">No interpretation rules</div>
</template>