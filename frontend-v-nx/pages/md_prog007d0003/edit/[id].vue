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
const pleaseSelectId = import.meta.env.VITE_PLEASE_SELECT_ID;
const pleaseSelectLabel = import.meta.env.VITE_PLEASE_SELECT_TEXT || import.meta.env.VITE_PLEASE_SELECT_LABEL || 'Please select';
const selectedWorkspaceOid = ref(pleaseSelectId);
const loadingData = ref(false);
const { showLoading, hideLoading } = useSwalLoading();

const defaultForm = () => ({
    oid : route.params.id as string,
    themeOid : pleaseSelectId,
    objectiveCode : '',
    objectiveName : '',
    weightValue : 0,
    sortNo : 0,
    description : ''
});

const formParam = ref<any>(defaultForm());

const btnBack = () => router.back();

const loadWorkspaceList = async () => {
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findWorkspaceList', {});
        if (response.data && import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
            workspaceList.value = response.data.value || [];
        }
    } catch (e: any) {
        alert(e);
    }
};

const loadThemeList = async (resetTheme = true) => {
    themeList.value = [];
    if (resetTheme) {
        formParam.value.themeOid = pleaseSelectId;
    }
    if (selectedWorkspaceOid.value === pleaseSelectId) {
        return;
    }
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findThemeList', {
            workspaceOid: selectedWorkspaceOid.value
        });
        if (response.data && import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
            themeList.value = response.data.value || [];
        }
    } catch (e: any) {
        alert(e);
    }
};

const resolveWorkspaceByTheme = async (themeOid: string) => {
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + '/MD_PROG007D0002/load', { oid: themeOid });
        if (response.data && import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
            selectedWorkspaceOid.value = response.data.value?.workspaceOid || pleaseSelectId;
        }
    } catch (e: any) {
        toast.warning(e?.message || e);
    }
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
                themeOid: response.data.value.themeOid || pleaseSelectId,
                description: response.data.value.description || ''
            };
            await resolveWorkspaceByTheme(formParam.value.themeOid);
            await loadThemeList(false);
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

const normalizePayload = () => ({
    ...formParam.value,
    description: formParam.value.description || null
});

const btnUpdate = async () => {
    checkFields.value = {};
    showLoading();
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/update', normalizePayload());
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
    await loadWorkspaceList();
    await loadData();
});

watch(
    () => selectedWorkspaceOid.value,
    () => {
        if (loadingData.value) {
            return;
        }
        loadThemeList(true);
    }
);
</script>

<template>
<div class="row">
    <div class="col-12">
        <Toolbar
            :progId="pageProgramId"
            description="Strategy Objective Edit"
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
      <div class="col-md-6">
        <label for="workspaceOid" class="form-label">Workspace</label>
        <select class="form-select" id="workspaceOid" v-model="selectedWorkspaceOid">
          <option :value="pleaseSelectId">{{ pleaseSelectLabel }}</option>
          <option v-for="workspace in workspaceList" :key="workspace.oid" :value="workspace.oid">
            {{ workspace.workspaceCode }} - {{ workspace.workspaceName }}
          </option>
        </select>
      </div>
      <div class="col-md-6">
        <label for="themeOid" class="form-label">Theme</label>
        <select :class="['form-select', checkInvalid('themeOid', checkFields) ? 'is-invalid' : '']" id="themeOid" v-model="formParam.themeOid">
          <option :value="pleaseSelectId">{{ pleaseSelectLabel }}</option>
          <option v-for="theme in themeList" :key="theme.oid" :value="theme.oid">
            {{ theme.themeCode }} - {{ theme.themeName }}
          </option>
        </select>
        <div v-if="checkInvalid('themeOid', checkFields)" class="invalid-feedback">{{ invalidFeedback('themeOid', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="objectiveCode" class="form-label">Objective Code</label>
        <input type="text" :class="['form-control', checkInvalid('objectiveCode', checkFields) ? 'is-invalid' : '']" id="objectiveCode" v-model="formParam.objectiveCode">
        <div v-if="checkInvalid('objectiveCode', checkFields)" class="invalid-feedback">{{ invalidFeedback('objectiveCode', checkFields) }}</div>
      </div>
      <div class="col-md-8">
        <label for="objectiveName" class="form-label">Objective Name</label>
        <input type="text" :class="['form-control', checkInvalid('objectiveName', checkFields) ? 'is-invalid' : '']" id="objectiveName" v-model="formParam.objectiveName">
        <div v-if="checkInvalid('objectiveName', checkFields)" class="invalid-feedback">{{ invalidFeedback('objectiveName', checkFields) }}</div>
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
      <div class="col-md-12">
        <label for="description" class="form-label">Description</label>
        <textarea class="form-control" id="description" rows="3" v-model="formParam.description"></textarea>
      </div>
    </div>
    <div class="mt-4 d-flex gap-2">
      <button type="button" class="btn btn-primary" @click="btnUpdate"><i class="bi bi-save"></i> Save</button>
    </div>
  </div>
</div>
</template>
