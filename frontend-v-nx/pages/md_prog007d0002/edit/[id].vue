<script setup lang="ts">
import { ref, onMounted } from 'vue';
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
const pleaseSelectId = import.meta.env.VITE_PLEASE_SELECT_ID;
const pleaseSelectLabel = import.meta.env.VITE_PLEASE_SELECT_TEXT || 'Please select';
const { showLoading, hideLoading } = useSwalLoading();

const formParam = ref({
    oid : route.params.id as string,
    workspaceOid : pleaseSelectId,
    themeCode : '',
    themeName : '',
    weightValue : 0,
    sortNo : 0,
    description : ''
});

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

const loadData = async () => {
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
                workspaceOid: response.data.value.workspaceOid || pleaseSelectId,
                description: response.data.value.description || ''
            };
        } else {
            toast.error('error, null');
            router.push(getUrlPrefixFromProgItem(getProgItem(PageConstants.QueryId)));
        }
    } catch (e: any) {
        hideLoading();
        alert(e);
        router.push(getUrlPrefixFromProgItem(getProgItem(PageConstants.QueryId)));
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
    loadData();
});
</script>

<template>
<div class="row">
    <div class="col-12">
        <Toolbar
            :progId="pageProgramId"
            description="Strategy Theme Edit"
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
        <select :class="['form-select', checkInvalid('workspaceOid', checkFields) ? 'is-invalid' : '']" id="workspaceOid" v-model="formParam.workspaceOid">
          <option :value="pleaseSelectId">{{ pleaseSelectLabel }}</option>
          <option v-for="workspace in workspaceList" :key="workspace.oid" :value="workspace.oid">
            {{ workspace.workspaceCode }} - {{ workspace.workspaceName }}
          </option>
        </select>
        <div v-if="checkInvalid('workspaceOid', checkFields)" class="invalid-feedback">{{ invalidFeedback('workspaceOid', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="themeCode" class="form-label">Theme Code</label>
        <input type="text" :class="['form-control', checkInvalid('themeCode', checkFields) ? 'is-invalid' : '']" id="themeCode" v-model="formParam.themeCode">
        <div v-if="checkInvalid('themeCode', checkFields)" class="invalid-feedback">{{ invalidFeedback('themeCode', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="themeName" class="form-label">Theme Name</label>
        <input type="text" :class="['form-control', checkInvalid('themeName', checkFields) ? 'is-invalid' : '']" id="themeName" v-model="formParam.themeName">
        <div v-if="checkInvalid('themeName', checkFields)" class="invalid-feedback">{{ invalidFeedback('themeName', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="weightValue" class="form-label">Weight</label>
        <input type="number" min="0" step="0.01" :class="['form-control', checkInvalid('weightValue', checkFields) ? 'is-invalid' : '']" id="weightValue" v-model="formParam.weightValue">
        <div v-if="checkInvalid('weightValue', checkFields)" class="invalid-feedback">{{ invalidFeedback('weightValue', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="sortNo" class="form-label">Sort No</label>
        <input type="number" min="0" step="1" :class="['form-control', checkInvalid('sortNo', checkFields) ? 'is-invalid' : '']" id="sortNo" v-model="formParam.sortNo">
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
