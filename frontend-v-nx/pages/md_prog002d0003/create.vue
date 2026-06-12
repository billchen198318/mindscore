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
const formulaList = ref<any[]>([]);
const { showLoading, hideLoading } = useSwalLoading();

const managementModeOptions = [
    { value: '', label: '不限' },
    { value: 'HIGHER_BETTER', label: '越高越好' },
    { value: 'LOWER_BETTER', label: '越低越好' },
    { value: 'TARGET_RANGE', label: '目標區間' }
];

const compareModeOptions = [
    { value: '', label: '不限' },
    { value: 'GTE', label: '大於等於目標' },
    { value: 'LTE', label: '小於等於目標' },
    { value: 'BETWEEN', label: '介於上下限' },
    { value: 'EQ', label: '等於目標' }
];

const periodTypeOptions = [
    { value: '', label: '不限' },
    { value: 'DAY', label: '日' },
    { value: 'WEEK', label: '週' },
    { value: 'MONTH', label: '月' },
    { value: 'QUARTER', label: '季' },
    { value: 'HALFYEAR', label: '半年' },
    { value: 'YEAR', label: '年' }
];

const dataTypeOptions = [
    { value: '', label: '不限' },
    { value: 'DECIMAL', label: '數值' },
    { value: 'PERCENT', label: '百分比' },
    { value: 'COUNT', label: '筆數' },
    { value: 'BOOLEAN', label: '是/否' }
];

const formParam = ref({
    ruleCode : '',
    ruleName : '',
    managementMode : '',
    compareMode : '',
    periodType : '',
    dataType : '',
    recommendedFormulaOid : '',
    priorityNo : 100,
    isDefault : 'N',
    enabled : 'Y',
    description : ''
});

const btnBack = () => router.back();

const loadFormulaList = async () => {
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + '/MD_PROG002D0001/findList', {});
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            formulaList.value = (response.data.value || []).filter((item: any) => item.enabled === 'Y' && item.isRecommendable === 'Y');
        }
    } catch (e: any) {
        toast.warning(e?.message || e);
    }
};

const btnClear = () => {
    checkFields.value = {};
    formParam.value.ruleCode = '';
    formParam.value.ruleName = '';
    formParam.value.managementMode = '';
    formParam.value.compareMode = '';
    formParam.value.periodType = '';
    formParam.value.dataType = '';
    formParam.value.recommendedFormulaOid = '';
    formParam.value.priorityNo = 100;
    formParam.value.isDefault = 'N';
    formParam.value.enabled = 'Y';
    formParam.value.description = '';
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

onMounted(() => {
    loadFormulaList();
});
</script>

<template>
<div class="row">
    <div class="col-12">
        <Toolbar
            :progId="pageProgramId"
            description="公式推薦規則新增"
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
        <label for="ruleCode" class="form-label">規則代碼</label>
        <input type="text" :class="['form-control', checkInvalid('ruleCode', checkFields) ? 'is-invalid' : '']" id="ruleCode" v-model="formParam.ruleCode">
        <div v-if="checkInvalid('ruleCode', checkFields)" class="invalid-feedback">{{ invalidFeedback('ruleCode', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="ruleName" class="form-label">規則名稱</label>
        <input type="text" :class="['form-control', checkInvalid('ruleName', checkFields) ? 'is-invalid' : '']" id="ruleName" v-model="formParam.ruleName">
        <div v-if="checkInvalid('ruleName', checkFields)" class="invalid-feedback">{{ invalidFeedback('ruleName', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="priorityNo" class="form-label">優先序</label>
        <input type="number" min="1" :class="['form-control', checkInvalid('priorityNo', checkFields) ? 'is-invalid' : '']" id="priorityNo" v-model.number="formParam.priorityNo">
        <div v-if="checkInvalid('priorityNo', checkFields)" class="invalid-feedback">{{ invalidFeedback('priorityNo', checkFields) }}</div>
      </div>

      <div class="col-md-3">
        <label for="managementMode" class="form-label">管理模式</label>
        <select class="form-select" id="managementMode" v-model="formParam.managementMode">
          <option v-for="item in managementModeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
      </div>
      <div class="col-md-3">
        <label for="compareMode" class="form-label">比較模式</label>
        <select class="form-select" id="compareMode" v-model="formParam.compareMode">
          <option v-for="item in compareModeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
      </div>
      <div class="col-md-3">
        <label for="periodType" class="form-label">期間</label>
        <select class="form-select" id="periodType" v-model="formParam.periodType">
          <option v-for="item in periodTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
      </div>
      <div class="col-md-3">
        <label for="dataType" class="form-label">資料型態</label>
        <select class="form-select" id="dataType" v-model="formParam.dataType">
          <option v-for="item in dataTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
      </div>

      <div class="col-md-6">
        <label for="recommendedFormulaOid" class="form-label">推薦公式</label>
        <select :class="['form-select', checkInvalid('recommendedFormulaOid', checkFields) ? 'is-invalid' : '']" id="recommendedFormulaOid" v-model="formParam.recommendedFormulaOid">
          <option value="">請選擇</option>
          <option v-for="item in formulaList" :key="item.oid" :value="item.oid">{{ item.formulaCode }} - {{ item.formulaName }}</option>
        </select>
        <div v-if="checkInvalid('recommendedFormulaOid', checkFields)" class="invalid-feedback">{{ invalidFeedback('recommendedFormulaOid', checkFields) }}</div>
      </div>
      <div class="col-md-3">
        <label for="isDefault" class="form-label">預設規則</label>
        <select :class="['form-select', checkInvalid('isDefault', checkFields) ? 'is-invalid' : '']" id="isDefault" v-model="formParam.isDefault">
          <option value="Y">是</option>
          <option value="N">否</option>
        </select>
        <div v-if="checkInvalid('isDefault', checkFields)" class="invalid-feedback">{{ invalidFeedback('isDefault', checkFields) }}</div>
      </div>
      <div class="col-md-3">
        <label for="enabled" class="form-label">啟用</label>
        <select :class="['form-select', checkInvalid('enabled', checkFields) ? 'is-invalid' : '']" id="enabled" v-model="formParam.enabled">
          <option value="Y">是</option>
          <option value="N">否</option>
        </select>
        <div v-if="checkInvalid('enabled', checkFields)" class="invalid-feedback">{{ invalidFeedback('enabled', checkFields) }}</div>
      </div>

      <div class="col-md-12">
        <label for="description" class="form-label">說明</label>
        <textarea class="form-control" id="description" rows="3" v-model="formParam.description"></textarea>
      </div>
    </div>
    <div class="mt-4 d-flex gap-2">
      <button type="button" class="btn btn-primary" @click="btnSave"><i class="bi bi-save"></i> 儲存</button>
      <button type="button" class="btn btn-outline-secondary" @click="btnClear"><i class="bi bi-eraser"></i> 清除</button>
    </div>
  </div>
</div>
</template>
