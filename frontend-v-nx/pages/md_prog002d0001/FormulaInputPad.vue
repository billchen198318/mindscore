<script setup lang="ts">
import { ref } from 'vue';

defineProps<{
    disabled?: boolean;
}>();

const emit = defineEmits<{
    insert: [value: string];
    clear: [];
    test: [values: FormulaTestValues];
}>();

type FormulaTestValues = {
    actual: number;
    target: number;
    kpiMax: number;
    kpiMin: number;
    kpiTarget: number;
    kpiWeight: number;
};

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
        title: '資料參數',
        buttons: [
            { label: '實際值 actual', value: '$P{actual}' },
            { label: '目標值 target', value: '$P{target}' },
            { label: 'KPI 上限', value: '$P{kpi.max}' },
            { label: 'KPI 下限', value: '$P{kpi.min}' },
            { label: 'KPI 目標', value: '$P{kpi.target}' },
            { label: 'KPI 權重', value: '$P{kpi.weight}' }
        ]
    },
    {
        title: '數字',
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
<div class="card border-secondary formula-input-pad">
  <div class="card-header d-flex justify-content-between align-items-center">
    <span>公式輸入輔助</span>
    <button type="button" class="btn btn-sm btn-outline-secondary" :disabled="disabled" @click="emit('clear')">清空公式</button>
  </div>
  <div class="card-body">
    <div class="row g-2 mb-3">
      <div class="col-md-2">
        <label class="form-label small">actual</label>
        <input type="number" class="form-control form-control-sm" v-model.number="testValues.actual" :disabled="disabled">
      </div>
      <div class="col-md-2">
        <label class="form-label small">target</label>
        <input type="number" class="form-control form-control-sm" v-model.number="testValues.target" :disabled="disabled">
      </div>
      <div class="col-md-2">
        <label class="form-label small">kpi.max</label>
        <input type="number" class="form-control form-control-sm" v-model.number="testValues.kpiMax" :disabled="disabled">
      </div>
      <div class="col-md-2">
        <label class="form-label small">kpi.min</label>
        <input type="number" class="form-control form-control-sm" v-model.number="testValues.kpiMin" :disabled="disabled">
      </div>
      <div class="col-md-2">
        <label class="form-label small">kpi.target</label>
        <input type="number" class="form-control form-control-sm" v-model.number="testValues.kpiTarget" :disabled="disabled">
      </div>
      <div class="col-md-2">
        <label class="form-label small">kpi.weight</label>
        <input type="number" class="form-control form-control-sm" v-model.number="testValues.kpiWeight" :disabled="disabled">
      </div>
    </div>

    <div v-for="group in inputGroups" :key="group.title" class="mb-3">
      <div class="small text-secondary mb-2">{{ group.title }}</div>
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
    <button type="button" class="btn btn-sm btn-warning" :disabled="disabled" @click="emit('test', testValues)">TEST</button>
    <div class="form-text">
      按鈕會插入到 Expression 游標位置。$P{...} 為公式參數 token，後續執行器會依 KPI 或量測資料解析成實際值。
    </div>
  </div>
</div>
</template>

<style scoped>
.formula-input-pad {
    background: #fbfbf8;
}
</style>
