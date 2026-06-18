<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useSwalLoading } from '@/composables/useSwalLoading';
import { toast } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

import Toolbar from '@/components/Toolbar.vue';
import { PageConstants } from './config';
import {
    getAxiosInstance,
    invalidFeedback,
    checkInvalid,
    escapeQifuHtmlMsg
} from '../../components/BaseHelper';

definePageMeta({ middleware: ['auth'] });

const router = useRouter();
const { showLoading, hideLoading } = useSwalLoading();
const pageProgramId = ref(PageConstants.CreateId);
const checkFields = ref<any>({});
const pleaseSelectId = import.meta.env.VITE_PLEASE_SELECT_ID;
const pleaseSelectLabel = import.meta.env.VITE_PLEASE_SELECT_LABEL;

const orgList = ref<any[]>([]);
const memberList = ref<any[]>([]);
const kpiList = ref<any[]>([]);
const okrObjectiveList = ref<any[]>([]);
const strategyObjectiveList = ref<any[]>([]);
const ownerList = ref<any[]>([]);
const sourceLinkList = ref<any[]>([]);

const selectedOwnerType = ref('ACCOUNT');
const selectedOwnerAccount = ref(pleaseSelectId);
const selectedOwnerOrgOid = ref(pleaseSelectId);
const selectedOwnerRole = ref('OWNER');
const selectedSourceType = ref('KPI');
const selectedSourceOid = ref(pleaseSelectId);
const selectedSourceReason = ref('');

const statusOptions = [
    { value: 'DRAFT', label: 'Draft' },
    { value: 'ACTIVE', label: 'Active' },
    { value: 'CLOSED', label: 'Closed' },
    { value: 'ARCHIVED', label: 'Archived' }
];
const ownerTypeOptions = [
    { value: 'ACCOUNT', label: 'Account' },
    { value: 'ORG', label: 'Organization' }
];
const ownerRoleOptions = [
    { value: 'OWNER', label: 'Owner' },
    { value: 'VIEWER', label: 'Viewer' },
    { value: 'APPROVER', label: 'Approver' }
];
const sourceTypeOptions = [
    { value: 'KPI', label: 'KPI' },
    { value: 'OKR_OBJECTIVE', label: 'OKR Objective' },
    { value: 'STRATEGY', label: 'Strategy Objective' },
    { value: 'INSIGHT', label: 'Insight' }
];

const defaultPlan = () => ({
    planCode : '',
    planName : '',
    description : '',
    startDate : '',
    endDate : '',
    progressValue : 0,
    status : 'ACTIVE'
});

const actionPlan = ref<any>(defaultPlan());

const currentSourceOptions = computed(() => {
    if (selectedSourceType.value === 'KPI') {
        return kpiList.value.map((item: any) => ({ oid: item.oid, label: item.kpiCode + ' - ' + item.kpiName }));
    }
    if (selectedSourceType.value === 'OKR_OBJECTIVE') {
        return okrObjectiveList.value.map((item: any) => ({ oid: item.oid, label: item.objectiveCode + ' - ' + item.objectiveName }));
    }
    if (selectedSourceType.value === 'STRATEGY') {
        return strategyObjectiveList.value.map((item: any) => ({ oid: item.oid, label: item.objectiveCode + ' - ' + item.objectiveName }));
    }
    return [];
});

const btnBack = () => router.back();

const orgName = (orgOid: string) => {
    const item = orgList.value.find((org: any) => org.oid === orgOid);
    return item ? item.orgCode + ' - ' + item.orgName : orgOid;
};

const accountName = (account: string) => {
    const item = memberList.value.find((member: any) => member.account === account);
    return item ? item.account + (item.displayName ? ' - ' + item.displayName : '') : account;
};

const sourceName = (sourceType: string, sourceOid: string) => {
    if (sourceType === 'KPI') {
        const item = kpiList.value.find((row: any) => row.oid === sourceOid);
        return item ? item.kpiCode + ' - ' + item.kpiName : sourceOid;
    }
    if (sourceType === 'OKR_OBJECTIVE') {
        const item = okrObjectiveList.value.find((row: any) => row.oid === sourceOid);
        return item ? item.objectiveCode + ' - ' + item.objectiveName : sourceOid;
    }
    if (sourceType === 'STRATEGY') {
        const item = strategyObjectiveList.value.find((row: any) => row.oid === sourceOid);
        return item ? item.objectiveCode + ' - ' + item.objectiveName : sourceOid;
    }
    return sourceOid;
};

const loadOptionList = async (path: string, target: any, filterEnabled = true) => {
    const axiosInstance = getAxiosInstance();
    const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + path, {});
    if (response.data && import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
        target.value = filterEnabled ? (response.data.value || []).filter((item: any) => item.enabled !== 'N') : (response.data.value || []);
    }
};

const loadOptions = async () => {
    try {
        await Promise.all([
            loadOptionList('/findOrgList', orgList),
            loadOptionList('/findMemberList', memberList),
            loadOptionList('/findKpiList', kpiList),
            loadOptionList('/findOkrObjectiveList', okrObjectiveList, false),
            loadOptionList('/findStrategyObjectiveList', strategyObjectiveList, false)
        ]);
    } catch (e: any) {
        toast.warning(e?.message || e);
    }
};

const addOwner = () => {
    if (selectedOwnerType.value === 'ACCOUNT') {
        if (selectedOwnerAccount.value === pleaseSelectId || ownerList.value.some((item: any) => item.ownerType === 'ACCOUNT' && item.account === selectedOwnerAccount.value)) {
            return;
        }
        ownerList.value.push({ ownerType: 'ACCOUNT', account: selectedOwnerAccount.value, ownerRole: selectedOwnerRole.value });
        selectedOwnerAccount.value = pleaseSelectId;
        return;
    }
    if (selectedOwnerOrgOid.value === pleaseSelectId || ownerList.value.some((item: any) => item.ownerType === 'ORG' && item.orgOid === selectedOwnerOrgOid.value)) {
        return;
    }
    ownerList.value.push({ ownerType: 'ORG', orgOid: selectedOwnerOrgOid.value, ownerRole: selectedOwnerRole.value });
    selectedOwnerOrgOid.value = pleaseSelectId;
};

const removeOwner = (idx: number) => {
    ownerList.value.splice(idx, 1);
};

const addSourceLink = () => {
    if (selectedSourceType.value !== 'INSIGHT' && selectedSourceOid.value === pleaseSelectId) {
        return;
    }
    const sourceOid = selectedSourceType.value === 'INSIGHT' ? selectedSourceReason.value.trim() : selectedSourceOid.value;
    if (!sourceOid || sourceLinkList.value.some((item: any) => item.sourceType === selectedSourceType.value && item.sourceOid === sourceOid)) {
        return;
    }
    sourceLinkList.value.push({
        sourceType: selectedSourceType.value,
        sourceOid,
        linkReason: selectedSourceType.value === 'INSIGHT' ? '' : selectedSourceReason.value
    });
    selectedSourceOid.value = pleaseSelectId;
    selectedSourceReason.value = '';
};

const removeSourceLink = (idx: number) => {
    sourceLinkList.value.splice(idx, 1);
};

const normalizePayload = () => ({
    actionPlan: {
        ...actionPlan.value,
        startDate: actionPlan.value.startDate || null,
        endDate: actionPlan.value.endDate || null,
        description: actionPlan.value.description || null
    },
    ownerList: ownerList.value,
    sourceLinkList: sourceLinkList.value
});

const btnClear = () => {
    checkFields.value = {};
    actionPlan.value = defaultPlan();
    ownerList.value = [];
    sourceLinkList.value = [];
    selectedOwnerType.value = 'ACCOUNT';
    selectedOwnerAccount.value = pleaseSelectId;
    selectedOwnerOrgOid.value = pleaseSelectId;
    selectedOwnerRole.value = 'OWNER';
    selectedSourceType.value = 'KPI';
    selectedSourceOid.value = pleaseSelectId;
    selectedSourceReason.value = '';
};

const btnSave = async () => {
    checkFields.value = {};
    showLoading();
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/save', normalizePayload());
        hideLoading();
        if (response.data) {
            checkFields.value = response.data.checkFields || {};
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            toast.success(response.data.message);
            btnClear();
        } else {
            toast.error('error, null');
        }
    } catch (e: any) {
        hideLoading();
        alert(e);
    }
};

onMounted(loadOptions);
</script>

<template>
<div class="row">
    <div class="col-12">
        <Toolbar
            :progId="pageProgramId"
            description="Action Plan Create"
            refreshFlag="Y"
            @refreshMethod="btnClear"
            backFlag="Y"
            @backMethod="btnBack"
            saveFlag="Y"
            @saveMethod="btnSave"
        />
    </div>
</div>

<div class="card mb-4">
  <div class="card-body">
    <div class="row g-3">
      <div class="col-md-3">
        <label for="planCode" class="form-label">Plan Code</label>
        <input type="text" :class="['form-control', checkInvalid('planCode', checkFields) ? 'is-invalid' : '']" id="planCode" v-model="actionPlan.planCode">
        <div v-if="checkInvalid('planCode', checkFields)" class="invalid-feedback">{{ invalidFeedback('planCode', checkFields) }}</div>
      </div>
      <div class="col-md-5">
        <label for="planName" class="form-label">Plan Name</label>
        <input type="text" :class="['form-control', checkInvalid('planName', checkFields) ? 'is-invalid' : '']" id="planName" v-model="actionPlan.planName">
        <div v-if="checkInvalid('planName', checkFields)" class="invalid-feedback">{{ invalidFeedback('planName', checkFields) }}</div>
      </div>
      <div class="col-md-2">
        <label for="progressValue" class="form-label">Progress</label>
        <input type="number" min="0" max="100" step="0.01" :class="['form-control', checkInvalid('progressValue', checkFields) ? 'is-invalid' : '']" id="progressValue" v-model.number="actionPlan.progressValue">
        <div v-if="checkInvalid('progressValue', checkFields)" class="invalid-feedback">{{ invalidFeedback('progressValue', checkFields) }}</div>
      </div>
      <div class="col-md-2">
        <label for="status" class="form-label">Status</label>
        <select :class="['form-select', checkInvalid('status', checkFields) ? 'is-invalid' : '']" id="status" v-model="actionPlan.status">
          <option v-for="item in statusOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
        <div v-if="checkInvalid('status', checkFields)" class="invalid-feedback">{{ invalidFeedback('status', checkFields) }}</div>
      </div>
      <div class="col-md-3">
        <label for="startDate" class="form-label">Start Date</label>
        <input type="date" class="form-control" id="startDate" v-model="actionPlan.startDate">
      </div>
      <div class="col-md-3">
        <label for="endDate" class="form-label">End Date</label>
        <input type="date" :class="['form-control', checkInvalid('endDate', checkFields) ? 'is-invalid' : '']" id="endDate" v-model="actionPlan.endDate">
        <div v-if="checkInvalid('endDate', checkFields)" class="invalid-feedback">{{ invalidFeedback('endDate', checkFields) }}</div>
      </div>
      <div class="col-md-6">
        <label for="description" class="form-label">Description</label>
        <textarea class="form-control" id="description" rows="2" v-model="actionPlan.description"></textarea>
      </div>

      <div class="col-md-12"><hr></div>

      <div class="col-md-2">
        <label for="ownerType" class="form-label">Owner Type</label>
        <select class="form-select" id="ownerType" v-model="selectedOwnerType">
          <option v-for="item in ownerTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
      </div>
      <div v-if="selectedOwnerType === 'ACCOUNT'" class="col-md-5">
        <label for="ownerAccount" class="form-label">Account</label>
        <select class="form-select" id="ownerAccount" v-model="selectedOwnerAccount">
          <option :value="pleaseSelectId">{{ pleaseSelectLabel }}</option>
          <option v-for="item in memberList" :key="item.account" :value="item.account">{{ item.account }}<template v-if="item.displayName"> - {{ item.displayName }}</template></option>
        </select>
      </div>
      <div v-if="selectedOwnerType === 'ORG'" class="col-md-5">
        <label for="ownerOrg" class="form-label">Organization</label>
        <select class="form-select" id="ownerOrg" v-model="selectedOwnerOrgOid">
          <option :value="pleaseSelectId">{{ pleaseSelectLabel }}</option>
          <option v-for="item in orgList" :key="item.oid" :value="item.oid">{{ item.orgCode }} - {{ item.orgName }}</option>
        </select>
      </div>
      <div class="col-md-3">
        <label for="ownerRole" class="form-label">Owner Role</label>
        <select class="form-select" id="ownerRole" v-model="selectedOwnerRole">
          <option v-for="item in ownerRoleOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
      </div>
      <div class="col-md-2 d-flex align-items-end">
        <button type="button" class="btn btn-outline-primary w-100" @click="addOwner"><i class="bi bi-plus"></i> Add</button>
      </div>
      <div class="col-md-12 d-flex flex-wrap gap-2">
        <span v-for="(item, idx) in ownerList" :key="idx" class="badge text-bg-secondary d-inline-flex align-items-center gap-2">
          {{ item.ownerType === 'ACCOUNT' ? accountName(item.account) : orgName(item.orgOid) }} / {{ item.ownerRole }}
          <button type="button" class="btn-close btn-close-white" aria-label="Remove" @click="removeOwner(idx)"></button>
        </span>
      </div>

      <div class="col-md-12"><hr></div>

      <div class="col-md-2">
        <label for="sourceType" class="form-label">Source Type</label>
        <select class="form-select" id="sourceType" v-model="selectedSourceType">
          <option v-for="item in sourceTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
      </div>
      <div v-if="selectedSourceType !== 'INSIGHT'" class="col-md-5">
        <label for="sourceOid" class="form-label">Source</label>
        <select class="form-select" id="sourceOid" v-model="selectedSourceOid">
          <option :value="pleaseSelectId">{{ pleaseSelectLabel }}</option>
          <option v-for="item in currentSourceOptions" :key="item.oid" :value="item.oid">{{ item.label }}</option>
        </select>
      </div>
      <div class="col-md-5">
        <label for="sourceReason" class="form-label">{{ selectedSourceType === 'INSIGHT' ? 'Insight Ref' : 'Link Reason' }}</label>
        <input type="text" class="form-control" id="sourceReason" v-model="selectedSourceReason">
      </div>
      <div class="col-md-2 d-flex align-items-end">
        <button type="button" class="btn btn-outline-primary w-100" @click="addSourceLink"><i class="bi bi-plus"></i> Add</button>
      </div>
      <div class="col-md-12">
        <div v-for="(item, idx) in sourceLinkList" :key="idx" class="border rounded p-2 mb-2 d-flex justify-content-between align-items-center gap-2">
          <div>
            <div class="fw-semibold">{{ item.sourceType }} / {{ sourceName(item.sourceType, item.sourceOid) }}</div>
            <div v-if="item.linkReason" class="small text-muted">{{ item.linkReason }}</div>
          </div>
          <button type="button" class="btn btn-sm btn-outline-danger" @click="removeSourceLink(idx)"><i class="bi bi-trash"></i></button>
        </div>
      </div>
    </div>
    <div class="mt-4 d-flex gap-2">
      <button type="button" class="btn btn-primary" @click="btnSave"><i class="bi bi-save"></i> Save</button>
      <button type="button" class="btn btn-outline-secondary" @click="btnClear"><i class="bi bi-eraser"></i> Clear</button>
    </div>
  </div>
</div>
</template>
