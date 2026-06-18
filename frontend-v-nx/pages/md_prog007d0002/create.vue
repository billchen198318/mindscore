<script setup lang="ts">
import { ref, onMounted } from 'vue';
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
const pleaseSelectId = import.meta.env.VITE_PLEASE_SELECT_ID;
const pleaseSelectLabel = import.meta.env.VITE_PLEASE_SELECT_TEXT || 'Please select';
const { showLoading, hideLoading } = useSwalLoading();

const formParam = ref({
    workspaceOid : pleaseSelectId,
    themeCode : '',
    themeName : '',
    weightValue : 0,
    sortNo : 0,
    description : ''
});

const btnBack = () => router.back();

const btnClear = () => {
    checkFields.value = {};
    formParam.value.workspaceOid = pleaseSelectId;
    formParam.value.themeCode = '';
    formParam.value.themeName = '';
    formParam.value.weightValue = 0;
    formParam.value.sortNo = 0;
    formParam.value.description = '';
};

const normalizePayload = () => ({
    ...formParam.value,
    description: formParam.value.description || null
});

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

const btnSave = async () => {
    checkFields.value = {};
    showLoading();
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/save', normalizePayload());
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

onMounted(() => {
    loadWorkspaceList();
});
</script>

<template>
<div class="row">
    <div class="col-12">
        <Toolbar
            :progId="pageProgramId"
            description="Strategy Theme Create"
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
      <button type="button" class="btn btn-primary" @click="btnSave"><i class="bi bi-save"></i> Save</button>
      <button type="button" class="btn btn-outline-secondary" @click="btnClear"><i class="bi bi-eraser"></i> Clear</button>
    </div>
  </div>
</div>
</template>
