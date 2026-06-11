<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { toast } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

import Toolbar from '@/components/Toolbar.vue';
import Grid from '@/components/Grid.vue';
import GridPagination from '@/components/GridPagination.vue';
import { PageConstants } from './config';
import { getGridConfig, setConfigRow, setConfigPage, setConfigTotal, resetConfigByOld } from '../../components/GridHelper';
import { useMdProg001d0002Store } from './QueryPageStore'; 
import { 
	getAxiosInstance, 
	getProgItem, 
	getUrlPrefixFromProgItem 
} from '../../components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';

definePageMeta({ middleware: ['auth'] });

const router = useRouter();
const queryPageStore = useMdProg001d0002Store();
const { showLoading, hideLoading, confirmFire } = useSwalLoading();

const pageProgramId = ref(PageConstants.QueryId);
const dsList = ref<any[]>([]);

const tbRefresh = () => btnClear();
const tbCreate = () => router.push(PageConstants.frontendNamespace + '/create');

const btnClear = () => {
	queryPageStore.clearData();
	dsList.value = [];
	clearGridConfig();
};

const changeQueryGridRow = (row: number) => {
	setConfigRow(queryPageStore.gridConfig, row);
	queryPageStore.gridConfig.page = 1;
	btnQuery();
};

const changePageSelect = (page: number) => {
	setConfigPage(queryPageStore.gridConfig, page);
	btnQuery();
};

const clearGridConfig = () => {
	setConfigRow(queryPageStore.gridConfig, import.meta.env.VITE_DEFAULT_ROW);
	setConfigPage(queryPageStore.gridConfig, 1);
	setConfigTotal(queryPageStore.gridConfig, 0);
};

const initQueryGridConfig = () => {
  	return getGridConfig(
		'oid',
		[
			{
				'method'  : (val: any) => { 
					const url = getUrlPrefixFromProgItem(getProgItem(PageConstants.EditId)) + '/' + val;
					router.push(url);
				},
				'icon'    : 'pen',
				'type'    : 'edit',
				'memo'    : 'Edit current item.',
				'class'	  : 'btn btn-info btn-sm'
			},
			{
				'method'  : (val: any) => { 
					confirmFire('刪除?', delItem, val);           
				},
				'icon'    : 'trash',
				'type'    : 'delete',
				'memo'    : 'Delete current item.',
				'class'	  : 'btn btn-danger btn-sm'
			}     
		],
		[
			{ label: '<i class="bi bi-hand-index-thumb"></i>', field: 'oid', labHtml: true },
			{ label: '帳號', field: 'account' },
			{ label: '名稱', field: 'displayName' },
			{ label: '職稱', field: 'jobTitle' }
		]    
	);
};

const btnQuery = async () => {
	showLoading();
	dsList.value = [];
	try {
		const axiosInstance = getAxiosInstance();
		const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findPage', {
			"field": {
				"accountLike"     	: queryPageStore.queryParam.account,
				"displayNameLike" 	: queryPageStore.queryParam.displayName
			},
			"pageOf": {
				"select"  : queryPageStore.gridConfig.page,
				"showRow" : queryPageStore.gridConfig.row
			}
		});
		hideLoading();
		if (response.data) {
			if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
				clearGridConfig();
				toast.warning(response.data.message);
				return;
			}
			dsList.value = response.data.value;
			setConfigTotal(queryPageStore.gridConfig, response.data.pageOf.countSize);
		} else {
			toast.error('error, null');
			clearGridConfig();
		}
	} catch (e: any) {
		hideLoading();    
		clearGridConfig();
		alert(e);
	}
};

const delItem = async (oid: string) => {
	showLoading();  
	try {
		const axiosInstance = getAxiosInstance();  
		const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/delete', { "oid": oid });
		hideLoading();
		if (response.data) {
			if (import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
				toast.success(response.data.message);
			} else {        
				toast.warning(response.data.message);
			} 
			btnQuery();
		} else {
			toast.error('error, null');
			clearGridConfig();
		}
	} catch (e: any) {
		hideLoading();    
		btnQuery();
		alert(e);
	}
};

onMounted(() => {
	const newGridConfig = initQueryGridConfig();
	if (queryPageStore.gridConfig.column) {
		resetConfigByOld(newGridConfig, queryPageStore.gridConfig);
	}
	queryPageStore.gridConfig = newGridConfig;
	
	if (queryPageStore.gridConfig.total > 0) {
		btnQuery();
	}
});
</script>

<template>
<div class="row">
  <div class="col-12">
    <Toolbar 
        :progId="pageProgramId" 
        description="組織成員維護" 
        refreshFlag="Y"
        @refreshMethod="tbRefresh"
        createFlag="Y"
        @createMethod="tbCreate"
    />
  </div>
</div>

<div class="row">
    <div class="col-12">
        <div class="card mb-4">
          <div class="card-body">
            <div class="row g-3">
              <div class="col-md-6">
                <div class="form-group form-floating">
                  <input type="text" class="form-control" id="account" placeholder="帳號" v-model="queryPageStore.queryParam.account">
                  <label for="account">帳號</label>
                </div>
              </div>
              <div class="col-md-6">
                <div class="form-group form-floating">
                  <input type="text" class="form-control" id="displayName" placeholder="名稱" v-model="queryPageStore.queryParam.displayName">
                  <label for="displayName">名稱</label>
                </div>
              </div>
            </div>
            <div class="row mt-3">
              <div class="col-12 d-flex gap-2">
                <button type="button" class="btn btn-primary" @click="btnQuery"><i class="bi bi-search"></i> 查詢</button>
                <button type="button" class="btn btn-outline-secondary" @click="btnClear"><i class="bi bi-eraser"></i> 清除</button>
              </div>
            </div>
          </div>
        </div>

        <GridPagination 
            :progId="pageProgramId" 
            :gridConfig="queryPageStore.gridConfig" 
            :changePageSelectMethod="changePageSelect" 
            :changeGridConfigRowMethod="changeQueryGridRow" 
        />
        <Grid :progId="pageProgramId" :dataSource="dsList" :config="queryPageStore.gridConfig" />
    </div>
</div>
</template>
