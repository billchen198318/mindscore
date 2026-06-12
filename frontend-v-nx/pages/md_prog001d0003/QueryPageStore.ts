import { defineStore } from 'pinia';

export const useMdProg001d0003Store = defineStore('md_prog001d0003', {
    state: () => {
        return { 
            treeData : [] as any[]
        }
    },
    actions: {
        setTreeData(data: any[]) {
            this.treeData = data;
        },
        clearData() {
            this.treeData = [];
        }
    },
})