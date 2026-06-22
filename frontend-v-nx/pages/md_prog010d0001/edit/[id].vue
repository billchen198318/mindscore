<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { toast } from 'vue3-toastify';
import Toolbar from '@/components/Toolbar.vue';
import ProviderForm from '../ProviderForm.vue';
import { getAxiosInstance, escapeQifuHtmlMsg } from '@/components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';
import { PageConstants } from '../config';

definePageMeta({ middleware: ['auth'] });
const route = useRoute();
const router = useRouter();
const { showLoading, hideLoading } = useSwalLoading();
const checkFields = ref<Record<string, any>>({});
const form = ref<any>({ oid: route.params.id, providerCode: '', providerName: '', providerType: 'OPENAI',
    apiBaseUrl: '', defaultModel: '', apiKey: '', apiKeyMasked: '', enabledFlag: 'Y', defaultFlag: 'N', configJson: '' });

const load = async () => {
    showLoading();
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/load', { oid: route.params.id });
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) throw new Error(response.data?.message || 'Provider not found');
        form.value = { ...response.data.value, apiKey: '' };
    } catch (e: any) { toast.error(escapeQifuHtmlMsg(e?.message || String(e))); router.push(PageConstants.frontendNamespace); }
    finally { hideLoading(); }
};

const update = async () => {
    checkFields.value = {};
    showLoading();
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/update', form.value);
        checkFields.value = response.data?.checkFields || {};
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) {
            toast.warning(escapeQifuHtmlMsg(response.data?.message || 'Unable to update provider')); return;
        }
        form.value = { ...response.data.value, apiKey: '' };
        toast.success('Provider updated');
    } catch (e: any) { toast.error(e?.message || String(e)); }
    finally { hideLoading(); }
};

const testConnection = async () => {
    showLoading();
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/testConnection', { oid: form.value.oid });
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) {
            toast.warning(escapeQifuHtmlMsg(response.data?.message || 'Connection failed')); return;
        }
        response.data.value.connected ? toast.success(response.data.value.message) : toast.warning(response.data.value.message);
        await load();
    } catch (e: any) { toast.error(e?.message || String(e)); }
    finally { hideLoading(); }
};

onMounted(load);
</script>

<template>
  <Toolbar :progId="PageConstants.EditId" description="Edit LLM Provider" refreshFlag="Y" @refreshMethod="load"
           backFlag="Y" @backMethod="router.back()" saveFlag="Y" @saveMethod="update" />
  <div class="card"><div class="card-body">
    <ProviderForm v-model="form" :checkFields="checkFields" editMode />
    <div v-if="form.connectStatus" class="alert mt-3" :class="form.connectStatus === 'SUCCESS' ? 'alert-success' : 'alert-danger'">
      Last connection: {{ form.connectStatus }}<span v-if="form.lastErrorMessage"> - {{ form.lastErrorMessage }}</span>
    </div>
    <div class="mt-4 d-flex gap-2">
      <button class="btn btn-primary" @click="update"><i class="bi bi-save"></i> Save</button>
      <button class="btn btn-outline-secondary" @click="testConnection"><i class="bi bi-plug"></i> Test Connection</button>
    </div>
  </div></div>
</template>
