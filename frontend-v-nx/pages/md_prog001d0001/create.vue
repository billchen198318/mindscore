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
	orgCode : '',
	orgName : '',
	parentOid : '',
	orgLevel : 1,
	sortNo : 0,
	enabled : 'Y',
	description : ''
});

const btnBack = () => router.back();

const btnClear = () => {
	checkFields.value = {};
	formParam.value.orgCode = '';
	formParam.value.orgName = '';
	formParam.value.parentOid = '';
	formParam.value.orgLevel = 1;
	formParam.value.sortNo = 0;
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
</script>

<template>
<div class="row">
	<div class="col-12">
		<Toolbar 
			:progId="pageProgramId" 
        	description="組織單位維護，新增資料作業." 
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
        <label for="orgCode" class="form-label">組織代碼</label>
        <input 
          type="text" 
          :class="['form-control', checkInvalid('orgCode', checkFields) ? 'is-invalid' : '']" 
          id="orgCode" 
          placeholder="輸入組織代碼" 
          v-model="formParam.orgCode"
        >
        <div v-if="checkInvalid('orgCode', checkFields)" class="invalid-feedback">{{ invalidFeedback('orgCode', checkFields) }}</div>
      </div>
      <div class="col-md-6">
        <label for="orgName" class="form-label">組織名稱</label>
        <input 
          type="text" 
          :class="['form-control', checkInvalid('orgName', checkFields) ? 'is-invalid' : '']" 
          id="orgName" 
          placeholder="輸入組織名稱" 
          v-model="formParam.orgName"
        >
        <div v-if="checkInvalid('orgName', checkFields)" class="invalid-feedback">{{ invalidFeedback('orgName', checkFields) }}</div>
      </div>
      <div class="col-md-12">
        <label for="description" class="form-label">說明</label>
        <textarea 
          class="form-control" 
          id="description" 
          placeholder="輸入說明" 
          v-model="formParam.description"
        ></textarea>
      </div>
    </div>
    <div class="mt-4 d-flex gap-2">
      <button type="button" class="btn btn-primary" @click="btnSave"><i class="bi bi-save"></i> 儲存</button>
      <button type="button" class="btn btn-outline-secondary" @click="btnClear"><i class="bi bi-eraser"></i> 清除</button>		
    </div>
  </div>
</div>
</template>
