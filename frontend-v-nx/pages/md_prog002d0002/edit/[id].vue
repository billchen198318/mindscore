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
const isBuiltin = computed(() => formParam.value.aggrType === 'BUILTIN');
const aggrTypeDisplay = computed(() => formParam.value.aggrType === 'BUILTIN' ? '系統提供' : '使用者自訂');

const formParam = ref({
    oid : route.params.id as string,
    aggrCode : '',
    aggrName : '',
    aggrType : 'CUSTOM',
    expression : '',
    description : '',
    enabled : 'Y'
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
            formParam.value = { ...response.data.value };
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
        toast.warning('BUILTIN彙總方法為系統內建資料，不能由維護畫面修改。');
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
            description="彙總方法修改"
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
        <label for="aggrCode" class="form-label">彙總代碼</label>
        <input type="text" class="form-control" id="aggrCode" v-model="formParam.aggrCode" readonly>
      </div>
      <div class="col-md-4">
        <label for="aggrName" class="form-label">彙總名稱</label>
        <input type="text" :class="['form-control', checkInvalid('aggrName', checkFields) ? 'is-invalid' : '']" id="aggrName" v-model="formParam.aggrName" :readonly="isBuiltin">
        <div v-if="checkInvalid('aggrName', checkFields)" class="invalid-feedback">{{ invalidFeedback('aggrName', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="aggrType" class="form-label">彙總來源類型</label>
        <input type="text" class="form-control" id="aggrType" :value="aggrTypeDisplay" readonly>
        <div class="form-text">彙總來源類型由建立決定，不能在維護畫面切換。</div>
      </div>

      <div class="col-md-12">
        <label for="enabled" class="form-label">啟用</label>
        <select :class="['form-select', checkInvalid('enabled', checkFields) ? 'is-invalid' : '']" id="enabled" v-model="formParam.enabled" :disabled="isBuiltin">
          <option value="Y">是</option>
          <option value="N">否</option>
        </select>
        <div v-if="checkInvalid('enabled', checkFields)" class="invalid-feedback">{{ invalidFeedback('enabled', checkFields) }}</div>
      </div>
      
      <div class="col-md-12">
        <label for="expression" class="form-label">彙總公式或腳本 (Expression)</label>
        <textarea class="form-control" id="expression" rows="5" v-model="formParam.expression" :readonly="isBuiltin"></textarea>
      </div>

      <div class="col-md-12">
        <label for="description" class="form-label">說明</label>
        <textarea class="form-control" id="description" rows="3" v-model="formParam.description" :readonly="isBuiltin"></textarea>
      </div>
    </div>
    <div class="mt-4 d-flex gap-2">
      <button type="button" class="btn btn-primary" @click="btnUpdate" :disabled="isBuiltin"><i class="bi bi-save"></i> 儲存</button>
    </div>
  </div>
</div>
</template>
