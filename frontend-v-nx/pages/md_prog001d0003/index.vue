<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { toast } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

import Toolbar from '@/components/Toolbar.vue';
import OrgTree from './OrgTree.vue';
import { PageConstants } from './config';
import { useMdProg001d0003Store } from './QueryPageStore'; 
import { getAxiosInstance } from '../../components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';

definePageMeta({ middleware: ['auth'] });

const queryPageStore = useMdProg001d0003Store();
const { showLoading, hideLoading } = useSwalLoading();
const pageProgramId = ref(PageConstants.QueryId);

const loadTreeData = async () => {
    showLoading();
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findTree', {});
        hideLoading();
        if (response.data && response.data.success == import.meta.env.VITE_SUCCESS_FLAG) {
            queryPageStore.setTreeData(response.data.value);
        } else {
            toast.warning(response.data.message);
        }
    } catch (e: any) {
        hideLoading();
        toast.error('無法載入組織樹');
    }
};

onMounted(() => {
    loadTreeData();
});
</script>

<template>
<div class="row">
  <div class="col-12">
    <Toolbar 
        :progId="pageProgramId" 
        description="組織架構重組 (拖拉調整父子關係)" 
        refreshFlag="Y"
        @refreshMethod="loadTreeData"
    />
  </div>
</div>

<div class="card mb-4">
  <div class="card-body">
    <OrgTree v-model="queryPageStore.treeData" @refresh="loadTreeData" />
  </div>
</div>
</template>