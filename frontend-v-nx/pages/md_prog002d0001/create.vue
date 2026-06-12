<script setup lang="ts">
import { computed, nextTick, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useSwalLoading } from '@/composables/useSwalLoading';
import { toast } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

import Toolbar from '@/components/Toolbar.vue';
import FormulaInputPad from './FormulaInputPad.vue';
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
const expressionTextarea = ref<HTMLTextAreaElement | null>(null);
const { showLoading, hideLoading } = useSwalLoading();
const formulaTypeDisplay = computed(() => formParam.value.formulaType === 'BUILTIN' ? '系統提供' : '使用者自訂');

const formParam = ref({
    formulaCode : '',
    formulaName : '',
    formulaType : 'CUSTOM',
    scriptType : 'GROOVY',
    expression : '',
    returnType : 'DECIMAL',
    versionNo : 1,
    isSystem : 'N',
    isRecommendable : 'Y',
    enabled : 'Y',
    description : '',
    exampleText : '',
    paramSchemaJson : ''
});

const btnBack = () => router.back();

const insertExpressionValue = async (value: string) => {
    const textarea = expressionTextarea.value;
    if (!textarea) {
        formParam.value.expression = formParam.value.expression + value;
        return;
    }
    const start = textarea.selectionStart ?? formParam.value.expression.length;
    const end = textarea.selectionEnd ?? formParam.value.expression.length;
    formParam.value.expression = formParam.value.expression.substring(0, start) + value + formParam.value.expression.substring(end);
    await nextTick();
    textarea.focus();
    textarea.setSelectionRange(start + value.length, start + value.length);
};

const clearExpressionValue = () => {
    formParam.value.expression = '';
};

const btnTestFormula = async (testValues: any) => {
    checkFields.value = {};
    showLoading();
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/test', {
            scriptType : formParam.value.scriptType,
            expression : formParam.value.expression,
            ...testValues
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
    formParam.value.formulaCode = '';
    formParam.value.formulaName = '';
    formParam.value.formulaType = 'CUSTOM';
    formParam.value.scriptType = 'GROOVY';
    formParam.value.expression = '';
    formParam.value.returnType = 'DECIMAL';
    formParam.value.versionNo = 1;
    formParam.value.isSystem = 'N';
    formParam.value.isRecommendable = 'Y';
    formParam.value.enabled = 'Y';
    formParam.value.description = '';
    formParam.value.exampleText = '';
    formParam.value.paramSchemaJson = '';
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
            description="Formula新增"
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
        <label for="formulaCode" class="form-label">Formula代碼</label>
        <input type="text" :class="['form-control', checkInvalid('formulaCode', checkFields) ? 'is-invalid' : '']" id="formulaCode" v-model="formParam.formulaCode">
        <div v-if="checkInvalid('formulaCode', checkFields)" class="invalid-feedback">{{ invalidFeedback('formulaCode', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="formulaName" class="form-label">Formula名稱</label>
        <input type="text" :class="['form-control', checkInvalid('formulaName', checkFields) ? 'is-invalid' : '']" id="formulaName" v-model="formParam.formulaName">
        <div v-if="checkInvalid('formulaName', checkFields)" class="invalid-feedback">{{ invalidFeedback('formulaName', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="versionNo" class="form-label">版本號</label>
        <input type="number" min="1" :class="['form-control', checkInvalid('versionNo', checkFields) ? 'is-invalid' : '']" id="versionNo" v-model.number="formParam.versionNo">
        <div v-if="checkInvalid('versionNo', checkFields)" class="invalid-feedback">{{ invalidFeedback('versionNo', checkFields) }}</div>
      </div>

      <div class="col-md-3">
        <label for="formulaType" class="form-label">公式來源類型</label>
        <input type="text" class="form-control" id="formulaType" :value="formulaTypeDisplay" readonly>
        <div class="form-text">新增公式一律為 CUSTOM；BUILTIN 只能由系統資料建立。</div>
      </div>
      <div class="col-md-3">
        <label for="scriptType" class="form-label">Script類型</label>
        <select :class="['form-select', checkInvalid('scriptType', checkFields) ? 'is-invalid' : '']" id="scriptType" v-model="formParam.scriptType">
          <option value="GROOVY">GROOVY</option>
        </select>
        <div v-if="checkInvalid('scriptType', checkFields)" class="invalid-feedback">{{ invalidFeedback('scriptType', checkFields) }}</div>
      </div>
      <div class="col-md-3">
        <label for="returnType" class="form-label">回傳類型</label>
        <select :class="['form-select', checkInvalid('returnType', checkFields) ? 'is-invalid' : '']" id="returnType" v-model="formParam.returnType">
          <option value="DECIMAL">DECIMAL</option>
        </select>
        <div v-if="checkInvalid('returnType', checkFields)" class="invalid-feedback">{{ invalidFeedback('returnType', checkFields) }}</div>
      </div>
      <div class="col-md-3">
        <label for="enabled" class="form-label">啟用</label>
        <select :class="['form-select', checkInvalid('enabled', checkFields) ? 'is-invalid' : '']" id="enabled" v-model="formParam.enabled">
          <option value="Y">是</option>
          <option value="N">否</option>
        </select>
        <div v-if="checkInvalid('enabled', checkFields)" class="invalid-feedback">{{ invalidFeedback('enabled', checkFields) }}</div>
      </div>

      <div class="col-md-3">
        <label for="isRecommendable" class="form-label">可推薦</label>
        <select :class="['form-select', checkInvalid('isRecommendable', checkFields) ? 'is-invalid' : '']" id="isRecommendable" v-model="formParam.isRecommendable">
          <option value="Y">是</option>
          <option value="N">否</option>
        </select>
        <div v-if="checkInvalid('isRecommendable', checkFields)" class="invalid-feedback">{{ invalidFeedback('isRecommendable', checkFields) }}</div>
      </div>

      <div class="col-md-12">
        <label for="expression" class="form-label">Expression</label>
        <textarea ref="expressionTextarea" class="form-control" id="expression" rows="5" v-model="formParam.expression"></textarea>
      </div>
      <div class="col-md-12">
        <FormulaInputPad @insert="insertExpressionValue" @clear="clearExpressionValue" @test="btnTestFormula" />
      </div>
      <div class="col-md-12">
        <label for="paramSchemaJson" class="form-label">參數規格 JSON</label>
        <textarea class="form-control" id="paramSchemaJson" rows="4" v-model="formParam.paramSchemaJson"></textarea>
        <div class="form-text">描述公式需要的輸入參數，供未來公式測試、KPI綁定與執行前驗證使用；不是公式內容，公式請填在 Expression。</div>
      </div>
      <div class="col-md-12">
        <label for="exampleText" class="form-label">範例說明</label>
        <textarea class="form-control" id="exampleText" rows="3" v-model="formParam.exampleText"></textarea>
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
