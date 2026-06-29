<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { toast } from 'vue3-toastify';
import Toolbar from '@/components/Toolbar.vue';
import RuleForm from './RuleForm.vue';
import { getAxiosInstance, escapeQifuHtmlMsg } from '@/components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';
import { PageConstants } from './config';

definePageMeta({ middleware: ['auth'] });
const router = useRouter();
const { showLoading, hideLoading } = useSwalLoading();
const checkFields = ref<Record<string, any>>({});
const form = ref({ ruleCode: '', ruleName: '', ruleType: 'SIGNAL', sourceType: 'KPI', severity: 'MEDIUM', enabledFlag: 'Y', priorityNo: 0,
    conditionExpr: '{\n  "signalType": "SCORE_STATUS",\n  "statusCode": "BAD"\n}',
    actionExpr: '{\n  "insightType": "PERFORMANCE_RISK",\n  "titleTemplate": "{sourceName} needs attention"\n}',
    description: '' });

const save = async () => {
    checkFields.value = {};
    showLoading();
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/save', form.value);
        checkFields.value = response.data?.checkFields || {};
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) {
            toast.warning(escapeQifuHtmlMsg(response.data?.message || 'Unable to save rule'));
            return;
        }
        toast.success('Rule created');
        router.push(PageConstants.frontendNamespace);
    } catch (e: any) {
        toast.warning(escapeQifuHtmlMsg(e?.message || String(e)));
    } finally {
        hideLoading();
    }
};
</script>

<template>
  <Toolbar :progId="PageConstants.CreateId" description="Create Interpretation Rule" backFlag="Y"
           @backMethod="router.back()" saveFlag="Y" @saveMethod="save" />
  <div class="card"><div class="card-body"><RuleForm v-model="form" :checkFields="checkFields" />
    <div class="mt-4"><button class="btn btn-primary" @click="save"><i class="bi bi-save"></i> Save</button></div>
  </div></div>
</template>