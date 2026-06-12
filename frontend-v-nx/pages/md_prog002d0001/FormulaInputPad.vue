<script setup lang="ts">
import { ref } from 'vue';

defineProps<{
    disabled?: boolean;
}>();

type FormulaTestValues = {
    actual: number;
    target: number;
    kpiMax: number;
    kpiMin: number;
    kpiTarget: number;
    kpiWeight: number;
};

const emit = defineEmits<{
    insert: [value: string];
    clear: [];
    test: [values: FormulaTestValues];
}>();

const testValues = ref<FormulaTestValues>({
    actual: 70,
    target: 100,
    kpiMax: 100,
    kpiMin: 0,
    kpiTarget: 80,
    kpiWeight: 1
});

const inputGroups = [
    {
        title: '公式參數',
        help: '插入 $P{...} token，執行測試或計算時會轉成實際數值。',
        buttons: [
            { label: '實際值 actual', value: '$P{actual}' },
            { label: '量測目標 target', value: '$P{target}' },
            { label: 'KPI 上限', value: '$P{kpi.max}' },
            { label: 'KPI 下限', value: '$P{kpi.min}' },
            { label: 'KPI 目標', value: '$P{kpi.target}' },
            { label: 'KPI 權重', value: '$P{kpi.weight}' }
        ]
    },
    {
        title: '數字',
        help: '',
        buttons: [
            { label: '7', value: '7' },
            { label: '8', value: '8' },
            { label: '9', value: '9' },
            { label: '4', value: '4' },
            { label: '5', value: '5' },
            { label: '6', value: '6' },
            { label: '1', value: '1' },
            { label: '2', value: '2' },
            { label: '3', value: '3' },
            { label: '0', value: '0' },
            { label: '.', value: '.' }
        ]
    },
    {
        title: '運算子',
        help: '按鈕插入的是 Groovy 可執行的運算符號。',
        buttons: [
            { label: '+', value: ' + ' },
            { label: '-', value: ' - ' },
            { label: '*', value: ' * ' },
            { label: '/', value: ' / ' },
            { label: '%', value: ' % ' },
            { label: '(', value: '(' },
            { label: ')', value: ')' },
            { label: 'abs(', value: 'abs(' },
            { label: 'sqrt(', value: 'sqrt(' }
        ]
    }
];
</script>

<template>
<div class="card formula-input-pad">
  <div class="card-header d-flex flex-wrap gap-2 justify-content-between align-items-center">
    <div>
      <div class="fw-semibold">公式輸入輔助</div>
      <div class="small text-secondary">協助建立 Expression，並可用測試資料立即試算。</div>
    </div>
    <button type="button" class="btn btn-sm btn-outline-secondary" :disabled="disabled" @click="emit('clear')">清空 Expression</button>
  </div>

  <div class="card-body">
    <section class="formula-section formula-test-section mb-4">
      <div class="d-flex flex-wrap gap-2 align-items-center mb-2">
        <span class="badge text-bg-warning">TEST only</span>
        <span class="fw-semibold">公式測試資料</span>
      </div>
      <div class="small text-secondary mb-3">
        這些值只在按 TEST 時帶入後端計算，不會寫入 Formula 資料。用來模擬 $P{...} 參數的實際數值。
      </div>

      <div class="row g-2">
        <div class="col-md-2">
          <label class="form-label small">實際值 actual</label>
          <input type="number" class="form-control form-control-sm" v-model.number="testValues.actual" :disabled="disabled">
        </div>
        <div class="col-md-2">
          <label class="form-label small">量測目標 target</label>
          <input type="number" class="form-control form-control-sm" v-model.number="testValues.target" :disabled="disabled">
        </div>
        <div class="col-md-2">
          <label class="form-label small">KPI 上限</label>
          <input type="number" class="form-control form-control-sm" v-model.number="testValues.kpiMax" :disabled="disabled">
        </div>
        <div class="col-md-2">
          <label class="form-label small">KPI 下限</label>
          <input type="number" class="form-control form-control-sm" v-model.number="testValues.kpiMin" :disabled="disabled">
        </div>
        <div class="col-md-2">
          <label class="form-label small">KPI 目標</label>
          <input type="number" class="form-control form-control-sm" v-model.number="testValues.kpiTarget" :disabled="disabled">
        </div>
        <div class="col-md-2">
          <label class="form-label small">KPI 權重</label>
          <input type="number" class="form-control form-control-sm" v-model.number="testValues.kpiWeight" :disabled="disabled">
        </div>
      </div>

      <div class="mt-3">
        <button type="button" class="btn btn-sm btn-warning" :disabled="disabled" @click="emit('test', testValues)">TEST 試算</button>
      </div>
    </section>

    <section class="formula-section">
      <div class="d-flex flex-wrap gap-2 align-items-center mb-2">
        <span class="badge text-bg-dark">Expression</span>
        <span class="fw-semibold">插入公式內容</span>
      </div>
      <div class="small text-secondary mb-3">
        以下按鈕會插入到 Expression 游標位置，會成為公式內容的一部分。
      </div>

      <div v-for="group in inputGroups" :key="group.title" class="mb-3">
        <div class="small fw-semibold mb-1">{{ group.title }}</div>
        <div v-if="group.help" class="small text-secondary mb-2">{{ group.help }}</div>
        <div class="d-flex flex-wrap gap-2">
          <button
              v-for="button in group.buttons"
              :key="group.title + button.value"
              type="button"
              class="btn btn-sm btn-dark"
              :disabled="disabled"
              @click="emit('insert', button.value)">
            {{ button.label }}
          </button>
        </div>
      </div>
    </section>
  </div>
</div>
</template>

<style scoped>
.formula-input-pad {
    background: #fbfbf8;
    border: 1px solid #d6d3c7;
}

.formula-section {
    border: 1px solid #dedbd0;
    border-radius: 0.75rem;
    padding: 1rem;
    background: #ffffff;
}

.formula-test-section {
    background: #fffaf0;
}
</style>
