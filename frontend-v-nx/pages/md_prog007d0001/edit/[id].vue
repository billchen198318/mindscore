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
const { showLoading, hideLoading } = useSwalLoading();

const formParam = ref({
    oid : route.params.id as string,
    workspaceCode : '',
    workspaceName : '',
    visionText : '',
    missionText : '',
    description : '',
    status : 'DRAFT'
});

const btnBack = () => router.back();

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
                visionText: response.data.value.visionText || '',
                missionText: response.data.value.missionText || '',
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
    visionText: formParam.value.visionText || null,
    missionText: formParam.value.missionText || null,
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

onMounted(() => {
    loadData();
});
</script>

<template>
<div class="row">
    <div class="col-12">
        <Toolbar
            :progId="pageProgramId"
            description="Strategy Workspace Edit"
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
        <label for="workspaceCode" class="form-label">Workspace Code</label>
        <input type="text" :class="['form-control', checkInvalid('workspaceCode', checkFields) ? 'is-invalid' : '']" id="workspaceCode" v-model="formParam.workspaceCode">
        <div v-if="checkInvalid('workspaceCode', checkFields)" class="invalid-feedback">{{ invalidFeedback('workspaceCode', checkFields) }}</div>
      </div>
      <div class="col-md-8">
        <label for="workspaceName" class="form-label">Workspace Name</label>
        <input type="text" :class="['form-control', checkInvalid('workspaceName', checkFields) ? 'is-invalid' : '']" id="workspaceName" v-model="formParam.workspaceName">
        <div v-if="checkInvalid('workspaceName', checkFields)" class="invalid-feedback">{{ invalidFeedback('workspaceName', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="status" class="form-label">Status</label>
        <select :class="['form-select', checkInvalid('status', checkFields) ? 'is-invalid' : '']" id="status" v-model="formParam.status">
          <option value="DRAFT">Draft</option>
          <option value="ACTIVE">Active</option>
          <option value="CLOSED">Closed</option>
          <option value="ARCHIVED">Archived</option>
        </select>
        <div v-if="checkInvalid('status', checkFields)" class="invalid-feedback">{{ invalidFeedback('status', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="visionText" class="form-label">Vision</label>
        <textarea class="form-control" id="visionText" rows="3" v-model="formParam.visionText"></textarea>
      </div>
      <div class="col-md-4">
        <label for="missionText" class="form-label">Mission</label>
        <textarea class="form-control" id="missionText" rows="3" v-model="formParam.missionText"></textarea>
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
