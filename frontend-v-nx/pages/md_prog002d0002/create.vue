<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useSwalLoading } from '@/composables/useSwalLoading';
import { toast } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

import Toolbar from '@/components/Toolbar.vue';
import AggregationMethodInputPad from './AggregationMethodInputPad.vue';
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
const aggrTypeDisplay = computed(() => formParam.value.aggrType === 'BUILTIN' ? '系統提供' : '使用者自訂');

const formParam = ref({
    aggrCode : '',
    aggrName : '',
    aggrType : 'CUSTOM',
    expression : '',
    description : '',
    enabled : 'Y'
});

const btnBack = () => router.back();

const selectAggregationMethod = (method: any) => {
    formParam.value.expression = method.expression;
    if (!formParam.value.aggrCode) {
        formParam.value.aggrCode = method.code;
    }
    if (!formParam.value.aggrName) {
        formParam.value.aggrName = method.label + ' Aggregation';
    }
};

const clearExpressionValue = () => {
    formParam.value.expression = '';
};

const btnTestAggregation = async (scores: number[]) => {
    checkFields.value = {};
    showLoading();
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/test', {
            aggrCode : formParam.value.aggrCode,
            aggrType : formParam.value.aggrType,
            expression : formParam.value.expression,
            scores : scores
        });
        hideLoading();
        if (response.data) {
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

const btnClear = () => {
    checkFields.value = {};
    formParam.value.aggrCode = '';
    formParam.value.aggrName = '';
    formParam.value.aggrType = 'CUSTOM';
    formParam.value.expression = '';
    formParam.value.description = '';
    formParam.value.enabled = 'Y';
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
            description="彙總方法新增"
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
        <label for="aggrCode" class="form-label">彙總代碼</label>
        <input type="text" :class="['form-control', checkInvalid('aggrCode', checkFields) ? 'is-invalid' : '']" id="aggrCode" v-model="formParam.aggrCode">
        <div v-if="checkInvalid('aggrCode', checkFields)" class="invalid-feedback">{{ invalidFeedback('aggrCode', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="aggrName" class="form-label">彙總名稱</label>
        <input type="text" :class="['form-control', checkInvalid('aggrName', checkFields) ? 'is-invalid' : '']" id="aggrName" v-model="formParam.aggrName">
        <div v-if="checkInvalid('aggrName', checkFields)" class="invalid-feedback">{{ invalidFeedback('aggrName', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="aggrType" class="form-label">彙總來源類型</label>
        <input type="text" class="form-control" id="aggrType" :value="aggrTypeDisplay" readonly>
        <div class="form-text">新增一律為 CUSTOM；BUILTIN 只能由系統資料建立。</div>
      </div>

      <div class="col-md-12">
        <label for="enabled" class="form-label">啟用</label>
        <select :class="['form-select', checkInvalid('enabled', checkFields) ? 'is-invalid' : '']" id="enabled" v-model="formParam.enabled">
          <option value="Y">是</option>
          <option value="N">否</option>
        </select>
        <div v-if="checkInvalid('enabled', checkFields)" class="invalid-feedback">{{ invalidFeedback('enabled', checkFields) }}</div>
      </div>
      
      <div class="col-md-12">
        <label for="expression" class="form-label">彙總公式或腳本 (Expression)</label>
        <textarea class="form-control" id="expression" rows="5" v-model="formParam.expression"></textarea>
      </div>
      <div class="col-md-12">
        <AggregationMethodInputPad
            @select="selectAggregationMethod"
            @clear="clearExpressionValue"
            @test="btnTestAggregation"
        />
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
