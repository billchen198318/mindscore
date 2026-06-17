<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useSwalLoading } from '@/composables/useSwalLoading';
import { toast } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

import Toolbar from '@/components/Toolbar.vue';
import { PageConstants } from '../config';
import {
    getAxiosInstance,
    invalidFeedback,
    checkInvalid,
    escapeQifuHtmlMsg,
    getProgItem,
    getUrlPrefixFromProgItem
} from '../../../components/BaseHelper';

definePageMeta({ middleware: ['auth'] });

const router = useRouter();
const route = useRoute();
const pageProgramId = ref(PageConstants.EditId);
const checkFields = ref<any>({});
const workspaceList = ref<any[]>([]);
const themeList = ref<any[]>([]);
const strategyObjectiveList = ref<any[]>([]);
const kpiList = ref<any[]>([]);
const cycleList = ref<any[]>([]);
const okrObjectiveList = ref<any[]>([]);
const pleaseSelectId = import.meta.env.VITE_PLEASE_SELECT_ID;
const pleaseSelectLabel = import.meta.env.VITE_PLEASE_SELECT_TEXT || import.meta.env.VITE_PLEASE_SELECT_LABEL || 'Please select';
const selectedWorkspaceOid = ref(pleaseSelectId);
const selectedThemeOid = ref(pleaseSelectId);
const selectedCycleOid = ref(pleaseSelectId);
const loadingData = ref(false);
const { showLoading, hideLoading } = useSwalLoading();

const defaultForm = () => ({
    oid : route.params.id as string,
    strategyObjectiveOid : pleaseSelectId,
    linkType : 'KPI',
    linkOid : pleaseSelectId,
    weightValue : 0,
    sortNo : 0
});

const formParam = ref<any>(defaultForm());

const btnBack = () => router.back();

const postList = async (url: string, payload: any = {}) => {
    const axiosInstance = getAxiosInstance();
    const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + url, payload);
    return response.data && import.meta.env.VITE_SUCCESS_FLAG == response.data.success ? response.data.value || [] : [];
};

const loadWorkspaceList = async () => workspaceList.value = await postList('/findWorkspaceList');
const loadKpiList = async () => kpiList.value = await postList('/findKpiList');
const loadCycleList = async () => cycleList.value = await postList('/findCycleList');

const loadThemeList = async (resetTheme = true) => {
    themeList.value = [];
    if (resetTheme) {
        selectedThemeOid.value = pleaseSelectId;
        formParam.value.strategyObjectiveOid = pleaseSelectId;
        strategyObjectiveList.value = [];
    }
    if (selectedWorkspaceOid.value === pleaseSelectId) {
        return;
    }
    themeList.value = await postList('/findThemeList', { workspaceOid: selectedWorkspaceOid.value });
};

const loadStrategyObjectiveList = async (resetObjective = true) => {
    strategyObjectiveList.value = [];
    if (resetObjective) {
        formParam.value.strategyObjectiveOid = pleaseSelectId;
    }
    if (selectedThemeOid.value === pleaseSelectId) {
        return;
    }
    strategyObjectiveList.value = await postList('/findStrategyObjectiveList', { themeOid: selectedThemeOid.value });
};

const loadOkrObjectiveList = async (resetLink = true) => {
    okrObjectiveList.value = [];
    if (resetLink) {
        formParam.value.linkOid = pleaseSelectId;
    }
    if (selectedCycleOid.value === pleaseSelectId) {
        return;
    }
    okrObjectiveList.value = await postList('/findOkrObjectiveList', { cycleOid: selectedCycleOid.value });
};

const resolveStrategyHierarchy = async (strategyObjectiveOid: string) => {
    const axiosInstance = getAxiosInstance();
    const objectiveResponse = await axiosInstance.post(import.meta.env.VITE_API_URL + '/MD_PROG007D0003/load', { oid: strategyObjectiveOid });
    if (!objectiveResponse.data || import.meta.env.VITE_SUCCESS_FLAG != objectiveResponse.data.success) {
        return;
    }
    selectedThemeOid.value = objectiveResponse.data.value?.themeOid || pleaseSelectId;
    const themeResponse = await axiosInstance.post(import.meta.env.VITE_API_URL + '/MD_PROG007D0002/load', { oid: selectedThemeOid.value });
    if (themeResponse.data && import.meta.env.VITE_SUCCESS_FLAG == themeResponse.data.success) {
        selectedWorkspaceOid.value = themeResponse.data.value?.workspaceOid || pleaseSelectId;
    }
    await loadThemeList(false);
    await loadStrategyObjectiveList(false);
};

const resolveOkrCycle = async (okrObjectiveOid: string) => {
    const axiosInstance = getAxiosInstance();
    const response = await axiosInstance.post(import.meta.env.VITE_API_URL + '/MD_PROG006D0002/load', { oid: okrObjectiveOid });
    if (response.data && import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
        selectedCycleOid.value = response.data.value?.objective?.cycleOid || pleaseSelectId;
    }
    await loadOkrObjectiveList(false);
};

const loadData = async () => {
    loadingData.value = true;
    showLoading();
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/load', { 'oid' : formParam.value.oid });
        hideLoading();
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(response.data.message);
                router.push(getUrlPrefixFromProgItem(getProgItem(PageConstants.QueryId)));
                return;
            }
            formParam.value = {
                ...response.data.value,
                strategyObjectiveOid: response.data.value.strategyObjectiveOid || pleaseSelectId,
                linkOid: response.data.value.linkOid || pleaseSelectId
            };
            await resolveStrategyHierarchy(formParam.value.strategyObjectiveOid);
            if (formParam.value.linkType === 'OKR_OBJECTIVE') {
                await resolveOkrCycle(formParam.value.linkOid);
            }
        } else {
            toast.error('error, null');
            router.push(getUrlPrefixFromProgItem(getProgItem(PageConstants.QueryId)));
        }
    } catch (e: any) {
        hideLoading();
        alert(e);
        router.push(getUrlPrefixFromProgItem(getProgItem(PageConstants.QueryId)));
    } finally {
        loadingData.value = false;
    }
};

const btnUpdate = async () => {
    checkFields.value = {};
    showLoading();
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/update', formParam.value);
        hideLoading();
        if (response.data) {
            checkFields.value = response.data.checkFields || {};
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            toast.success(response.data.message);
        } else {
            toast.error('error, null');
        }
    } catch (e: any) {
        hideLoading();
        alert(e);
    }
};

onMounted(async () => {
    await Promise.all([loadWorkspaceList(), loadKpiList(), loadCycleList()]);
    await loadData();
});

watch(() => selectedWorkspaceOid.value, () => {
    if (loadingData.value) {
        return;
    }
    loadThemeList(true);
});

watch(() => selectedThemeOid.value, () => {
    if (loadingData.value) {
        return;
    }
    loadStrategyObjectiveList(true);
});

watch(() => selectedCycleOid.value, () => {
    if (loadingData.value) {
        return;
    }
    loadOkrObjectiveList(true);
});

watch(
    () => formParam.value.linkType,
    () => {
        if (loadingData.value) {
            return;
        }
        formParam.value.linkOid = pleaseSelectId;
    }
);
</script>

<template>
<div class="row">
    <div class="col-12">
        <Toolbar
            :progId="pageProgramId"
            description="Strategy Objective Link Edit"
            refreshFlag="Y"
            @refreshMethod="loadData"
            backFlag="Y"
            @backMethod="btnBack"
            saveFlag="Y"
            @saveMethod="btnUpdate"
        />
    </div>
</div>

<div class="card mb-4">
  <div class="card-body">
    <div class="row g-3">
      <div class="col-md-4">
        <label for="workspaceOid" class="form-label">Workspace</label>
        <select class="form-select" id="workspaceOid" v-model="selectedWorkspaceOid">
          <option :value="pleaseSelectId">{{ pleaseSelectLabel }}</option>
          <option v-for="workspace in workspaceList" :key="workspace.oid" :value="workspace.oid">{{ workspace.workspaceCode }} - {{ workspace.workspaceName }}</option>
        </select>
      </div>
      <div class="col-md-4">
        <label for="themeOid" class="form-label">Theme</label>
        <select class="form-select" id="themeOid" v-model="selectedThemeOid">
          <option :value="pleaseSelectId">{{ pleaseSelectLabel }}</option>
          <option v-for="theme in themeList" :key="theme.oid" :value="theme.oid">{{ theme.themeCode }} - {{ theme.themeName }}</option>
        </select>
      </div>
      <div class="col-md-4">
        <label for="strategyObjectiveOid" class="form-label">Strategy Objective</label>
        <select :class="['form-select', checkInvalid('strategyObjectiveOid', checkFields) ? 'is-invalid' : '']" id="strategyObjectiveOid" v-model="formParam.strategyObjectiveOid">
          <option :value="pleaseSelectId">{{ pleaseSelectLabel }}</option>
          <option v-for="objective in strategyObjectiveList" :key="objective.oid" :value="objective.oid">{{ objective.objectiveCode }} - {{ objective.objectiveName }}</option>
        </select>
        <div v-if="checkInvalid('strategyObjectiveOid', checkFields)" class="invalid-feedback">{{ invalidFeedback('strategyObjectiveOid', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="linkType" class="form-label">Link Type</label>
        <select :class="['form-select', checkInvalid('linkType', checkFields) ? 'is-invalid' : '']" id="linkType" v-model="formParam.linkType">
          <option value="KPI">KPI</option>
          <option value="OKR_OBJECTIVE">OKR Objective</option>
        </select>
        <div v-if="checkInvalid('linkType', checkFields)" class="invalid-feedback">{{ invalidFeedback('linkType', checkFields) }}</div>
      </div>
      <div v-if="formParam.linkType === 'OKR_OBJECTIVE'" class="col-md-4">
        <label for="cycleOid" class="form-label">OKR Cycle</label>
        <select class="form-select" id="cycleOid" v-model="selectedCycleOid">
          <option :value="pleaseSelectId">{{ pleaseSelectLabel }}</option>
          <option v-for="cycle in cycleList" :key="cycle.oid" :value="cycle.oid">{{ cycle.cycleCode }} - {{ cycle.cycleName }}</option>
        </select>
      </div>
      <div class="col-md-4">
        <label for="linkOid" class="form-label">Link Target</label>
        <select :class="['form-select', checkInvalid('linkOid', checkFields) ? 'is-invalid' : '']" id="linkOid" v-model="formParam.linkOid">
          <option :value="pleaseSelectId">{{ pleaseSelectLabel }}</option>
          <option v-if="formParam.linkType === 'KPI'" v-for="kpi in kpiList" :key="kpi.oid" :value="kpi.oid">{{ kpi.kpiCode }} - {{ kpi.kpiName }}</option>
          <option v-if="formParam.linkType === 'OKR_OBJECTIVE'" v-for="objective in okrObjectiveList" :key="objective.oid" :value="objective.oid">{{ objective.objectiveCode }} - {{ objective.objectiveName }}</option>
        </select>
        <div v-if="checkInvalid('linkOid', checkFields)" class="invalid-feedback">{{ invalidFeedback('linkOid', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="weightValue" class="form-label">Weight</label>
        <input type="number" min="0" step="0.01" :class="['form-control', checkInvalid('weightValue', checkFields) ? 'is-invalid' : '']" id="weightValue" v-model.number="formParam.weightValue">
        <div v-if="checkInvalid('weightValue', checkFields)" class="invalid-feedback">{{ invalidFeedback('weightValue', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="sortNo" class="form-label">Sort No</label>
        <input type="number" min="0" step="1" :class="['form-control', checkInvalid('sortNo', checkFields) ? 'is-invalid' : '']" id="sortNo" v-model.number="formParam.sortNo">
        <div v-if="checkInvalid('sortNo', checkFields)" class="invalid-feedback">{{ invalidFeedback('sortNo', checkFields) }}</div>
      </div>
    </div>
    <div class="mt-4 d-flex gap-2">
      <button type="button" class="btn btn-primary" @click="btnUpdate"><i class="bi bi-save"></i> Save</button>
    </div>
  </div>
</div>
</template>
