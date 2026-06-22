<script setup lang="ts">
import { computed, ref, onMounted } from 'vue';
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

const toDateInputValue = (value: any) => {
    if (!value) {
        return '';
    }
    if (typeof value === 'string') {
        return value.substring(0, 10);
    }
    const d = new Date(value);
    if (Number.isNaN(d.getTime())) {
        return '';
    }
    return d.toISOString().substring(0, 10);
};

const formParam = ref({
    oid : route.params.id as string,
    cycleCode : '',
    cycleName : '',
    periodType : 'QUARTER',
    startDate : '',
    endDate : '',
    status : 'DRAFT'
});

const originalStatus = ref('DRAFT');
const statusLabel: Record<string, string> = {
    DRAFT: 'Draft',
    ACTIVE: 'Active',
    CLOSED: 'Closed',
    ARCHIVED: 'Archived'
};
const allowedStatusTransitions: Record<string, string[]> = {
    DRAFT: ['DRAFT', 'ACTIVE', 'ARCHIVED'],
    ACTIVE: ['ACTIVE', 'CLOSED'],
    CLOSED: ['CLOSED', 'ARCHIVED'],
    ARCHIVED: ['ARCHIVED']
};
const allowedStatusOptions = computed(() =>
    (allowedStatusTransitions[originalStatus.value] || [originalStatus.value]).map(value => ({
        value,
        label: statusLabel[value] || value
    }))
);

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
                startDate: toDateInputValue(response.data.value.startDate),
                endDate: toDateInputValue(response.data.value.endDate)
            };
            originalStatus.value = response.data.value.status;
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
            originalStatus.value = formParam.value.status;
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
            description="OKR Cycle Edit"
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
        <label for="cycleCode" class="form-label">Cycle Code</label>
        <input type="text" :class="['form-control', checkInvalid('cycleCode', checkFields) ? 'is-invalid' : '']" id="cycleCode" v-model="formParam.cycleCode">
        <div v-if="checkInvalid('cycleCode', checkFields)" class="invalid-feedback">{{ invalidFeedback('cycleCode', checkFields) }}</div>
      </div>
      <div class="col-md-8">
        <label for="cycleName" class="form-label">Cycle Name</label>
        <input type="text" :class="['form-control', checkInvalid('cycleName', checkFields) ? 'is-invalid' : '']" id="cycleName" v-model="formParam.cycleName">
        <div v-if="checkInvalid('cycleName', checkFields)" class="invalid-feedback">{{ invalidFeedback('cycleName', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="periodType" class="form-label">Period Type</label>
        <select :class="['form-select', checkInvalid('periodType', checkFields) ? 'is-invalid' : '']" id="periodType" v-model="formParam.periodType">
          <option value="DAY">Day</option>
          <option value="WEEK">Week</option>
          <option value="MONTH">Month</option>
          <option value="QUARTER">Quarter</option>
          <option value="HALFYEAR">Half Year</option>
          <option value="YEAR">Year</option>
        </select>
        <div v-if="checkInvalid('periodType', checkFields)" class="invalid-feedback">{{ invalidFeedback('periodType', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="startDate" class="form-label">Start Date</label>
        <input type="date" :class="['form-control', checkInvalid('startDate', checkFields) ? 'is-invalid' : '']" id="startDate" v-model="formParam.startDate">
        <div v-if="checkInvalid('startDate', checkFields)" class="invalid-feedback">{{ invalidFeedback('startDate', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="endDate" class="form-label">End Date</label>
        <input type="date" :class="['form-control', checkInvalid('endDate', checkFields) ? 'is-invalid' : '']" id="endDate" v-model="formParam.endDate">
        <div v-if="checkInvalid('endDate', checkFields)" class="invalid-feedback">{{ invalidFeedback('endDate', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="status" class="form-label">Status</label>
        <select :class="['form-select', checkInvalid('status', checkFields) ? 'is-invalid' : '']" id="status" v-model="formParam.status">
          <option v-for="item in allowedStatusOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
        <div v-if="checkInvalid('status', checkFields)" class="invalid-feedback">{{ invalidFeedback('status', checkFields) }}</div>
      </div>
    </div>
    <div class="mt-4 d-flex gap-2">
      <button type="button" class="btn btn-primary" @click="btnUpdate"><i class="bi bi-save"></i> Save</button>
    </div>
  </div>
</div>
</template>
