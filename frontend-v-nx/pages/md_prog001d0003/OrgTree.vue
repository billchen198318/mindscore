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
    // 獲取被移動的節點
    const movedItem = props.modelValue[evt.newIndex];
    
    // 從拖曳目標的 container 屬性取得正確的目標 parentOid
    const targetParentOid = evt.to.getAttribute('data-parent-oid');
    const newParentOid = (targetParentOid === 'null' || targetParentOid === null) ? null : targetParentOid;

    if (!movedItem) {
        toast.error("無法取得移動節點資訊");
        return;
    }

    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/move', {
            oid: movedItem.oid,
            parentOid: newParentOid
        });
        
        if (response.data && response.data.success == import.meta.env.VITE_SUCCESS_FLAG) {
            toast.success("移動成功");
            emit('refresh'); 
        } else {
            toast.warning(response.data.message);
            emit('refresh'); 
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
        :data-parent-oid="parentOid"
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
