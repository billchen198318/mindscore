<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { toast } from 'vue3-toastify';
import Toolbar from '@/components/Toolbar.vue';
import Grid from '@/components/Grid.vue';
import GridPagination from '@/components/GridPagination.vue';
import { getGridConfig, resetConfigByOld, setConfigPage, setConfigRow, setConfigTotal } from '@/components/GridHelper';
import { getAxiosInstance, escapeQifuHtmlMsg } from '@/components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';
import { useMdProg010d0001Store } from './QueryPageStore';
import { PageConstants, providerTypeOptions } from './config';

definePageMeta({ middleware: ['auth'] });
const router = useRouter();
const queryPageStore = useMdProg010d0001Store();
const { showLoading, hideLoading, confirmFire } = useSwalLoading();
const activeTab = ref<'providers' | 'logs'>('providers');
const dsList = ref<any[]>([]);
const logList = ref<any[]>([]);
const providerOptions = ref<any[]>([]);
const logQuery = ref({ providerOid: '', providerType: '', requestType: '', status: '' });
const pageProgramId = ref(PageConstants.QueryId);
const pageTabs = [
    { value: 'providers', label: 'Providers' },
    { value: 'logs', label: 'Run Logs' }
];

const formatDate = (value: any) => value ? new Date(value).toLocaleString() : '-';
const connectStatusHtml = (value: string) => {
    const status = value || 'NOT_TESTED';
    const badge = status === 'SUCCESS' ? 'text-bg-success' : status === 'FAILED' ? 'text-bg-danger' : 'text-bg-secondary';
    return `<span class="badge ${badge}">${status}</span>`;
};
const runStatusHtml = (value: string) => {
    const badge = value === 'SUCCESS' ? 'text-bg-success' : 'text-bg-danger';
    return `<span class="badge ${badge}">${value || '-'}</span>`;
};
const durationText = (value: any) => value == null ? '-' : `${value} ms`;
const valueOrDash = (value: any) => value == null || value === '' ? '-' : value;

const postPage = async (endpoint: string, field: any, showRow = 100) => {
    const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + endpoint, {
        field, pageOf: { select: 1, showRow }
    });
    if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) {
        throw new Error(response.data?.message || 'Query failed');
    }
    return response.data.value || [];
};

const loadProviderOptions = async () => {
    try {
        providerOptions.value = await postPage('/findProviderPage', {}, 1000);
    } catch (e: any) {
        toast.warning(escapeQifuHtmlMsg(e?.message || String(e)));
    }
};

const queryProviders = async () => {
    showLoading();
    dsList.value = [];
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findProviderPage', {
            field: {
                providerCodeLike: queryPageStore.queryParam.providerCode,
                providerNameLike: queryPageStore.queryParam.providerName,
                providerType: queryPageStore.queryParam.providerType,
                enabledFlag: queryPageStore.queryParam.enabledFlag
            },
            pageOf: {
                select: queryPageStore.gridConfig.page,
                showRow: queryPageStore.gridConfig.row
            }
        });
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) {
            clearGridConfig();
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

const queryLogs = async () => {
    showLoading();
    logList.value = [];
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findRunLogPage', {
            field: logQuery.value,
            pageOf: {
                select: queryPageStore.logGridConfig.page,
                showRow: queryPageStore.logGridConfig.row
            }
        });
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) {
            clearLogGridConfig();
            throw new Error(response.data?.message || 'Query failed');
        }
        logList.value = response.data.value || [];
        setConfigTotal(queryPageStore.logGridConfig, response.data.pageOf?.countSize || logList.value.length);
    } catch (e: any) {
        toast.warning(escapeQifuHtmlMsg(e?.message || String(e)));
    } finally {
        hideLoading();
    }
};

const testConnection = async (oid: string) => {
    showLoading();
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/testConnection', { oid });
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) throw new Error(response.data?.message || 'Connection failed');
        response.data.value.connected ? toast.success(response.data.value.message) : toast.warning(response.data.value.message);
        await queryProviders();
        if (activeTab.value === 'logs') {
            await queryLogs();
        }
    } catch (e: any) { toast.warning(escapeQifuHtmlMsg(e?.message || String(e))); }
    finally { hideLoading(); }
};

const deleteProvider = async (oid: string) => {
    showLoading();
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/delete', { oid });
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) throw new Error(response.data?.message || 'Delete failed');
        if (response.data?.value !== true) throw new Error(response.data?.message || 'Delete failed');
        dsList.value = dsList.value.filter((item) => item.oid !== oid);
        providerOptions.value = providerOptions.value.filter((item) => item.oid !== oid);
        if (logQuery.value.providerOid === oid) {
            logQuery.value.providerOid = '';
        }
        toast.success('Provider deleted');
        await queryProviders();
    } catch (e: any) { toast.warning(escapeQifuHtmlMsg(e?.message || String(e))); }
    finally { hideLoading(); }
};

const changeQueryGridRow = (row: number) => {
    setConfigRow(queryPageStore.gridConfig, row);
    queryPageStore.gridConfig.page = 1;
    queryProviders();
};

const changePageSelect = (page: number) => {
    setConfigPage(queryPageStore.gridConfig, page);
    queryProviders();
};

const changeLogGridRow = (row: number) => {
    setConfigRow(queryPageStore.logGridConfig, row);
    queryPageStore.logGridConfig.page = 1;
    queryLogs();
};

const changeLogPageSelect = (page: number) => {
    setConfigPage(queryPageStore.logGridConfig, page);
    queryLogs();
};

const clearGridConfig = () => {
    setConfigRow(queryPageStore.gridConfig, import.meta.env.VITE_DEFAULT_ROW);
    setConfigPage(queryPageStore.gridConfig, 1);
    setConfigTotal(queryPageStore.gridConfig, 0);
};

const clearLogGridConfig = () => {
    setConfigRow(queryPageStore.logGridConfig, import.meta.env.VITE_DEFAULT_ROW);
    setConfigPage(queryPageStore.logGridConfig, 1);
    setConfigTotal(queryPageStore.logGridConfig, 0);
};

const clearProviders = () => {
    queryPageStore.clearData();
    dsList.value = [];
    clearGridConfig();
};
const clearLogs = () => {
    logQuery.value = { providerOid: '', providerType: '', requestType: '', status: '' };
    logList.value = [];
    clearLogGridConfig();
};
const selectTab = (tab: 'providers' | 'logs') => {
    activeTab.value = tab;
    if (tab === 'logs') {
        if (providerOptions.value.length === 0) {
            loadProviderOptions();
        }
        if (logList.value.length === 0) {
            queryLogs();
        }
    }
};

const initQueryGridConfig = () => getGridConfig(
    'oid',
    [
        {
            method : (val: any) => router.push(PageConstants.frontendNamespace + '/edit/' + val),
            icon : 'pen',
            type : 'edit',
            memo : 'Edit current item.',
            class : 'btn btn-info btn-sm'
        },
        {
            method : (val: any) => testConnection(val),
            icon : 'plug',
            type : 'testConnection',
            memo : 'Test connection.',
            class : 'btn btn-outline-secondary btn-sm'
        },
        {
            method : (val: any) => confirmFire('Delete this provider?', deleteProvider, val),
            icon : 'trash',
            type : 'delete',
            memo : 'Delete current item.',
            class : 'btn btn-danger btn-sm'
        }
    ],
    [
        { label: 'Actions', field: 'oid', labTextAlign: 'center', textAlign: 'center' },
        { label: 'Code', field: 'providerCode' },
        { label: 'Name', field: 'providerName' },
        { label: 'Type', field: 'providerType' },
        { label: 'Model', field: 'defaultModel' },
        { label: 'API Key', field: 'apiKeyMasked' },
        { label: 'Enabled', field: 'enabledFlag', textAlign: 'center' },
        { label: 'Connection', field: 'connectStatus', colMethod: connectStatusHtml, colHtml: true, textAlign: 'center' },
        { label: 'Last Test', field: 'lastTestAt', colMethod: formatDate }
    ]
);

const initLogGridConfig = () => getGridConfig(
    'oid',
    [],
    [
        { label: 'Started', field: 'startedAt', colMethod: formatDate },
        { label: 'Provider', field: 'providerType' },
        { label: 'Model', field: 'modelName' },
        { label: 'Request', field: 'requestType' },
        { label: 'Status', field: 'status', colMethod: runStatusHtml, colHtml: true, textAlign: 'center' },
        { label: 'Duration', field: 'durationMs', colMethod: durationText, textAlign: 'right' },
        { label: 'Tokens', field: 'totalTokens', colMethod: valueOrDash, textAlign: 'right' },
        { label: 'Error', field: 'errorMessage', colMethod: valueOrDash }
    ]
);

onMounted(() => {
    const newGridConfig = initQueryGridConfig();
    if (queryPageStore.gridConfig.column) {
        resetConfigByOld(newGridConfig, queryPageStore.gridConfig);
    }
    queryPageStore.gridConfig = newGridConfig;

    const newLogGridConfig = initLogGridConfig();
    if (queryPageStore.logGridConfig.column) {
        resetConfigByOld(newLogGridConfig, queryPageStore.logGridConfig);
    }
    queryPageStore.logGridConfig = newLogGridConfig;

    queryProviders();
    loadProviderOptions();
});
</script>

<template>
  <Toolbar :progId="pageProgramId" description="LLM Provider Config / Run Log"
           refreshFlag="Y" @refreshMethod="activeTab === 'providers' ? queryProviders() : queryLogs()"
           createFlag="Y" @createMethod="router.push(PageConstants.frontendNamespace + '/create')" />

  <div class="module-tabs mb-3">
    <button
        v-for="item in pageTabs"
        :key="item.value"
        type="button"
        class="module-tab"
        :class="{ active: activeTab === item.value }"
        @click="selectTab(item.value as 'providers' | 'logs')"
    >{{ item.label }}</button>
  </div>

  <div v-show="activeTab === 'providers'">
    <div class="card mb-3"><div class="card-body"><div class="row g-3">
      <div class="col-md-3"><input class="form-control" placeholder="Provider code" v-model="queryPageStore.queryParam.providerCode"></div>
      <div class="col-md-3"><input class="form-control" placeholder="Provider name" v-model="queryPageStore.queryParam.providerName"></div>
      <div class="col-md-2"><select class="form-select" v-model="queryPageStore.queryParam.providerType"><option value="">All types</option><option v-for="item in providerTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option></select></div>
      <div class="col-md-2"><select class="form-select" v-model="queryPageStore.queryParam.enabledFlag"><option value="">All statuses</option><option value="Y">Enabled</option><option value="N">Disabled</option></select></div>
      <div class="col-md-2 d-flex gap-2"><button class="btn btn-primary" @click="queryProviders"><i class="bi bi-search"></i></button><button class="btn btn-outline-secondary" @click="clearProviders"><i class="bi bi-eraser"></i></button></div>
    </div></div></div>
    <GridPagination
        :progId="pageProgramId"
        :gridConfig="queryPageStore.gridConfig"
        :changePageSelectMethod="changePageSelect"
        :changeGridConfigRowMethod="changeQueryGridRow"
    />
    <Grid :progId="pageProgramId" :dataSource="dsList" :config="queryPageStore.gridConfig" />
    <div v-if="dsList.length === 0" class="text-center text-muted py-3">No providers</div>
  </div>

  <div v-show="activeTab === 'logs'">
    <div class="card mb-3"><div class="card-body"><div class="row g-3">
      <div class="col-md-3"><select class="form-select" v-model="logQuery.providerOid"><option value="">All providers</option><option v-for="item in providerOptions" :key="item.oid" :value="item.oid">{{ item.providerCode }}</option></select></div>
      <div class="col-md-2"><select class="form-select" v-model="logQuery.providerType"><option value="">All types</option><option value="OPENAI">OpenAI</option><option value="GEMINI">Gemini</option></select></div>
      <div class="col-md-2"><select class="form-select" v-model="logQuery.requestType"><option value="">All requests</option><option value="TEST">Test</option><option value="INSIGHT">Insight</option><option value="RECOMMENDATION">Recommendation</option></select></div>
      <div class="col-md-2"><select class="form-select" v-model="logQuery.status"><option value="">All statuses</option><option value="SUCCESS">Success</option><option value="FAILED">Failed</option></select></div>
      <div class="col-md-3 d-flex gap-2"><button class="btn btn-primary" @click="queryLogs"><i class="bi bi-search"></i> Query</button><button class="btn btn-outline-secondary" @click="clearLogs"><i class="bi bi-eraser"></i></button></div>
    </div></div></div>
    <GridPagination
        :progId="pageProgramId"
        :gridConfig="queryPageStore.logGridConfig"
        :changePageSelectMethod="changeLogPageSelect"
        :changeGridConfigRowMethod="changeLogGridRow"
    />
    <Grid :progId="pageProgramId" :dataSource="logList" :config="queryPageStore.logGridConfig" />
    <div v-if="logList.length === 0" class="text-center text-muted py-3">No run logs</div>
  </div>
</template>

<style scoped>
.module-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  align-items: center;
}

.module-tab {
  border: 1px solid var(--bs-border-color);
  border-radius: 0.375rem;
  background: var(--bs-body-bg);
  color: var(--bs-body-color);
  min-height: 2.375rem;
  min-width: 8.5rem;
  padding: 0.45rem 0.75rem;
  font-size: 0.925rem;
  line-height: 1.2;
  text-align: center;
  white-space: nowrap;
}

.module-tab.active {
  border-color: var(--bs-primary);
  background: var(--bs-primary);
  color: #fff;
}

@media (max-width: 576px) {
  .module-tab {
    flex: 1 1 calc(50% - 0.5rem);
    min-width: 0;
  }
}
</style>
