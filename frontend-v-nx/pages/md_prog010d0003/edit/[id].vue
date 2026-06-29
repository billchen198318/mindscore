<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { toast } from 'vue3-toastify';
import Toolbar from '@/components/Toolbar.vue';
import RuleForm from '../RuleForm.vue';
import { getAxiosInstance, escapeQifuHtmlMsg } from '@/components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';
import { PageConstants } from '../config';

definePageMeta({ middleware: ['auth'] });
const route = useRoute();
const router = useRouter();
const { showLoading, hideLoading } = useSwalLoading();
const checkFields = ref<Record<string, any>>({});
const form = ref<any>({ oid: route.params.id, ruleCode: '', ruleName: '', ruleType: 'SIGNAL', sourceType: 'KPI', severity: 'MEDIUM', enabledFlag: 'Y', priorityNo: 0,
    conditionExpr: '', actionExpr: '', description: '' });

const load = async () => {
    showLoading();
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/load', { oid: route.params.id });
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) throw new Error(response.data?.message || 'Rule not found');
        form.value = { ...response.data.value, oid: route.params.id };
    } catch (e: any) {
        toast.warning(escapeQifuHtmlMsg(e?.message || String(e)));
    } finally {
        hideLoading();
    }
};

const update = async () => {
    checkFields.value = {};
    showLoading();
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/update', form.value);
        checkFields.value = response.data?.checkFields || {};
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) {
            toast.warning(escapeQifuHtmlMsg(response.data?.message || 'Unable to update rule'));
            return;
        }
        toast.success('Rule updated');
        router.push(PageConstants.frontendNamespace);
    } catch (e: any) {
        toast.warning(escapeQifuHtmlMsg(e?.message || String(e)));
    } finally {
        hideLoading();
    }
};

onMounted(load);
</script>

<template>
  <Toolbar :progId="PageConstants.EditId" description="Edit Interpretation Rule" refreshFlag="Y" @refreshMethod="load"
           backFlag="Y" @backMethod="router.back()" saveFlag="Y" @saveMethod="update" />
  <div class="card"><div class="card-body"><RuleForm v-model="form" :checkFields="checkFields" editMode />
    <div class="mt-4"><button class="btn btn-primary" @click="update"><i class="bi bi-save"></i> Save</button></div>
  </div></div>
</template>