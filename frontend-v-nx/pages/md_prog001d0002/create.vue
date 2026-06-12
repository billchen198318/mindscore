<script setup lang="ts">
import { ref, onMounted } from 'vue';
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
const { showLoading, hideLoading } = useSwalLoading();
const checkFields = ref<any>({});
const orgList = ref<any[]>([]);
const pleaseSelectId = import.meta.env.VITE_PLEASE_SELECT_ID;
const pleaseSelectLabel = import.meta.env.VITE_PLEASE_SELECT_LABEL;

const formParam = ref({
    orgOid: pleaseSelectId,
    account: '',
    displayName: '',
    employeeId: '',
    email: '',
    jobTitle: '',
    isManager: 'N',
    enabled: 'Y'
});

const btnBack = () => router.back();

const btnClear = () => {
    checkFields.value = {};
    formParam.value.orgOid = pleaseSelectId;
    formParam.value.account = '';
    formParam.value.displayName = '';
    formParam.value.employeeId = '';
    formParam.value.email = '';
    formParam.value.jobTitle = '';
    formParam.value.isManager = 'N';
    formParam.value.enabled = 'Y';
};

const loadOrgList = async () => {
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + '/MD_PROG001D0001/findList', {});
        if (response.data && response.data.success == import.meta.env.VITE_SUCCESS_FLAG) {
            orgList.value = response.data.value || [];
        }
    } catch (e: any) {
        toast.error('組織資料載入失敗');
    }
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
    loadOrgList();
});
</script>

<template>
<div class="row">
  <div class="col-12">
    <Toolbar
      :progId="pageProgramId"
      description="組織成員維護，新增資料作業."
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
      <div class="col-md-6">
        <label for="orgOid" class="form-label">組織</label>
        <select :class="['form-select', checkInvalid('orgOid', checkFields) ? 'is-invalid' : '']" id="orgOid" v-model="formParam.orgOid">
          <option :value="pleaseSelectId">{{ pleaseSelectLabel }}</option>
          <option v-for="org in orgList" :key="org.oid" :value="org.oid">{{ org.orgName }}</option>
        </select>
        <div v-if="checkInvalid('orgOid', checkFields)" class="invalid-feedback">{{ invalidFeedback('orgOid', checkFields) }}</div>
      </div>
      <div class="col-md-6">
        <label for="account" class="form-label">帳號</label>
        <input type="text" :class="['form-control', checkInvalid('account', checkFields) ? 'is-invalid' : '']" id="account" v-model="formParam.account" placeholder="輸入帳號">
        <div v-if="checkInvalid('account', checkFields)" class="invalid-feedback">{{ invalidFeedback('account', checkFields) }}</div>
      </div>
      <div class="col-md-6">
        <label for="displayName" class="form-label">名稱</label>
        <input type="text" :class="['form-control', checkInvalid('displayName', checkFields) ? 'is-invalid' : '']" id="displayName" v-model="formParam.displayName" placeholder="輸入名稱">
        <div v-if="checkInvalid('displayName', checkFields)" class="invalid-feedback">{{ invalidFeedback('displayName', checkFields) }}</div>
      </div>
      <div class="col-md-6">
        <label for="employeeId" class="form-label">員工編號</label>
        <input type="text" :class="['form-control', checkInvalid('employeeId', checkFields) ? 'is-invalid' : '']" id="employeeId" v-model="formParam.employeeId" placeholder="輸入員工編號">
        <div v-if="checkInvalid('employeeId', checkFields)" class="invalid-feedback">{{ invalidFeedback('employeeId', checkFields) }}</div>
      </div>
      <div class="col-md-6">
        <label for="email" class="form-label">Email</label>
        <input type="text" :class="['form-control', checkInvalid('email', checkFields) ? 'is-invalid' : '']" id="email" v-model="formParam.email" placeholder="輸入Email">
        <div v-if="checkInvalid('email', checkFields)" class="invalid-feedback">{{ invalidFeedback('email', checkFields) }}</div>
      </div>
      <div class="col-md-6">
        <label for="jobTitle" class="form-label">職稱</label>
        <input type="text" class="form-control" id="jobTitle" v-model="formParam.jobTitle" placeholder="輸入職稱">
      </div>
      <div class="col-md-6">
        <label for="isManager" class="form-label">是否主管</label>
        <select class="form-select" id="isManager" v-model="formParam.isManager">
          <option value="Y">是</option>
          <option value="N">否</option>
        </select>
      </div>
    </div>
    <div class="mt-4 d-flex gap-2">
      <button type="button" class="btn btn-primary" @click="btnSave"><i class="bi bi-save"></i> 儲存</button>
      <button type="button" class="btn btn-outline-secondary" @click="btnClear"><i class="bi bi-eraser"></i> 清除</button>
    </div>
  </div>
</div>
</template>
