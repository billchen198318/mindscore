<script setup lang="ts">
import { watch } from 'vue';
import { providerTypeOptions, defaultBaseUrl } from './config';
import { checkInvalid, invalidFeedback } from '@/components/BaseHelper';

const props = defineProps<{
    modelValue: any;
    checkFields?: Record<string, any>;
    editMode?: boolean;
}>();
const emit = defineEmits(['update:modelValue']);

const update = (field: string, value: any) => {
    emit('update:modelValue', { ...props.modelValue, [field]: value });
};

watch(() => props.modelValue.providerType, (value, oldValue) => {
    if (value !== oldValue && (!props.modelValue.apiBaseUrl || props.modelValue.apiBaseUrl === defaultBaseUrl(oldValue))) {
        update('apiBaseUrl', defaultBaseUrl(value));
    }
});
</script>

<template>
  <div class="row g-3">
    <div class="col-md-4">
      <label class="form-label" for="providerCode">Provider Code</label>
      <input id="providerCode" :class="['form-control', checkInvalid('providerCode', checkFields) ? 'is-invalid' : '']" :readonly="editMode"
             :value="modelValue.providerCode" @input="update('providerCode', ($event.target as HTMLInputElement).value)">
      <div v-if="checkInvalid('providerCode', checkFields)" class="invalid-feedback">{{ invalidFeedback('providerCode', checkFields) }}</div>
    </div>
    <div class="col-md-4">
      <label class="form-label" for="providerName">Provider Name</label>
      <input id="providerName" :class="['form-control', checkInvalid('providerName', checkFields) ? 'is-invalid' : '']" :value="modelValue.providerName"
             @input="update('providerName', ($event.target as HTMLInputElement).value)">
      <div v-if="checkInvalid('providerName', checkFields)" class="invalid-feedback">{{ invalidFeedback('providerName', checkFields) }}</div>
    </div>
    <div class="col-md-4">
      <label class="form-label" for="providerType">Provider Type</label>
      <select id="providerType" :class="['form-select', checkInvalid('providerType', checkFields) ? 'is-invalid' : '']" :value="modelValue.providerType"
              @change="update('providerType', ($event.target as HTMLSelectElement).value)">
        <option v-for="item in providerTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
      </select>
      <div v-if="checkInvalid('providerType', checkFields)" class="invalid-feedback">{{ invalidFeedback('providerType', checkFields) }}</div>
    </div>
    <div class="col-md-8">
      <label class="form-label" for="apiBaseUrl">API Base URL</label>
      <input id="apiBaseUrl" :class="['form-control', checkInvalid('apiBaseUrl', checkFields) ? 'is-invalid' : '']" :value="modelValue.apiBaseUrl"
             @input="update('apiBaseUrl', ($event.target as HTMLInputElement).value)">
      <div v-if="checkInvalid('apiBaseUrl', checkFields)" class="invalid-feedback">{{ invalidFeedback('apiBaseUrl', checkFields) }}</div>
    </div>
    <div class="col-md-4">
      <label class="form-label" for="defaultModel">Default Model</label>
      <input id="defaultModel" :class="['form-control', checkInvalid('defaultModel', checkFields) ? 'is-invalid' : '']" :value="modelValue.defaultModel"
             @input="update('defaultModel', ($event.target as HTMLInputElement).value)">
      <div v-if="checkInvalid('defaultModel', checkFields)" class="invalid-feedback">{{ invalidFeedback('defaultModel', checkFields) }}</div>
    </div>
    <div class="col-md-8">
      <label class="form-label" for="apiKey">API Key</label>
      <input id="apiKey" type="password" autocomplete="new-password" :class="['form-control', checkInvalid('apiKey', checkFields) ? 'is-invalid' : '']"
             :placeholder="editMode ? 'Leave blank to keep the current API key' : 'Enter API key'"
             :value="modelValue.apiKey" @input="update('apiKey', ($event.target as HTMLInputElement).value)">
      <div v-if="checkInvalid('apiKey', checkFields)" class="invalid-feedback">{{ invalidFeedback('apiKey', checkFields) }}</div>
      <div v-if="editMode && modelValue.apiKeyMasked" class="form-text">
        Current key: {{ modelValue.apiKeyMasked }}
      </div>
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
      <label class="form-label" for="defaultFlag">Default</label>
      <select id="defaultFlag" :class="['form-select', checkInvalid('defaultFlag', checkFields) ? 'is-invalid' : '']" :value="modelValue.defaultFlag"
              @change="update('defaultFlag', ($event.target as HTMLSelectElement).value)">
        <option value="Y">Yes</option><option value="N">No</option>
      </select>
      <div v-if="checkInvalid('defaultFlag', checkFields)" class="invalid-feedback">{{ invalidFeedback('defaultFlag', checkFields) }}</div>
    </div>
    <div class="col-12">
      <label class="form-label" for="configJson">Additional Config JSON</label>
      <textarea id="configJson" rows="4" class="form-control font-monospace" :value="modelValue.configJson"
                @input="update('configJson', ($event.target as HTMLTextAreaElement).value)"></textarea>
    </div>
  </div>
</template>
