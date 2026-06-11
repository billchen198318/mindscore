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
const formParam = ref({
	oid : route.params.id as string,
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
	formParam.value.orgName = '';
	formParam.value.description = '';
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
        	description="組織單位維護，修改資料作業." 
        	refreshFlag="Y"
        	@refreshMethod="loadData"
        	backFlag="Y"
        	@backMethod="btnBack"
        	saveFlag="Y"
        	@saveMethod="btnUpdate"
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
          class="form-control" 
          id="orgCode" 
          v-model="formParam.orgCode" 
          readonly
        >
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
      <button type="button" class="btn btn-primary" @click="btnUpdate"><i class="bi bi-save"></i> 儲存</button>
      <button type="button" class="btn btn-outline-secondary" @click="btnClear"><i class="bi bi-eraser"></i> 清除</button>		
    </div>
  </div>
</div>
</template>
