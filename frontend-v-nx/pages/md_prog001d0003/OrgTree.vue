<script setup lang="ts">
import draggable from 'vuedraggable';
import { getAxiosInstance } from '@/components/BaseHelper';
import { PageConstants } from '@/pages/md_prog001d0003/config';
import { toast } from 'vue3-toastify';

const props = defineProps<{
    modelValue: any[];
    parentOid?: string | null;
}>();

const emit = defineEmits(['update:modelValue', 'refresh']);

const list = computed({
    get: () => props.modelValue,
    set: (value) => emit('update:modelValue', value)
});

const onEnd = async (evt: any) => {
    // evt.item: dragged element
    // evt.to: target list
    // evt.from: source list
    // newIndex / oldIndex
    
    // 這裡實作 call API 的邏輯
    const movedItem = evt.item._underlying_vm_; // vuedraggable 綁定的資料物件
    const newParentOid = props.parentOid || null;

    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/move', {
            oid: movedItem.oid,
            parentOid: newParentOid
        });
        
        if (response.data && response.data.success == import.meta.env.VITE_SUCCESS_FLAG) {
            toast.success("移動成功");
            emit('refresh'); // 通知父層刷新資料
        } else {
            toast.warning(response.data.message);
            emit('refresh'); // 失敗則強制刷新以還原狀態
        }
    } catch (e) {
        toast.error("移動失敗");
        emit('refresh');
    }
};
</script>

<template>
    <draggable 
        class="tree-list"
        :list="list"
        group="org-tree"
        item-key="oid"
        @end="onEnd"
    >
        <template #item="{ element }">
            <div class="tree-item">
                <div class="tree-node border p-2 mb-1 bg-white">
                    {{ element.orgName }} ({{ element.orgCode }})
                </div>
                <!-- 遞迴渲染子節點 -->
                <div class="ps-4">
                    <OrgTree 
                        v-model="element.children" 
                        :parentOid="element.oid"
                        @refresh="$emit('refresh')"
                    />
                </div>
            </div>
        </template>
    </draggable>
</template>

<style scoped>
.tree-list { min-height: 20px; }
.tree-item { cursor: move; }
.tree-node:hover { background-color: #e9ecef !important; }
</style>
