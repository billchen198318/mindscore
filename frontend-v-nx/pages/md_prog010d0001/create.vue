<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { toast } from 'vue3-toastify';
import Toolbar from '@/components/Toolbar.vue';
import ProviderForm from './ProviderForm.vue';
import { getAxiosInstance, escapeQifuHtmlMsg } from '@/components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';
import { PageConstants, defaultBaseUrl } from './config';

definePageMeta({ middleware: ['auth'] });
const router = useRouter();
const { showLoading, hideLoading } = useSwalLoading();
const checkFields = ref<Record<string, any>>({});
const form = ref({ providerCode: '', providerName: '', providerType: 'OPENAI',
    apiBaseUrl: defaultBaseUrl('OPENAI'), defaultModel: '', apiKey: '',
    enabledFlag: 'Y', defaultFlag: 'N', configJson: '' });

const save = async () => {
    checkFields.value = {};
    showLoading();
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/save', form.value);
        checkFields.value = response.data?.checkFields || {};
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) {
            toast.warning(escapeQifuHtmlMsg(response.data?.message || 'Unable to save provider'));
            return;
        }
        toast.success('Provider created');
        router.push(PageConstants.frontendNamespace);
    } catch (e: any) { toast.error(e?.message || String(e)); }
    finally { hideLoading(); }
};
</script>

<template>
  <Toolbar :progId="PageConstants.CreateId" description="Create LLM Provider" backFlag="Y"
           @backMethod="router.back()" saveFlag="Y" @saveMethod="save" />
  <div class="card"><div class="card-body"><ProviderForm v-model="form" :checkFields="checkFields" />
    <div class="mt-4"><button class="btn btn-primary" @click="save"><i class="bi bi-save"></i> Save</button></div>
  </div></div>
</template>
