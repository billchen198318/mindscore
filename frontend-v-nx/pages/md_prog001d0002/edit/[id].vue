<script setup lang="ts">
import { ref, onMounted } from 'vue';
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
	escapeQifuHtmlMsg
} from '../../../components/BaseHelper';

definePageMeta({ middleware: ['auth'] });

const router = useRouter();
const route = useRoute();
const { showLoading, hideLoading } = useSwalLoading();
const checkFields = ref<any>({});
const orgList = ref<any[]>([]);

const formParam = ref({
    oid : '',
	orgOid : '',
	account : '',
	displayName : '',
	jobTitle : '',
	enabled : 'Y'
});

const pageProgramId = ref(PageConstants.EditId);

const btnBack = () => router.back();

const btnClear = () => {
    // 編輯時，清除通常是恢復成原本載入的值，這裡簡單實作清空欄位
	checkFields.value = {};
	formParam.value.displayName = '';
	formParam.value.jobTitle = '';
};

const loadOrgList = async () => {
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + '/MD_PROG001D0001/findList', {});
        if (response.data && response.data.success == import.meta.env.VITE_SUCCESS_FLAG) {
            orgList.value = response.data.value;
        }
    } catch (e: any) {
        toast.error('無法載入組織列表');
    }
};

const loadData = async () => {
    showLoading();
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/load', { "oid": route.params.id });
        hideLoading();
        if (response.data && response.data.success == import.meta.env.VITE_SUCCESS_FLAG) {
            formParam.value = response.data.value;
        } else {
            toast.error(response.data.message);
        }
    } catch (e: any) {
        hideLoading();
        alert(e);
    }
};

const btnSave = async () => {
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
            // 修改完不導向，保持在當前編輯頁
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
    loadData();
});
</script>

<template>
<div class="row">
	<div class="col-12">
		<Toolbar 
			:progId="pageProgramId" 
        	description="組織成員維護，修改資料作業." 
        	refreshFlag="Y"
            @refreshMethod="loadData"
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
        <select 
            :class="['form-select', checkInvalid('orgOid', checkFields) ? 'is-invalid' : '']" 
            id="orgOid" 
            v-model="formParam.orgOid"
        >
            <option value="">請選擇</option>
            <option v-for="org in orgList" :key="org.oid" :value="org.oid">{{ org.orgName }}</option>
        </select>
        <div v-if="checkInvalid('orgOid', checkFields)" class="invalid-feedback">{{ invalidFeedback('orgOid', checkFields) }}</div>
      </div>
      <div class="col-md-6">
        <label for="account" class="form-label">帳號 (不可修改)</label>
        <input 
          type="text" 
          class="form-control" 
          id="account" 
          v-model="formParam.account"
          readonly
        >
      </div>
      <div class="col-md-6">
        <label for="displayName" class="form-label">名稱</label>
        <input 
          type="text" 
          :class="['form-control', checkInvalid('displayName', checkFields) ? 'is-invalid' : '']" 
          id="displayName" 
          placeholder="輸入名稱" 
          v-model="formParam.displayName"
        >
        <div v-if="checkInvalid('displayName', checkFields)" class="invalid-feedback">{{ invalidFeedback('displayName', checkFields) }}</div>
      </div>
      <div class="col-md-6">
        <label for="jobTitle" class="form-label">職稱</label>
        <input 
          type="text" 
          class="form-control" 
          id="jobTitle" 
          placeholder="輸入職稱" 
          v-model="formParam.jobTitle"
        >
      </div>
    </div>
    <div class="mt-4 d-flex gap-2">
      <button type="button" class="btn btn-primary" @click="btnSave"><i class="bi bi-save"></i> 儲存</button>
      <button type="button" class="btn btn-outline-secondary" @click="btnClear"><i class="bi bi-eraser"></i> 清除</button>		
    </div>
  </div>
</div>
</template>
