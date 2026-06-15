<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useSwalLoading } from '@/composables/useSwalLoading';
import { toast } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

import Toolbar from '@/components/Toolbar.vue';
import { PageConstants } from './config';
import { getAxiosInstance, invalidFeedback, checkInvalid, escapeQifuHtmlMsg } from '../../components/BaseHelper';

definePageMeta({ middleware: ['auth'] });

const router = useRouter();
const pageProgramId = ref(PageConstants.CreateId);
const checkFields = ref<any>({});
const kpiList = ref<any[]>([]);
const { showLoading, hideLoading } = useSwalLoading();
const pleaseSelectId = import.meta.env.VITE_PLEASE_SELECT_ID;

const scopeTypeOptions = [
    { value: 'GLOBAL', label: 'Global' },
    { value: 'KPI', label: 'KPI override' }
];
const colorTypeOptions = [
    { value: 'CUSTOM', label: 'Score range' },
    { value: 'DEFAULT', label: 'Default fallback' }
];
const scoreStatusOptions = [
    { value: 'GOOD', label: 'Good' },
    { value: 'WARNING', label: 'Warning' },
    { value: 'BAD', label: 'Bad' },
    { value: 'UNKNOWN', label: 'Unknown' }
];

const defaultForm = () => ({
    scopeType: 'GLOBAL',
    scopeKey: 'GLOBAL',
    kpiOid: pleaseSelectId,
    colorType: 'CUSTOM',
    colorCode: '',
    colorName: '',
    scoreMin: 80,
    scoreMax: 100,
    scoreStatus: 'GOOD',
    fontColor: '#212529',
    bgColor: '#198754',
    sortNo: 100,
    enabled: 'Y',
    description: ''
});
const formParam = ref<any>(defaultForm());

const btnBack = () => router.back();

const loadKpiList = async () => {
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findKpiList', {});
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            kpiList.value = (response.data.value || []).filter((item: any) => item.enabled === 'Y');
        }
    } catch (e: any) {
        toast.warning(e?.message || e);
    }
};

const btnClear = () => {
    checkFields.value = {};
    formParam.value = defaultForm();
};

const payload = () => ({
    ...formParam.value,
    kpiOid: formParam.value.scopeType === 'KPI' ? formParam.value.kpiOid : null,
    scoreMin: formParam.value.colorType === 'CUSTOM' ? formParam.value.scoreMin : null,
    scoreMax: formParam.value.colorType === 'CUSTOM' ? formParam.value.scoreMax : null
});

const btnSave = async () => {
    checkFields.value = {};
    showLoading();
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/save', payload());
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

watch(() => formParam.value.scopeType, () => {
    if (formParam.value.scopeType === 'GLOBAL') {
        formParam.value.kpiOid = pleaseSelectId;
        formParam.value.scopeKey = 'GLOBAL';
    }
});
watch(() => formParam.value.colorType, () => {
    if (formParam.value.colorType === 'DEFAULT') {
        formParam.value.scoreMin = null;
        formParam.value.scoreMax = null;
    }
});

onMounted(() => {
    loadKpiList();
});
</script>

<template>
<div class="row">
  <div class="col-12">
    <Toolbar
      :progId="pageProgramId"
      description="Create KPI Score Color"
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
      <div class="col-md-3">
        <label for="scopeType" class="form-label">Scope</label>
        <select :class="['form-select', checkInvalid('scopeType', checkFields) ? 'is-invalid' : '']" id="scopeType" v-model="formParam.scopeType">
          <option v-for="item in scopeTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
        <div v-if="checkInvalid('scopeType', checkFields)" class="invalid-feedback">{{ invalidFeedback('scopeType', checkFields) }}</div>
      </div>
      <div v-if="formParam.scopeType === 'KPI'" class="col-md-5">
        <label for="kpiOid" class="form-label">KPI</label>
        <select :class="['form-select', checkInvalid('kpiOid', checkFields) ? 'is-invalid' : '']" id="kpiOid" v-model="formParam.kpiOid">
          <option :value="pleaseSelectId">Please select</option>
          <option v-for="item in kpiList" :key="item.oid" :value="item.oid">{{ item.kpiCode }} - {{ item.kpiName }}</option>
        </select>
        <div v-if="checkInvalid('kpiOid', checkFields)" class="invalid-feedback">{{ invalidFeedback('kpiOid', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="colorType" class="form-label">Color Type</label>
        <select :class="['form-select', checkInvalid('colorType', checkFields) ? 'is-invalid' : '']" id="colorType" v-model="formParam.colorType">
          <option v-for="item in colorTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
        <div v-if="checkInvalid('colorType', checkFields)" class="invalid-feedback">{{ invalidFeedback('colorType', checkFields) }}</div>
      </div>

      <div class="col-md-4">
        <label for="colorCode" class="form-label">Color Code</label>
        <input type="text" :class="['form-control', checkInvalid('colorCode', checkFields) ? 'is-invalid' : '']" id="colorCode" v-model="formParam.colorCode">
        <div v-if="checkInvalid('colorCode', checkFields)" class="invalid-feedback">{{ invalidFeedback('colorCode', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="colorName" class="form-label">Color Name</label>
        <input type="text" :class="['form-control', checkInvalid('colorName', checkFields) ? 'is-invalid' : '']" id="colorName" v-model="formParam.colorName">
        <div v-if="checkInvalid('colorName', checkFields)" class="invalid-feedback">{{ invalidFeedback('colorName', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="scoreStatus" class="form-label">Score Status</label>
        <select :class="['form-select', checkInvalid('scoreStatus', checkFields) ? 'is-invalid' : '']" id="scoreStatus" v-model="formParam.scoreStatus">
          <option v-for="item in scoreStatusOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
        <div v-if="checkInvalid('scoreStatus', checkFields)" class="invalid-feedback">{{ invalidFeedback('scoreStatus', checkFields) }}</div>
      </div>

      <div v-if="formParam.colorType === 'CUSTOM'" class="col-md-3">
        <label for="scoreMin" class="form-label">Score Min</label>
        <input type="number" step="0.0001" :class="['form-control', checkInvalid('scoreMin', checkFields) ? 'is-invalid' : '']" id="scoreMin" v-model.number="formParam.scoreMin">
        <div v-if="checkInvalid('scoreMin', checkFields)" class="invalid-feedback">{{ invalidFeedback('scoreMin', checkFields) }}</div>
      </div>
      <div v-if="formParam.colorType === 'CUSTOM'" class="col-md-3">
        <label for="scoreMax" class="form-label">Score Max</label>
        <input type="number" step="0.0001" :class="['form-control', checkInvalid('scoreMax', checkFields) ? 'is-invalid' : '']" id="scoreMax" v-model.number="formParam.scoreMax">
        <div v-if="checkInvalid('scoreMax', checkFields)" class="invalid-feedback">{{ invalidFeedback('scoreMax', checkFields) }}</div>
      </div>
      <div class="col-md-3">
        <label for="fontColor" class="form-label">Font Color</label>
        <input type="color" :class="['form-control form-control-color', checkInvalid('fontColor', checkFields) ? 'is-invalid' : '']" id="fontColor" v-model="formParam.fontColor">
        <div v-if="checkInvalid('fontColor', checkFields)" class="invalid-feedback d-block">{{ invalidFeedback('fontColor', checkFields) }}</div>
      </div>
      <div class="col-md-3">
        <label for="bgColor" class="form-label">Background Color</label>
        <input type="color" :class="['form-control form-control-color', checkInvalid('bgColor', checkFields) ? 'is-invalid' : '']" id="bgColor" v-model="formParam.bgColor">
        <div v-if="checkInvalid('bgColor', checkFields)" class="invalid-feedback d-block">{{ invalidFeedback('bgColor', checkFields) }}</div>
      </div>

      <div class="col-md-3">
        <label for="sortNo" class="form-label">Sort</label>
        <input type="number" class="form-control" id="sortNo" v-model.number="formParam.sortNo">
      </div>
      <div class="col-md-3">
        <label for="enabled" class="form-label">Enabled</label>
        <select :class="['form-select', checkInvalid('enabled', checkFields) ? 'is-invalid' : '']" id="enabled" v-model="formParam.enabled">
          <option value="Y">Yes</option>
          <option value="N">No</option>
        </select>
        <div v-if="checkInvalid('enabled', checkFields)" class="invalid-feedback">{{ invalidFeedback('enabled', checkFields) }}</div>
      </div>
      <div class="col-md-6">
        <label class="form-label">Preview</label>
        <div>
          <span class="d-inline-block px-3 py-2 rounded" :style="{ color: formParam.fontColor, backgroundColor: formParam.bgColor }">TEST</span>
        </div>
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
