<script setup lang="ts">
import { checkInvalid, invalidFeedback } from '@/components/BaseHelper';
import { ruleTypeOptions, sourceTypeOptions, severityOptions } from './config';

const props = defineProps<{
    modelValue: any;
    checkFields?: Record<string, any>;
    editMode?: boolean;
}>();
const emit = defineEmits(['update:modelValue']);

const update = (field: string, value: any) => {
    emit('update:modelValue', { ...props.modelValue, [field]: value });
};
</script>

<template>
  <div class="row g-3">
    <div class="col-md-4">
      <label class="form-label" for="ruleCode">Rule Code</label>
      <input id="ruleCode" :readonly="editMode" :class="['form-control', checkInvalid('ruleCode', checkFields) ? 'is-invalid' : '']"
             :value="modelValue.ruleCode" @input="update('ruleCode', ($event.target as HTMLInputElement).value)">
      <div v-if="checkInvalid('ruleCode', checkFields)" class="invalid-feedback">{{ invalidFeedback('ruleCode', checkFields) }}</div>
    </div>
    <div class="col-md-8">
      <label class="form-label" for="ruleName">Rule Name</label>
      <input id="ruleName" :class="['form-control', checkInvalid('ruleName', checkFields) ? 'is-invalid' : '']"
             :value="modelValue.ruleName" @input="update('ruleName', ($event.target as HTMLInputElement).value)">
      <div v-if="checkInvalid('ruleName', checkFields)" class="invalid-feedback">{{ invalidFeedback('ruleName', checkFields) }}</div>
    </div>
    <div class="col-md-3">
      <label class="form-label" for="ruleType">Rule Type</label>
      <select id="ruleType" :class="['form-select', checkInvalid('ruleType', checkFields) ? 'is-invalid' : '']" :value="modelValue.ruleType"
              @change="update('ruleType', ($event.target as HTMLSelectElement).value)">
        <option v-for="item in ruleTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
      </select>
      <div v-if="checkInvalid('ruleType', checkFields)" class="invalid-feedback">{{ invalidFeedback('ruleType', checkFields) }}</div>
    </div>
    <div class="col-md-3">
      <label class="form-label" for="sourceType">Source Type</label>
      <select id="sourceType" :class="['form-select', checkInvalid('sourceType', checkFields) ? 'is-invalid' : '']" :value="modelValue.sourceType"
              @change="update('sourceType', ($event.target as HTMLSelectElement).value)">
        <option v-for="item in sourceTypeOptions.filter((item) => item.value)" :key="item.value" :value="item.value">{{ item.label }}</option>
      </select>
      <div v-if="checkInvalid('sourceType', checkFields)" class="invalid-feedback">{{ invalidFeedback('sourceType', checkFields) }}</div>
    </div>
    <div class="col-md-2">
      <label class="form-label" for="severity">Severity</label>
      <select id="severity" :class="['form-select', checkInvalid('severity', checkFields) ? 'is-invalid' : '']" :value="modelValue.severity"
              @change="update('severity', ($event.target as HTMLSelectElement).value)">
        <option v-for="item in severityOptions.filter((item) => item.value)" :key="item.value" :value="item.value">{{ item.label }}</option>
      </select>
      <div v-if="checkInvalid('severity', checkFields)" class="invalid-feedback">{{ invalidFeedback('severity', checkFields) }}</div>
    </div>
    <div class="col-md-2">
      <label class="form-label" for="enabledFlag">Enabled</label>
      <select id="enabledFlag" :class="['form-select', checkInvalid('enabledFlag', checkFields) ? 'is-invalid' : '']" :value="modelValue.enabledFlag"
              @change="update('enabledFlag', ($event.target as HTMLSelectElement).value)">
        <option value="Y">Yes</option><option value="N">No</option>
      </select>
      <div v-if="checkInvalid('enabledFlag', checkFields)" class="invalid-feedback">{{ invalidFeedback('enabledFlag', checkFields) }}</div>
    </div>
    <div class="col-md-2">
      <label class="form-label" for="priorityNo">Priority</label>
      <input id="priorityNo" type="number" class="form-control" :value="modelValue.priorityNo"
             @input="update('priorityNo', Number(($event.target as HTMLInputElement).value || 0))">
    </div>
    <div class="col-12">
      <label class="form-label" for="conditionExpr">Condition Expression</label>
      <textarea id="conditionExpr" rows="7" :class="['form-control', 'font-monospace', checkInvalid('conditionExpr', checkFields) ? 'is-invalid' : '']"
                :value="modelValue.conditionExpr" @input="update('conditionExpr', ($event.target as HTMLTextAreaElement).value)"></textarea>
      <div v-if="checkInvalid('conditionExpr', checkFields)" class="invalid-feedback">{{ invalidFeedback('conditionExpr', checkFields) }}</div>
    </div>
    <div class="col-12">
      <label class="form-label" for="actionExpr">Action Expression</label>
      <textarea id="actionExpr" rows="7" class="form-control font-monospace" :value="modelValue.actionExpr"
                @input="update('actionExpr', ($event.target as HTMLTextAreaElement).value)"></textarea>
    </div>
    <div class="col-12">
      <label class="form-label" for="description">Description</label>
      <textarea id="description" rows="3" class="form-control" :value="modelValue.description"
                @input="update('description', ($event.target as HTMLTextAreaElement).value)"></textarea>
    </div>
  </div>
</template>