<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { toast } from 'vue3-toastify';
import Toolbar from '@/components/Toolbar.vue';
import { getAxiosInstance, escapeQifuHtmlMsg } from '@/components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';
import { PageConstants, providerTypeOptions } from './config';

definePageMeta({ middleware: ['auth'] });
const router = useRouter();
const { showLoading, hideLoading, confirmFire } = useSwalLoading();
const activeTab = ref<'providers' | 'logs'>('providers');
const providers = ref<any[]>([]);
const logs = ref<any[]>([]);
const providerQuery = ref({ providerCode: '', providerName: '', providerType: '', enabledFlag: '' });
const logQuery = ref({ providerOid: '', providerType: '', requestType: '', status: '' });
const pageTabs = [
    { value: 'providers', label: 'Providers' },
    { value: 'logs', label: 'Run Logs' }
];

const postPage = async (endpoint: string, field: any) => {
    const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + endpoint, {
        field, pageOf: { select: 1, showRow: 100 }
    });
    if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) {
        throw new Error(response.data?.message || 'Query failed');
    }
    return response.data.value || [];
};

const queryProviders = async () => {
    showLoading();
    providers.value = [];
    try {
        providers.value = await postPage('/findProviderPage', {
            providerCodeLike: providerQuery.value.providerCode,
            providerNameLike: providerQuery.value.providerName,
            providerType: providerQuery.value.providerType,
            enabledFlag: providerQuery.value.enabledFlag
        });
    } catch (e: any) { toast.warning(escapeQifuHtmlMsg(e?.message || String(e))); }
    finally { hideLoading(); }
};

const queryLogs = async () => {
    showLoading();
    try { logs.value = await postPage('/findRunLogPage', logQuery.value); }
    catch (e: any) { toast.warning(escapeQifuHtmlMsg(e?.message || String(e))); }
    finally { hideLoading(); }
};

const testConnection = async (oid: string) => {
    showLoading();
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/testConnection', { oid });
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) throw new Error(response.data?.message || 'Connection failed');
        response.data.value.connected ? toast.success(response.data.value.message) : toast.warning(response.data.value.message);
        await queryProviders();
    } catch (e: any) { toast.warning(escapeQifuHtmlMsg(e?.message || String(e))); }
    finally { hideLoading(); }
};

const deleteProvider = async (oid: string) => {
    showLoading();
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/delete', { oid });
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) throw new Error(response.data?.message || 'Delete failed');
        if (response.data?.value !== true) throw new Error(response.data?.message || 'Delete failed');
        providers.value = providers.value.filter((item) => item.oid !== oid);
        if (logQuery.value.providerOid === oid) {
            logQuery.value.providerOid = '';
        }
        toast.success('Provider deleted');
        await queryProviders();
    } catch (e: any) { toast.warning(escapeQifuHtmlMsg(e?.message || String(e))); }
    finally { hideLoading(); }
};

const formatDate = (value: any) => value ? new Date(value).toLocaleString() : '-';
const clearProviders = () => { providerQuery.value = { providerCode: '', providerName: '', providerType: '', enabledFlag: '' }; providers.value = []; };
const clearLogs = () => { logQuery.value = { providerOid: '', providerType: '', requestType: '', status: '' }; logs.value = []; };
const selectTab = (tab: 'providers' | 'logs') => {
    activeTab.value = tab;
    if (tab === 'logs' && logs.value.length === 0) {
        queryLogs();
    }
};
onMounted(queryProviders);
</script>

<template>
  <Toolbar :progId="PageConstants.QueryId" description="LLM Provider Config / Run Log"
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
      <div class="col-md-3"><input class="form-control" placeholder="Provider code" v-model="providerQuery.providerCode"></div>
      <div class="col-md-3"><input class="form-control" placeholder="Provider name" v-model="providerQuery.providerName"></div>
      <div class="col-md-2"><select class="form-select" v-model="providerQuery.providerType"><option value="">All types</option><option v-for="item in providerTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option></select></div>
      <div class="col-md-2"><select class="form-select" v-model="providerQuery.enabledFlag"><option value="">All statuses</option><option value="Y">Enabled</option><option value="N">Disabled</option></select></div>
      <div class="col-md-2 d-flex gap-2"><button class="btn btn-primary" @click="queryProviders"><i class="bi bi-search"></i></button><button class="btn btn-outline-secondary" @click="clearProviders"><i class="bi bi-eraser"></i></button></div>
    </div></div></div>
    <div class="table-responsive"><table class="table table-striped table-hover align-middle">
      <thead><tr><th>Actions</th><th>Code</th><th>Name</th><th>Type</th><th>Model</th><th>API Key</th><th>Enabled</th><th>Connection</th><th>Last Test</th></tr></thead>
      <tbody><tr v-for="item in providers" :key="item.oid">
        <td class="text-nowrap">
          <button class="btn btn-sm btn-info me-1" @click="router.push(PageConstants.frontendNamespace + '/edit/' + item.oid)"><i class="bi bi-pen"></i></button>
          <button class="btn btn-sm btn-outline-secondary me-1" @click="testConnection(item.oid)"><i class="bi bi-plug"></i></button>
          <button class="btn btn-sm btn-danger" @click="confirmFire('Delete this provider?', deleteProvider, item.oid)"><i class="bi bi-trash"></i></button>
        </td>
        <td>{{ item.providerCode }}</td><td>{{ item.providerName }}</td><td>{{ item.providerType }}</td>
        <td>{{ item.defaultModel }}</td><td><code>{{ item.apiKeyMasked }}</code></td>
        <td>{{ item.enabledFlag }}</td><td><span class="badge" :class="item.connectStatus === 'SUCCESS' ? 'text-bg-success' : item.connectStatus === 'FAILED' ? 'text-bg-danger' : 'text-bg-secondary'">{{ item.connectStatus || 'NOT_TESTED' }}</span></td>
        <td>{{ formatDate(item.lastTestAt) }}</td>
      </tr><tr v-if="providers.length === 0"><td colspan="9" class="text-center text-muted">No providers</td></tr></tbody>
    </table></div>
  </div>

  <div v-show="activeTab === 'logs'">
    <div class="card mb-3"><div class="card-body"><div class="row g-3">
      <div class="col-md-3"><select class="form-select" v-model="logQuery.providerOid"><option value="">All providers</option><option v-for="item in providers" :key="item.oid" :value="item.oid">{{ item.providerCode }}</option></select></div>
      <div class="col-md-2"><select class="form-select" v-model="logQuery.providerType"><option value="">All types</option><option value="OPENAI">OpenAI</option><option value="GEMINI">Gemini</option></select></div>
      <div class="col-md-2"><select class="form-select" v-model="logQuery.requestType"><option value="">All requests</option><option value="TEST">Test</option><option value="INSIGHT">Insight</option><option value="RECOMMENDATION">Recommendation</option></select></div>
      <div class="col-md-2"><select class="form-select" v-model="logQuery.status"><option value="">All statuses</option><option value="SUCCESS">Success</option><option value="FAILED">Failed</option></select></div>
      <div class="col-md-3 d-flex gap-2"><button class="btn btn-primary" @click="queryLogs"><i class="bi bi-search"></i> Query</button><button class="btn btn-outline-secondary" @click="clearLogs"><i class="bi bi-eraser"></i></button></div>
    </div></div></div>
    <div class="table-responsive"><table class="table table-striped table-hover align-middle">
      <thead><tr><th>Started</th><th>Provider</th><th>Model</th><th>Request</th><th>Status</th><th>Duration</th><th>Tokens</th><th>Error</th></tr></thead>
      <tbody><tr v-for="item in logs" :key="item.oid">
        <td>{{ formatDate(item.startedAt) }}</td><td>{{ item.providerType }}</td><td>{{ item.modelName }}</td><td>{{ item.requestType }}</td>
        <td><span class="badge" :class="item.status === 'SUCCESS' ? 'text-bg-success' : 'text-bg-danger'">{{ item.status }}</span></td>
        <td>{{ item.durationMs ?? '-' }} ms</td><td>{{ item.totalTokens ?? '-' }}</td><td class="text-danger">{{ item.errorMessage }}</td>
      </tr><tr v-if="logs.length === 0"><td colspan="8" class="text-center text-muted">No run logs</td></tr></tbody>
    </table></div>
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
