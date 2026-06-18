<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useSwalLoading } from '@/composables/useSwalLoading';
import { toast } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

import Toolbar from '@/components/Toolbar.vue';
import { PageConstants } from './config';
import {
    getAxiosInstance,
    invalidFeedback,
    checkInvalid,
    escapeQifuHtmlMsg
} from '../../components/BaseHelper';

definePageMeta({ middleware: ['auth'] });

const router = useRouter();
const pageProgramId = ref(PageConstants.CreateId);
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
const { showLoading, hideLoading } = useSwalLoading();

const defaultForm = () => ({
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

const loadThemeList = async () => {
    themeList.value = [];
    selectedThemeOid.value = pleaseSelectId;
    formParam.value.strategyObjectiveOid = pleaseSelectId;
    strategyObjectiveList.value = [];
    if (selectedWorkspaceOid.value === pleaseSelectId) {
        return;
    }
    themeList.value = await postList('/findThemeList', { workspaceOid: selectedWorkspaceOid.value });
};

const loadStrategyObjectiveList = async () => {
    strategyObjectiveList.value = [];
    formParam.value.strategyObjectiveOid = pleaseSelectId;
    if (selectedThemeOid.value === pleaseSelectId) {
        return;
    }
    strategyObjectiveList.value = await postList('/findStrategyObjectiveList', { themeOid: selectedThemeOid.value });
};

const loadOkrObjectiveList = async () => {
    okrObjectiveList.value = [];
    formParam.value.linkOid = pleaseSelectId;
    if (selectedCycleOid.value === pleaseSelectId) {
        return;
    }
    okrObjectiveList.value = await postList('/findOkrObjectiveList', { cycleOid: selectedCycleOid.value });
};

const btnClear = () => {
    checkFields.value = {};
    selectedWorkspaceOid.value = pleaseSelectId;
    selectedThemeOid.value = pleaseSelectId;
    selectedCycleOid.value = pleaseSelectId;
    themeList.value = [];
    strategyObjectiveList.value = [];
    okrObjectiveList.value = [];
    formParam.value = defaultForm();
};

const btnSave = async () => {
    checkFields.value = {};
    showLoading();
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/save', formParam.value);
        hideLoading();
        if (response.data) {
            checkFields.value = response.data.checkFields || {};
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            toast.success(response.data.message);
            btnClear();
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
});

watch(() => selectedWorkspaceOid.value, () => loadThemeList());
watch(() => selectedThemeOid.value, () => loadStrategyObjectiveList());
watch(() => selectedCycleOid.value, () => loadOkrObjectiveList());
watch(
    () => formParam.value.linkType,
    () => {
        formParam.value.linkOid = pleaseSelectId;
    }
);
</script>

<template>
<div class="row">
    <div class="col-12">
        <Toolbar
            :progId="pageProgramId"
            description="Strategy Objective Link Create"
            refreshFlag="Y"
            @refreshMethod="btnClear"
            backFlag="Y"
            @backMethod="btnBack"
            saveFlag="Y"
            @saveMethod="btnSave"
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
      <button type="button" class="btn btn-primary" @click="btnSave"><i class="bi bi-save"></i> Save</button>
      <button type="button" class="btn btn-outline-secondary" @click="btnClear"><i class="bi bi-eraser"></i> Clear</button>
    </div>
  </div>
</div>
</template>
