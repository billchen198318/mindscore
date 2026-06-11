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

const onChange = async (evt: any) => {
    if (evt.added) {
        // 獲取被移動的節點
        const movedItem = evt.added.element;
        // 使用當前層級的 parentOid 作為新父節點，若無則使用系統預設的零值 OID 代表根節點
        const newParentOid = props.parentOid || '00000000-0000-0000-0000-000000000000';

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
    }
};
</script>

<template>
    <draggable 
        class="tree-list"
        :list="list"
        group="org-tree"
        item-key="oid"
        @change="onChange"
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
