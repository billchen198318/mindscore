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
const cycleList = ref<any[]>([]);
const objectiveList = ref<any[]>([]);
const pleaseSelectId = import.meta.env.VITE_PLEASE_SELECT_ID;
const pleaseSelectLabel = import.meta.env.VITE_PLEASE_SELECT_LABEL;
const selectedCycleOid = ref(pleaseSelectId);
const loadingData = ref(false);
const { showLoading, hideLoading } = useSwalLoading();

const defaultForm = () => ({
    oid : route.params.id as string,
    objectiveOid : pleaseSelectId,
    krCode : '',
    krName : '',
    krType : 'INCREASE',
    startValue : null as number | null,
    targetValue : null as number | null,
    currentValue : null as number | null,
    progressValue : 0,
    weightValue : 0,
    unitName : '',
    sortNo : 0,
    status : 'DRAFT'
});

const formParam = ref<any>(defaultForm());

const btnBack = () => router.back();

const loadCycleList = async () => {
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findCycleList', {});
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            cycleList.value = response.data.value || [];
        }
    } catch (e: any) {
        toast.warning(e?.message || e);
    }
};

const loadObjectiveList = async (resetObjective = true) => {
    objectiveList.value = [];
    if (resetObjective) {
        formParam.value.objectiveOid = pleaseSelectId;
    }
    if (selectedCycleOid.value === pleaseSelectId) {
        return;
    }
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findObjectiveList', {
            cycleOid: selectedCycleOid.value
        });
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            objectiveList.value = response.data.value || [];
        }
    } catch (e: any) {
        toast.warning(e?.message || e);
    }
};

const resolveCycleByObjective = async (objectiveOid: string) => {
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + '/MD_PROG006D0002/load', { oid: objectiveOid });
        if (response.data && import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
            selectedCycleOid.value = response.data.value?.objective?.cycleOid || pleaseSelectId;
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
                startValue: response.data.value.startValue ?? null,
                targetValue: response.data.value.targetValue ?? null,
                currentValue: response.data.value.currentValue ?? null,
                unitName: response.data.value.unitName || ''
            };
            await resolveCycleByObjective(formParam.value.objectiveOid);
            await loadObjectiveList(false);
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
    startValue : formParam.value.startValue === '' ? null : formParam.value.startValue,
    targetValue : formParam.value.targetValue === '' ? null : formParam.value.targetValue,
    currentValue : formParam.value.currentValue === '' ? null : formParam.value.currentValue,
    unitName : formParam.value.unitName || null
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
    await loadCycleList();
    await loadData();
});

watch(
    () => selectedCycleOid.value,
    () => {
        if (loadingData.value) {
            return;
        }
        loadObjectiveList(true);
    }
);
</script>

<template>
<div class="row">
    <div class="col-12">
        <Toolbar
            :progId="pageProgramId"
            description="OKR Key Result Edit"
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
        <label for="cycleOid" class="form-label">Cycle</label>
        <select class="form-select" id="cycleOid" v-model="selectedCycleOid">
          <option :value="pleaseSelectId">{{ pleaseSelectLabel }}</option>
          <option v-for="item in cycleList" :key="item.oid" :value="item.oid">{{ item.cycleCode }} - {{ item.cycleName }}</option>
        </select>
      </div>
      <div class="col-md-6">
        <label for="objectiveOid" class="form-label">Objective</label>
        <select :class="['form-select', checkInvalid('objectiveOid', checkFields) ? 'is-invalid' : '']" id="objectiveOid" v-model="formParam.objectiveOid">
          <option :value="pleaseSelectId">{{ pleaseSelectLabel }}</option>
          <option v-for="item in objectiveList" :key="item.oid" :value="item.oid">{{ item.objectiveCode }} - {{ item.objectiveName }}</option>
        </select>
        <div v-if="checkInvalid('objectiveOid', checkFields)" class="invalid-feedback">{{ invalidFeedback('objectiveOid', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="krCode" class="form-label">KR Code</label>
        <input type="text" :class="['form-control', checkInvalid('krCode', checkFields) ? 'is-invalid' : '']" id="krCode" v-model="formParam.krCode">
        <div v-if="checkInvalid('krCode', checkFields)" class="invalid-feedback">{{ invalidFeedback('krCode', checkFields) }}</div>
      </div>
      <div class="col-md-8">
        <label for="krName" class="form-label">KR Name</label>
        <input type="text" :class="['form-control', checkInvalid('krName', checkFields) ? 'is-invalid' : '']" id="krName" v-model="formParam.krName">
        <div v-if="checkInvalid('krName', checkFields)" class="invalid-feedback">{{ invalidFeedback('krName', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="krType" class="form-label">KR Type</label>
        <select :class="['form-select', checkInvalid('krType', checkFields) ? 'is-invalid' : '']" id="krType" v-model="formParam.krType">
          <option value="INCREASE">Increase</option>
          <option value="DECREASE">Decrease</option>
          <option value="PERCENT">Percent</option>
          <option value="MILESTONE">Milestone</option>
          <option value="BINARY">Binary</option>
          <option value="MANUAL">Manual</option>
        </select>
        <div v-if="checkInvalid('krType', checkFields)" class="invalid-feedback">{{ invalidFeedback('krType', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="unitName" class="form-label">Unit</label>
        <input type="text" class="form-control" id="unitName" v-model="formParam.unitName">
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
        <label for="startValue" class="form-label">Start Value</label>
        <input type="number" step="0.000001" class="form-control" id="startValue" v-model.number="formParam.startValue">
      </div>
      <div class="col-md-4">
        <label for="targetValue" class="form-label">Target Value</label>
        <input type="number" step="0.000001" class="form-control" id="targetValue" v-model.number="formParam.targetValue">
      </div>
      <div class="col-md-4">
        <label for="currentValue" class="form-label">Current Value</label>
        <input type="number" step="0.000001" class="form-control" id="currentValue" v-model.number="formParam.currentValue">
      </div>
      <div class="col-md-4">
        <label for="progressValue" class="form-label">Progress</label>
        <input type="number" min="0" max="100" step="0.0001" :class="['form-control', checkInvalid('progressValue', checkFields) ? 'is-invalid' : '']" id="progressValue" v-model.number="formParam.progressValue">
        <div v-if="checkInvalid('progressValue', checkFields)" class="invalid-feedback">{{ invalidFeedback('progressValue', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="weightValue" class="form-label">Weight</label>
        <input type="number" min="0" max="100" step="0.0001" :class="['form-control', checkInvalid('weightValue', checkFields) ? 'is-invalid' : '']" id="weightValue" v-model.number="formParam.weightValue">
        <div v-if="checkInvalid('weightValue', checkFields)" class="invalid-feedback">{{ invalidFeedback('weightValue', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="sortNo" class="form-label">Sort No</label>
        <input type="number" min="0" :class="['form-control', checkInvalid('sortNo', checkFields) ? 'is-invalid' : '']" id="sortNo" v-model.number="formParam.sortNo">
        <div v-if="checkInvalid('sortNo', checkFields)" class="invalid-feedback">{{ invalidFeedback('sortNo', checkFields) }}</div>
      </div>
    </div>
    <div class="mt-4 d-flex gap-2">
      <button type="button" class="btn btn-primary" @click="btnUpdate"><i class="bi bi-save"></i> Save</button>
    </div>
  </div>
</div>
</template>
