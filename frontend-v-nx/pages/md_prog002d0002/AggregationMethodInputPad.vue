<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { toast } from 'vue3-toastify';
import { PageConstants } from './config';
import {
    getAxiosInstance,
    escapeQifuHtmlMsg
} from '../../components/BaseHelper';

defineProps<{
    disabled?: boolean;
}>();

type AggregationMethod = {
    code: string;
    label: string;
    expression: string;
    description: string;
};

const emit = defineEmits<{
    select: [method: AggregationMethod];
    clear: [];
    test: [scores: number[]];
}>();

const methods = ref<AggregationMethod[]>([]);
const scores = ref<number[]>([80, 90, 75]);

const loadSupportedMethods = async () => {
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/supportedMethods');
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            methods.value = response.data.value || [];
        }
    } catch (e: any) {
        toast.warning(e?.message || e);
    }
};

const addScore = () => {
    scores.value.push(0);
};

const removeScore = (idx: number) => {
    if (scores.value.length <= 1) {
        return;
    }
    scores.value.splice(idx, 1);
};

onMounted(() => {
    loadSupportedMethods();
});
</script>

<template>
<div class="card aggregation-input-pad">
  <div class="card-header d-flex flex-wrap gap-2 justify-content-between align-items-center">
    <div>
      <div class="fw-semibold">彙總方法輔助</div>
      <div class="small text-secondary">方法清單由 AggregationMethodUtils 提供，套用後會填入 Expression。</div>
    </div>
    <button type="button" class="btn btn-sm btn-outline-secondary" :disabled="disabled" @click="emit('clear')">清空 Expression</button>
  </div>

  <div class="card-body">
    <section class="aggregation-section mb-4">
      <div class="d-flex flex-wrap gap-2 align-items-center mb-2">
        <span class="badge text-bg-dark">Method</span>
        <span class="fw-semibold">選擇彙總方法</span>
      </div>
      <div class="small text-secondary mb-3">
        選擇常用方法後，系統會填入對應的 Groovy Expression；需要自訂時再微調內容。
      </div>
      <div class="d-flex flex-wrap gap-2">
        <button
            v-for="method in methods"
            :key="method.code"
            type="button"
            class="btn btn-sm btn-dark"
            :title="method.description"
            :disabled="disabled"
            @click="emit('select', method)">
          {{ method.label }}
        </button>
      </div>
    </section>

    <section class="aggregation-section aggregation-test-section">
      <div class="d-flex flex-wrap gap-2 align-items-center mb-2">
        <span class="badge text-bg-warning">TEST only</span>
        <span class="fw-semibold">測試 scores</span>
      </div>
      <div class="small text-secondary mb-3">
        這些 scores 只在按 TEST 時帶入後端試算，不會寫入彙總方法資料。
      </div>

      <div class="row g-2">
        <div v-for="(_score, idx) in scores" :key="idx" class="col-md-3 col-sm-6">
          <label class="form-label small">score {{ idx + 1 }}</label>
          <div class="input-group input-group-sm">
            <input type="number" class="form-control" v-model.number="scores[idx]" :disabled="disabled">
            <button type="button" class="btn btn-outline-danger" :disabled="disabled || scores.length <= 1" @click="removeScore(idx)">
              <i class="bi bi-trash"></i>
            </button>
          </div>
        </div>
      </div>

      <div class="mt-3 d-flex flex-wrap gap-2">
        <button type="button" class="btn btn-sm btn-outline-secondary" :disabled="disabled" @click="addScore">
          <i class="bi bi-plus-lg"></i> 新增 score
        </button>
        <button type="button" class="btn btn-sm btn-warning" :disabled="disabled" @click="emit('test', scores)">TEST 試算</button>
      </div>
    </section>
  </div>
</div>
</template>

<style scoped>
.aggregation-input-pad {
    background: #fbfbf8;
    border: 1px solid #d6d3c7;
}

.aggregation-section {
    border: 1px solid #dedbd0;
    border-radius: 0.75rem;
    padding: 1rem;
    background: #ffffff;
}

.aggregation-test-section {
    background: #fffaf0;
}
</style>
