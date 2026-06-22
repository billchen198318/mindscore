<script setup lang="ts">
import { ref } from 'vue';
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
const { showLoading, hideLoading } = useSwalLoading();

const formParam = ref({
    cycleCode : '',
    cycleName : '',
    periodType : 'QUARTER',
    startDate : '',
    endDate : '',
    status : 'DRAFT'
});

const btnBack = () => router.back();

const btnClear = () => {
    checkFields.value = {};
    formParam.value.cycleCode = '';
    formParam.value.cycleName = '';
    formParam.value.periodType = 'QUARTER';
    formParam.value.startDate = '';
    formParam.value.endDate = '';
    formParam.value.status = 'DRAFT';
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
</script>

<template>
<div class="row">
    <div class="col-12">
        <Toolbar
            :progId="pageProgramId"
            description="OKR Cycle Create"
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
        <input type="text" class="form-control" id="status" value="Draft" readonly>
        <div v-if="checkInvalid('status', checkFields)" class="invalid-feedback">{{ invalidFeedback('status', checkFields) }}</div>
      </div>
    </div>
    <div class="mt-4 d-flex gap-2">
      <button type="button" class="btn btn-primary" @click="btnSave"><i class="bi bi-save"></i> Save</button>
      <button type="button" class="btn btn-outline-secondary" @click="btnClear"><i class="bi bi-eraser"></i> Clear</button>
    </div>
  </div>
</div>
</template>
