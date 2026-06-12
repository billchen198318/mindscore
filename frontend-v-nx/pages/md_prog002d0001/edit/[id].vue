<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
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
const isBuiltin = computed(() => formParam.value.formulaType === 'BUILTIN');
const formulaTypeDisplay = computed(() => formParam.value.formulaType === 'BUILTIN' ? '系統提供' : '使用者自訂');

const formParam = ref({
    oid : route.params.id as string,
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

const btnClear = () => {
    if (isBuiltin.value) {
        return;
    }
    checkFields.value = {};
    formParam.value.formulaName = '';
    formParam.value.expression = '';
    formParam.value.description = '';
    formParam.value.exampleText = '';
    formParam.value.paramSchemaJson = '';
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
            formParam.value = response.data.value;
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
    if (isBuiltin.value) {
        toast.warning('BUILTIN公式為系統內建資料，不能由維護畫面修改。');
        return;
    }
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
            description="Formula修改"
            refreshFlag="Y"
            @refreshMethod="loadData"
            backFlag="Y"
            @backMethod="btnBack"
            :saveFlag="isBuiltin ? 'N' : 'Y'"
            @saveMethod="btnUpdate"
        />
    </div>
</div>

<div class="card mb-4">
  <div class="card-body">
    <div class="row g-3">
      <div class="col-md-4">
        <label for="formulaCode" class="form-label">Formula代碼</label>
        <input type="text" class="form-control" id="formulaCode" v-model="formParam.formulaCode" readonly>
      </div>
      <div class="col-md-4">
        <label for="formulaName" class="form-label">Formula名稱</label>
        <input type="text" :class="['form-control', checkInvalid('formulaName', checkFields) ? 'is-invalid' : '']" id="formulaName" v-model="formParam.formulaName" :readonly="isBuiltin">
        <div v-if="checkInvalid('formulaName', checkFields)" class="invalid-feedback">{{ invalidFeedback('formulaName', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="versionNo" class="form-label">版本號</label>
        <input type="number" min="1" :class="['form-control', checkInvalid('versionNo', checkFields) ? 'is-invalid' : '']" id="versionNo" v-model.number="formParam.versionNo" :readonly="isBuiltin">
        <div v-if="checkInvalid('versionNo', checkFields)" class="invalid-feedback">{{ invalidFeedback('versionNo', checkFields) }}</div>
      </div>

      <div class="col-md-3">
        <label for="formulaType" class="form-label">公式來源類型</label>
        <input type="text" class="form-control" id="formulaType" :value="formulaTypeDisplay" readonly>
        <div class="form-text">公式來源類型由建立來源決定，不能在維護畫面切換。</div>
        <div v-if="checkInvalid('formulaType', checkFields)" class="invalid-feedback">{{ invalidFeedback('formulaType', checkFields) }}</div>
      </div>
      <div class="col-md-3">
        <label for="scriptType" class="form-label">Script類型</label>
        <select :class="['form-select', checkInvalid('scriptType', checkFields) ? 'is-invalid' : '']" id="scriptType" v-model="formParam.scriptType" :disabled="isBuiltin">
          <option value="GROOVY">GROOVY</option>
        </select>
        <div v-if="checkInvalid('scriptType', checkFields)" class="invalid-feedback">{{ invalidFeedback('scriptType', checkFields) }}</div>
      </div>
      <div class="col-md-3">
        <label for="returnType" class="form-label">回傳類型</label>
        <select :class="['form-select', checkInvalid('returnType', checkFields) ? 'is-invalid' : '']" id="returnType" v-model="formParam.returnType" :disabled="isBuiltin">
          <option value="DECIMAL">DECIMAL</option>
          <option value="INTEGER">INTEGER</option>
          <option value="BOOLEAN">BOOLEAN</option>
          <option value="TEXT">TEXT</option>
        </select>
        <div v-if="checkInvalid('returnType', checkFields)" class="invalid-feedback">{{ invalidFeedback('returnType', checkFields) }}</div>
      </div>
      <div class="col-md-3">
        <label for="enabled" class="form-label">啟用</label>
        <select :class="['form-select', checkInvalid('enabled', checkFields) ? 'is-invalid' : '']" id="enabled" v-model="formParam.enabled" :disabled="isBuiltin">
          <option value="Y">是</option>
          <option value="N">否</option>
        </select>
        <div v-if="checkInvalid('enabled', checkFields)" class="invalid-feedback">{{ invalidFeedback('enabled', checkFields) }}</div>
      </div>

      <div class="col-md-3">
        <label for="isSystem" class="form-label">系統公式</label>
        <select :class="['form-select', checkInvalid('isSystem', checkFields) ? 'is-invalid' : '']" id="isSystem" v-model="formParam.isSystem" :disabled="isBuiltin">
          <option value="Y">是</option>
          <option value="N">否</option>
        </select>
        <div v-if="checkInvalid('isSystem', checkFields)" class="invalid-feedback">{{ invalidFeedback('isSystem', checkFields) }}</div>
      </div>
      <div class="col-md-3">
        <label for="isRecommendable" class="form-label">可推薦</label>
        <select :class="['form-select', checkInvalid('isRecommendable', checkFields) ? 'is-invalid' : '']" id="isRecommendable" v-model="formParam.isRecommendable" :disabled="isBuiltin">
          <option value="Y">是</option>
          <option value="N">否</option>
        </select>
        <div v-if="checkInvalid('isRecommendable', checkFields)" class="invalid-feedback">{{ invalidFeedback('isRecommendable', checkFields) }}</div>
      </div>

      <div class="col-md-12">
        <label for="expression" class="form-label">Expression</label>
        <textarea class="form-control" id="expression" rows="5" v-model="formParam.expression" :readonly="isBuiltin"></textarea>
      </div>
      <div class="col-md-12">
        <label for="paramSchemaJson" class="form-label">參數規格 JSON</label>
        <textarea class="form-control" id="paramSchemaJson" rows="4" v-model="formParam.paramSchemaJson" :readonly="isBuiltin"></textarea>
        <div class="form-text">描述公式需要的輸入參數，供未來公式測試、KPI綁定與執行前驗證使用；不是公式內容，公式請填在 Expression。</div>
      </div>
      <div class="col-md-12">
        <label for="exampleText" class="form-label">範例說明</label>
        <textarea class="form-control" id="exampleText" rows="3" v-model="formParam.exampleText" :readonly="isBuiltin"></textarea>
      </div>
      <div class="col-md-12">
        <label for="description" class="form-label">說明</label>
        <textarea class="form-control" id="description" rows="3" v-model="formParam.description" :readonly="isBuiltin"></textarea>
      </div>
    </div>
    <div class="mt-4 d-flex gap-2">
      <button type="button" class="btn btn-primary" @click="btnUpdate" :disabled="isBuiltin"><i class="bi bi-save"></i> 儲存</button>
      <button type="button" class="btn btn-outline-secondary" @click="btnClear" :disabled="isBuiltin"><i class="bi bi-eraser"></i> 清除</button>
    </div>
  </div>
</div>
</template>
