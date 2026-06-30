<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { toast } from 'vue3-toastify';
import Toolbar from '@/components/Toolbar.vue';
import Grid from '@/components/Grid.vue';
import GridPagination from '@/components/GridPagination.vue';
import HiddenQueryFieldAlertInfo from '@/components/HiddenQueryFieldAlertInfo.vue';
import { checkInvalid, escapeQifuHtmlMsg, getAxiosInstance, invalidFeedback } from '@/components/BaseHelper';
import { getGridConfig, resetConfigByOld, setConfigPage, setConfigRow, setConfigTotal } from '@/components/GridHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';
import { useMdProg010d0005Store } from './QueryPageStore';
import {
    PageConstants,
    insightStatusOptions,
    insightTypeOptions,
    recommendationStatusOptions,
    recommendationTypeOptions,
    severityOptions,
    sourceTypeOptions
} from './config';

definePageMeta({ middleware: ['auth'] });

const queryPageStore = useMdProg010d0005Store();
const { showLoading, hideLoading, confirmFire } = useSwalLoading();
const pageProgramId = ref(PageConstants.QueryId);
const qFieldShow = ref(true);
const insightList = ref<any[]>([]);
const evidenceList = ref<any[]>([]);
const recommendationList = ref<any[]>([]);
const actionPlanList = ref<any[]>([]);
const selectedInsight = ref<any>(null);
const selectedActionPlanOid = ref('');
const llmPromptHint = ref('');
const recommendationEditMode = ref(false);
const checkFields = ref<Record<string, any>>({});
const recommendationForm = ref<any>({
    oid: '',
    insightOid: '',
    recommendationType: 'NEXT_STEP',
    title: '',
    contentText: '',
    priorityNo: 0,
    status: 'OPEN',
    acceptedFlag: 'N',
    actionCreatedFlag: 'N'
});

const isSearchNoData = (message: any) => String(message || '').toLowerCase().includes('search no data');
const dateText = (value: any) => value ? new Date(value).toLocaleString() : '-';
const badgeHtml = (value: string, style: string) => `<span class="badge ${style}">${value || '-'}</span>`;
const severityHtml = (value: string) => {
    const style = value === 'CRITICAL' || value === 'HIGH' ? 'text-bg-danger' : value === 'MEDIUM' ? 'text-bg-warning' : 'text-bg-success';
    return badgeHtml(value, style);
};
const statusHtml = (value: string) => {
    const style = value === 'OPEN' ? 'text-bg-warning' : value === 'ACCEPTED' ? 'text-bg-primary' : value === 'COMPLETED' || value === 'RESOLVED' ? 'text-bg-success' : 'text-bg-secondary';
    return badgeHtml(value, style);
};
const yesNoHtml = (value: string) => badgeHtml(value, value === 'Y' ? 'text-bg-success' : 'text-bg-secondary');

const clearInsightGridConfig = () => {
    setConfigRow(queryPageStore.insightGridConfig, import.meta.env.VITE_DEFAULT_ROW);
    setConfigPage(queryPageStore.insightGridConfig, 1);
    setConfigTotal(queryPageStore.insightGridConfig, 0);
};
const clearRecommendationGridConfig = () => {
    setConfigRow(queryPageStore.recommendationGridConfig, import.meta.env.VITE_DEFAULT_ROW);
    setConfigPage(queryPageStore.recommendationGridConfig, 1);
    setConfigTotal(queryPageStore.recommendationGridConfig, 0);
};
const resetRecommendationForm = () => {
    checkFields.value = {};
    recommendationEditMode.value = false;
    recommendationForm.value = {
        oid: '',
        insightOid: selectedInsight.value?.oid || '',
        recommendationType: 'NEXT_STEP',
        title: '',
        contentText: '',
        priorityNo: 0,
        status: 'OPEN',
        acceptedFlag: 'N',
        actionCreatedFlag: 'N'
    };
};

const queryInsights = async () => {
    showLoading();
    insightList.value = [];
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findInsightPage', {
            field: {
                insightNoLike: queryPageStore.queryParam.insightNo,
                titleLike: queryPageStore.queryParam.title,
                insightType: queryPageStore.queryParam.insightType,
                severity: queryPageStore.queryParam.severity,
                sourceType: queryPageStore.queryParam.sourceType,
                status: queryPageStore.queryParam.status,
                ownerAccount: queryPageStore.queryParam.ownerAccount,
                generatedByType: queryPageStore.queryParam.generatedByType
            },
            pageOf: { select: queryPageStore.insightGridConfig.page, showRow: queryPageStore.insightGridConfig.row }
        });
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) {
            clearInsightGridConfig();
            if (isSearchNoData(response.data?.message)) return;
            throw new Error(response.data?.message || 'Query failed');
        }
        insightList.value = response.data.value || [];
        setConfigTotal(queryPageStore.insightGridConfig, response.data.pageOf?.countSize || insightList.value.length);
    } catch (e: any) {
        clearInsightGridConfig();
        toast.warning(escapeQifuHtmlMsg(e?.message || String(e)));
    } finally {
        hideLoading();
    }
};

const queryEvidence = async (insightOid: string) => {
    evidenceList.value = [];
    const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findEvidenceList', { insightOid });
    if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) throw new Error(response.data?.message || 'Evidence query failed');
    evidenceList.value = response.data.value || [];
};

const queryRecommendations = async () => {
    if (!selectedInsight.value?.oid) return;
    recommendationList.value = [];
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findRecommendationPage', {
            field: {
                insightOid: selectedInsight.value.oid,
                recommendationType: queryPageStore.recommendationQueryParam.recommendationType,
                titleLike: queryPageStore.recommendationQueryParam.title,
                status: queryPageStore.recommendationQueryParam.status,
                acceptedFlag: queryPageStore.recommendationQueryParam.acceptedFlag,
                actionCreatedFlag: queryPageStore.recommendationQueryParam.actionCreatedFlag
            },
            pageOf: { select: queryPageStore.recommendationGridConfig.page, showRow: queryPageStore.recommendationGridConfig.row }
        });
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) {
            clearRecommendationGridConfig();
            if (isSearchNoData(response.data?.message)) return;
            throw new Error(response.data?.message || 'Recommendation query failed');
        }
        recommendationList.value = response.data.value || [];
        setConfigTotal(queryPageStore.recommendationGridConfig, response.data.pageOf?.countSize || recommendationList.value.length);
    } catch (e: any) {
        clearRecommendationGridConfig();
        toast.warning(escapeQifuHtmlMsg(e?.message || String(e)));
    }
};

const selectInsight = async (oid: string) => {
    showLoading();
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/loadInsight', { oid });
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) throw new Error(response.data?.message || 'Insight not found');
        selectedInsight.value = response.data.value;
        queryPageStore.clearRecommendationQuery();
        resetRecommendationForm();
        await queryEvidence(oid);
        await queryRecommendations();
    } catch (e: any) {
        toast.warning(escapeQifuHtmlMsg(e?.message || String(e)));
    } finally {
        hideLoading();
    }
};

const loadActionPlans = async () => {
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findActionPlanList', {});
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) throw new Error(response.data?.message || 'Action plan query failed');
        actionPlanList.value = response.data.value || [];
    } catch (e: any) {
        toast.warning(escapeQifuHtmlMsg(e?.message || String(e)));
    }
};

const generateLlmRecommendation = async () => {
    if (!selectedInsight.value?.oid) return;
    showLoading();
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/generateLlmRecommendation', {
            insightOid: selectedInsight.value.oid,
            promptHint: llmPromptHint.value
        });
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) throw new Error(response.data?.message || 'LLM generation failed');
        toast.success('LLM recommendation generated');
        await queryRecommendations();
    } catch (e: any) {
        toast.warning(escapeQifuHtmlMsg(e?.message || String(e)));
    } finally {
        hideLoading();
    }
};

const createActionFromRecommendation = async (recommendationOid: string) => {
    if (!selectedActionPlanOid.value) {
        toast.warning('Action plan is required');
        return;
    }
    showLoading();
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/createActionFromRecommendation', {
            recommendationOid,
            planOid: selectedActionPlanOid.value
        });
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) throw new Error(response.data?.message || 'Action creation failed');
        toast.success('Action item created');
        await queryRecommendations();
    } catch (e: any) {
        toast.warning(escapeQifuHtmlMsg(e?.message || String(e)));
    } finally {
        hideLoading();
    }
};
const saveRecommendation = async () => {
    if (!selectedInsight.value?.oid) return;
    checkFields.value = {};
    showLoading();
    try {
        recommendationForm.value.insightOid = selectedInsight.value.oid;
        const endpoint = recommendationEditMode.value ? '/updateRecommendation' : '/saveRecommendation';
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + endpoint, recommendationForm.value);
        checkFields.value = response.data?.checkFields || {};
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) {
            toast.warning(escapeQifuHtmlMsg(response.data?.message || 'Unable to save recommendation'));
            return;
        }
        toast.success(recommendationEditMode.value ? 'Recommendation updated' : 'Recommendation created');
        resetRecommendationForm();
        await queryRecommendations();
    } catch (e: any) {
        toast.warning(escapeQifuHtmlMsg(e?.message || String(e)));
    } finally {
        hideLoading();
    }
};

const editRecommendation = async (oid: string) => {
    showLoading();
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/loadRecommendation', { oid });
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) throw new Error(response.data?.message || 'Recommendation not found');
        recommendationForm.value = { ...response.data.value };
        recommendationEditMode.value = true;
        checkFields.value = {};
    } catch (e: any) {
        toast.warning(escapeQifuHtmlMsg(e?.message || String(e)));
    } finally {
        hideLoading();
    }
};

const deleteRecommendation = async (oid: string) => {
    showLoading();
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/deleteRecommendation', { oid });
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG || response.data?.value !== true) throw new Error(response.data?.message || 'Delete failed');
        toast.success('Recommendation deleted');
        resetRecommendationForm();
        await queryRecommendations();
    } catch (e: any) {
        toast.warning(escapeQifuHtmlMsg(e?.message || String(e)));
    } finally {
        hideLoading();
    }
};

const changeRecommendationStatus = async (oid: string, endpoint: string, message: string) => {
    showLoading();
    try {
        const response = await getAxiosInstance().post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + endpoint, { oid });
        if (response.data?.success !== import.meta.env.VITE_SUCCESS_FLAG) throw new Error(response.data?.message || 'Status update failed');
        toast.success(message);
        await queryRecommendations();
    } catch (e: any) {
        toast.warning(escapeQifuHtmlMsg(e?.message || String(e)));
    } finally {
        hideLoading();
    }
};

const btnClear = () => {
    queryPageStore.clearInsightQuery();
    insightList.value = [];
    evidenceList.value = [];
    recommendationList.value = [];
    selectedInsight.value = null;
    resetRecommendationForm();
    clearInsightGridConfig();
    clearRecommendationGridConfig();
};
const changeInsightGridRow = (row: number) => { setConfigRow(queryPageStore.insightGridConfig, row); queryPageStore.insightGridConfig.page = 1; queryInsights(); };
const changeInsightPage = (page: number) => { setConfigPage(queryPageStore.insightGridConfig, page); queryInsights(); };
const changeRecommendationGridRow = (row: number) => { setConfigRow(queryPageStore.recommendationGridConfig, row); queryPageStore.recommendationGridConfig.page = 1; queryRecommendations(); };
const changeRecommendationPage = (page: number) => { setConfigPage(queryPageStore.recommendationGridConfig, page); queryRecommendations(); };

const initInsightGridConfig = () => getGridConfig('oid', [
    { method: selectInsight, icon: 'eye', type: 'view', memo: 'View evidence and recommendations.', class: 'btn btn-outline-primary btn-sm' }
], [
    { label: 'Actions', field: 'oid', textAlign: 'center', labTextAlign: 'center' },
    { label: 'Insight No', field: 'insightNo' },
    { label: 'Title', field: 'title' },
    { label: 'Type', field: 'insightType' },
    { label: 'Source', field: 'sourceType', textAlign: 'center' },
    { label: 'Severity', field: 'severity', colMethod: severityHtml, colHtml: true, textAlign: 'center' },
    { label: 'Status', field: 'status', colMethod: statusHtml, colHtml: true, textAlign: 'center' },
    { label: 'Generated', field: 'generatedAt', colMethod: dateText }
]);

const initRecommendationGridConfig = () => getGridConfig('oid', [
    { method: editRecommendation, icon: 'pencil-square', type: 'edit', memo: 'Edit recommendation.', class: 'btn btn-outline-primary btn-sm' },
    { method: (oid: string) => changeRecommendationStatus(oid, '/acceptRecommendation', 'Recommendation accepted'), icon: 'check2-circle', type: 'accept', memo: 'Accept recommendation.', class: 'btn btn-outline-success btn-sm' },
    { method: (oid: string) => changeRecommendationStatus(oid, '/dismissRecommendation', 'Recommendation dismissed'), icon: 'x-circle', type: 'dismiss', memo: 'Dismiss recommendation.', class: 'btn btn-outline-secondary btn-sm' },
    { method: (oid: string) => changeRecommendationStatus(oid, '/completeRecommendation', 'Recommendation completed'), icon: 'flag', type: 'complete', memo: 'Complete recommendation.', class: 'btn btn-outline-info btn-sm' },
    { method: (oid: string) => changeRecommendationStatus(oid, '/reopenRecommendation', 'Recommendation reopened'), icon: 'arrow-counterclockwise', type: 'reopen', memo: 'Reopen recommendation.', class: 'btn btn-outline-warning btn-sm' },
    { method: createActionFromRecommendation, icon: 'clipboard-plus', type: 'action', memo: 'Create action item.', class: 'btn btn-outline-dark btn-sm' },
    { method: (oid: string) => confirmFire('Delete this recommendation?', deleteRecommendation, oid), icon: 'trash', type: 'delete', memo: 'Delete recommendation.', class: 'btn btn-outline-danger btn-sm' }
], [
    { label: 'Actions', field: 'oid', textAlign: 'center', labTextAlign: 'center' },
    { label: 'Type', field: 'recommendationType' },
    { label: 'Title', field: 'title' },
    { label: 'Priority', field: 'priorityNo', textAlign: 'right' },
    { label: 'Status', field: 'status', colMethod: statusHtml, colHtml: true, textAlign: 'center' },
    { label: 'Accepted', field: 'acceptedFlag', colMethod: yesNoHtml, colHtml: true, textAlign: 'center' },
    { label: 'Action', field: 'actionCreatedFlag', colMethod: yesNoHtml, colHtml: true, textAlign: 'center' }
]);

onMounted(() => {
    const insightConfig = initInsightGridConfig();
    if (queryPageStore.insightGridConfig.column) resetConfigByOld(insightConfig, queryPageStore.insightGridConfig);
    queryPageStore.insightGridConfig = insightConfig;
    const recommendationConfig = initRecommendationGridConfig();
    if (queryPageStore.recommendationGridConfig.column) resetConfigByOld(recommendationConfig, queryPageStore.recommendationGridConfig);
    queryPageStore.recommendationGridConfig = recommendationConfig;
    loadActionPlans();
    if (queryPageStore.insightGridConfig.total > 0) queryInsights();
});
</script>

<template>
  <Toolbar :progId="pageProgramId" description="Insight Evidence / Recommendation" refreshFlag="Y" @refreshMethod="queryInsights"
           queryFieldShowSwitchFlag="Y" @queryFieldShowSwitcMethod="qFieldShow = !qFieldShow" />
  <HiddenQueryFieldAlertInfo :dataSource="insightList" :queryFieldShowFlag="qFieldShow" />

  <div v-show="qFieldShow" class="card mb-3">
    <div class="card-body">
      <div class="row g-3">
        <div class="col-md-2"><input class="form-control" placeholder="Insight no" v-model="queryPageStore.queryParam.insightNo"></div>
        <div class="col-md-3"><input class="form-control" placeholder="Title" v-model="queryPageStore.queryParam.title"></div>
        <div class="col-md-2"><select class="form-select" v-model="queryPageStore.queryParam.insightType"><option v-for="item in insightTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option></select></div>
        <div class="col-md-2"><select class="form-select" v-model="queryPageStore.queryParam.sourceType"><option v-for="item in sourceTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option></select></div>
        <div class="col-md-2"><select class="form-select" v-model="queryPageStore.queryParam.severity"><option v-for="item in severityOptions" :key="item.value" :value="item.value">{{ item.label }}</option></select></div>
        <div class="col-md-2"><select class="form-select" v-model="queryPageStore.queryParam.status"><option v-for="item in insightStatusOptions" :key="item.value" :value="item.value">{{ item.label }}</option></select></div>
        <div class="col-md-2"><input class="form-control" placeholder="Owner" v-model="queryPageStore.queryParam.ownerAccount"></div>
        <div class="col-md-2"><select class="form-select" v-model="queryPageStore.queryParam.generatedByType"><option value="">All generated</option><option value="RULE">Rule</option><option value="LLM">LLM</option><option value="MANUAL">Manual</option></select></div>
        <div class="col-md-3 d-flex gap-2"><button class="btn btn-primary" @click="queryInsights"><i class="bi bi-search"></i> Query</button><button class="btn btn-outline-secondary" @click="btnClear"><i class="bi bi-eraser"></i></button></div>
      </div>
    </div>
  </div>

  <GridPagination :progId="pageProgramId" :gridConfig="queryPageStore.insightGridConfig"
                  :changePageSelectMethod="changeInsightPage" :changeGridConfigRowMethod="changeInsightGridRow" />
  <Grid :progId="pageProgramId" :dataSource="insightList" :config="queryPageStore.insightGridConfig" />
  <div v-if="insightList.length === 0" class="text-center text-muted py-3">No insights</div>

  <div v-if="selectedInsight" class="mt-3">
    <div class="card mb-3 border-primary-subtle">
      <div class="card-header d-flex justify-content-between align-items-center">
        <span>{{ selectedInsight.insightNo }} - {{ selectedInsight.title }}</span>
        <button class="btn btn-outline-secondary btn-sm" @click="selectedInsight = null"><i class="bi bi-x-lg"></i></button>
      </div>
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-2"><strong>Severity</strong><div v-html="severityHtml(selectedInsight.severity)"></div></div>
          <div class="col-md-2"><strong>Status</strong><div v-html="statusHtml(selectedInsight.status)"></div></div>
          <div class="col-md-2"><strong>Source</strong><div>{{ selectedInsight.sourceType }}</div></div>
          <div class="col-md-3"><strong>Owner</strong><div>{{ selectedInsight.ownerAccount || '-' }}</div></div>
          <div class="col-md-3"><strong>Generated</strong><div>{{ dateText(selectedInsight.generatedAt) }}</div></div>
          <div class="col-12"><strong>Summary</strong><div class="border rounded p-2 bg-light">{{ selectedInsight.summaryText || '-' }}</div></div>
        </div>
      </div>
    </div>

    <div class="card mb-3">
      <div class="card-body">
        <div class="row g-3 align-items-end">
          <div class="col-md-5">
            <label class="form-label" for="llmPromptHint">LLM prompt hint</label>
            <input id="llmPromptHint" class="form-control" placeholder="Optional instruction" v-model="llmPromptHint">
          </div>
          <div class="col-md-3">
            <button class="btn btn-outline-primary" @click="generateLlmRecommendation"><i class="bi bi-stars"></i> Generate Recommendation</button>
          </div>
          <div class="col-md-4">
            <label class="form-label" for="actionPlanOid">Action plan for created actions</label>
            <select id="actionPlanOid" class="form-select" v-model="selectedActionPlanOid">
              <option value="">Select action plan</option>
              <option v-for="plan in actionPlanList" :key="plan.oid" :value="plan.oid">{{ plan.planCode }} - {{ plan.planName }}</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <div class="card mb-3">
      <div class="card-header">Evidence</div>
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-sm table-hover mb-0">
            <thead><tr><th>Type</th><th>Label</th><th>Value</th><th>Source</th><th class="text-end">Sort</th></tr></thead>
            <tbody>
              <tr v-for="item in evidenceList" :key="item.oid">
                <td>{{ item.evidenceType }}</td>
                <td>{{ item.label }}</td>
                <td class="text-break">{{ item.valueText || item.valueNo || '-' }}</td>
                <td>{{ item.sourceType || '-' }}</td>
                <td class="text-end">{{ item.sortNo }}</td>
              </tr>
              <tr v-if="evidenceList.length === 0"><td colspan="5" class="text-center text-muted py-3">No evidence</td></tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <div class="card mb-3">
      <div class="card-header">{{ recommendationEditMode ? 'Edit Recommendation' : 'New Recommendation' }}</div>
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-3">
            <label class="form-label" for="recommendationType">Type</label>
            <select id="recommendationType" :class="['form-select', checkInvalid('recommendationType', checkFields) ? 'is-invalid' : '']" v-model="recommendationForm.recommendationType">
              <option v-for="item in recommendationTypeOptions.filter((item) => item.value)" :key="item.value" :value="item.value">{{ item.label }}</option>
            </select>
            <div v-if="checkInvalid('recommendationType', checkFields)" class="invalid-feedback">{{ invalidFeedback('recommendationType', checkFields) }}</div>
          </div>
          <div class="col-md-7">
            <label class="form-label" for="title">Title</label>
            <input id="title" :class="['form-control', checkInvalid('title', checkFields) ? 'is-invalid' : '']" v-model="recommendationForm.title">
            <div v-if="checkInvalid('title', checkFields)" class="invalid-feedback">{{ invalidFeedback('title', checkFields) }}</div>
          </div>
          <div class="col-md-2">
            <label class="form-label" for="priorityNo">Priority</label>
            <input id="priorityNo" type="number" class="form-control" v-model.number="recommendationForm.priorityNo">
          </div>
          <div class="col-md-3">
            <label class="form-label" for="status">Status</label>
            <select id="status" class="form-select" v-model="recommendationForm.status">
              <option v-for="item in recommendationStatusOptions.filter((item) => item.value)" :key="item.value" :value="item.value">{{ item.label }}</option>
            </select>
          </div>
          <div class="col-md-2">
            <label class="form-label" for="acceptedFlag">Accepted</label>
            <select id="acceptedFlag" class="form-select" v-model="recommendationForm.acceptedFlag"><option value="N">No</option><option value="Y">Yes</option></select>
          </div>
          <div class="col-md-2">
            <label class="form-label" for="actionCreatedFlag">Action</label>
            <select id="actionCreatedFlag" class="form-select" v-model="recommendationForm.actionCreatedFlag"><option value="N">No</option><option value="Y">Yes</option></select>
          </div>
          <div class="col-12">
            <label class="form-label" for="contentText">Content</label>
            <textarea id="contentText" rows="4" class="form-control" v-model="recommendationForm.contentText"></textarea>
          </div>
          <div class="col-12 d-flex gap-2">
            <button class="btn btn-primary" @click="saveRecommendation"><i class="bi bi-save"></i> Save</button>
            <button class="btn btn-outline-secondary" @click="resetRecommendationForm"><i class="bi bi-eraser"></i></button>
          </div>
        </div>
      </div>
    </div>

    <div class="card mb-3">
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-2"><select class="form-select" v-model="queryPageStore.recommendationQueryParam.recommendationType"><option v-for="item in recommendationTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option></select></div>
          <div class="col-md-3"><input class="form-control" placeholder="Recommendation title" v-model="queryPageStore.recommendationQueryParam.title"></div>
          <div class="col-md-2"><select class="form-select" v-model="queryPageStore.recommendationQueryParam.status"><option v-for="item in recommendationStatusOptions" :key="item.value" :value="item.value">{{ item.label }}</option></select></div>
          <div class="col-md-2"><select class="form-select" v-model="queryPageStore.recommendationQueryParam.acceptedFlag"><option value="">All accepted</option><option value="Y">Accepted</option><option value="N">Not accepted</option></select></div>
          <div class="col-md-2"><select class="form-select" v-model="queryPageStore.recommendationQueryParam.actionCreatedFlag"><option value="">All action</option><option value="Y">Created</option><option value="N">Not created</option></select></div>
          <div class="col-md-1"><button class="btn btn-primary" @click="queryRecommendations"><i class="bi bi-search"></i></button></div>
        </div>
      </div>
    </div>
    <GridPagination :progId="pageProgramId" :gridConfig="queryPageStore.recommendationGridConfig"
                    :changePageSelectMethod="changeRecommendationPage" :changeGridConfigRowMethod="changeRecommendationGridRow" />
    <Grid :progId="pageProgramId" :dataSource="recommendationList" :config="queryPageStore.recommendationGridConfig" />
    <div v-if="recommendationList.length === 0" class="text-center text-muted py-3">No recommendations</div>
  </div>
</template>
