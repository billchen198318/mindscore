<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
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
const pageProgramId = ref(PageConstants.CreateId);
const checkFields = ref<any>({});
const cycleList = ref<any[]>([]);
const objectiveList = ref<any[]>([]);
const orgList = ref<any[]>([]);
const memberList = ref<any[]>([]);
const orgOwnerList = ref<any[]>([]);
const accountOwnerList = ref<any[]>([]);
const pleaseSelectId = import.meta.env.VITE_PLEASE_SELECT_ID;
const pleaseSelectLabel = import.meta.env.VITE_PLEASE_SELECT_LABEL;
const selectedOrgOid = ref(pleaseSelectId);
const selectedAccount = ref(pleaseSelectId);
const { showLoading, hideLoading } = useSwalLoading();

const defaultForm = () => ({
    cycleOid : pleaseSelectId,
    objectiveCode : '',
    objectiveName : '',
    description : '',
    parentOid : pleaseSelectId,
    confidenceScore : null as number | null,
    progressValue : 0,
    status : 'DRAFT'
});

const formParam = ref<any>(defaultForm());

const btnBack = () => router.back();

const orgName = (orgOid: string) => {
    const item = orgList.value.find((org: any) => org.oid === orgOid);
    return item ? item.orgCode + ' - ' + item.orgName : orgOid;
};

const accountName = (account: string) => {
    const item = memberList.value.find((member: any) => member.account === account);
    return item ? item.account + (item.displayName ? ' - ' + item.displayName : '') : account;
};

const loadCycleList = async () => {
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findCycleList', {});
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            cycleList.value = response.data.value || [];
        }
    } catch (e: any) {
        toast.warning(e?.message || e);
    }
};

const loadObjectiveList = async () => {
    objectiveList.value = [];
    if (formParam.value.cycleOid === pleaseSelectId) {
        return;
    }
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findObjectiveList', {
            cycleOid: formParam.value.cycleOid
        });
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            objectiveList.value = response.data.value || [];
        }
    } catch (e: any) {
        toast.warning(e?.message || e);
    }
};

const loadOrgList = async () => {
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findOrgList', {});
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            orgList.value = (response.data.value || []).filter((item: any) => item.enabled === 'Y');
        }
    } catch (e: any) {
        toast.warning(e?.message || e);
    }
};

const loadMemberList = async () => {
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findMemberList', {});
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            const seen: Record<string, boolean> = {};
            memberList.value = (response.data.value || []).filter((item: any) => {
                if (item.enabled !== 'Y' || !item.account || seen[item.account]) {
                    return false;
                }
                seen[item.account] = true;
                return true;
            });
        }
    } catch (e: any) {
        toast.warning(e?.message || e);
    }
};

const addOrgOwner = () => {
    if (selectedOrgOid.value === pleaseSelectId || orgOwnerList.value.some((item: any) => item.orgOid === selectedOrgOid.value)) {
        return;
    }
    orgOwnerList.value.push({ ownerType: 'ORG', orgOid: selectedOrgOid.value, ownerRole: 'OWNER' });
    selectedOrgOid.value = pleaseSelectId;
};

const removeOrgOwner = (idx: number) => {
    orgOwnerList.value.splice(idx, 1);
};

const addAccountOwner = () => {
    if (selectedAccount.value === pleaseSelectId || accountOwnerList.value.some((item: any) => item.account === selectedAccount.value)) {
        return;
    }
    accountOwnerList.value.push({ ownerType: 'ACCOUNT', account: selectedAccount.value, ownerRole: 'OWNER' });
    selectedAccount.value = pleaseSelectId;
};

const removeAccountOwner = (idx: number) => {
    accountOwnerList.value.splice(idx, 1);
};

const normalizePayload = () => ({
    objective: {
        ...formParam.value,
        parentOid : formParam.value.parentOid === pleaseSelectId ? null : formParam.value.parentOid,
        description : formParam.value.description || null,
        confidenceScore : formParam.value.confidenceScore === '' ? null : formParam.value.confidenceScore
    },
    ownerList: [
        ...orgOwnerList.value,
        ...accountOwnerList.value
    ]
});

const btnClear = () => {
    checkFields.value = {};
    formParam.value = defaultForm();
    orgOwnerList.value = [];
    accountOwnerList.value = [];
    selectedOrgOid.value = pleaseSelectId;
    selectedAccount.value = pleaseSelectId;
    objectiveList.value = [];
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

onMounted(async () => {
    await Promise.all([loadCycleList(), loadOrgList(), loadMemberList()]);
});

watch(
    () => formParam.value.cycleOid,
    () => {
        formParam.value.parentOid = pleaseSelectId;
        loadObjectiveList();
    }
);
</script>

<template>
<div class="row">
    <div class="col-12">
        <Toolbar
            :progId="pageProgramId"
            description="OKR Objective Create"
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
      <div class="col-md-6">
        <label for="cycleOid" class="form-label">Cycle</label>
        <select :class="['form-select', checkInvalid('cycleOid', checkFields) ? 'is-invalid' : '']" id="cycleOid" v-model="formParam.cycleOid">
          <option :value="pleaseSelectId">{{ pleaseSelectLabel }}</option>
          <option v-for="item in cycleList" :key="item.oid" :value="item.oid">{{ item.cycleCode }} - {{ item.cycleName }}</option>
        </select>
        <div v-if="checkInvalid('cycleOid', checkFields)" class="invalid-feedback">{{ invalidFeedback('cycleOid', checkFields) }}</div>
      </div>
      <div class="col-md-6">
        <label for="parentOid" class="form-label">Parent Objective</label>
        <select class="form-select" id="parentOid" v-model="formParam.parentOid">
          <option :value="pleaseSelectId">{{ pleaseSelectLabel }}</option>
          <option v-for="item in objectiveList" :key="item.oid" :value="item.oid">{{ item.objectiveCode }} - {{ item.objectiveName }}</option>
        </select>
      </div>
      <div class="col-md-4">
        <label for="objectiveCode" class="form-label">Objective Code</label>
        <input type="text" :class="['form-control', checkInvalid('objectiveCode', checkFields) ? 'is-invalid' : '']" id="objectiveCode" v-model="formParam.objectiveCode">
        <div v-if="checkInvalid('objectiveCode', checkFields)" class="invalid-feedback">{{ invalidFeedback('objectiveCode', checkFields) }}</div>
      </div>
      <div class="col-md-8">
        <label for="objectiveName" class="form-label">Objective Name</label>
        <input type="text" :class="['form-control', checkInvalid('objectiveName', checkFields) ? 'is-invalid' : '']" id="objectiveName" v-model="formParam.objectiveName">
        <div v-if="checkInvalid('objectiveName', checkFields)" class="invalid-feedback">{{ invalidFeedback('objectiveName', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="progressValue" class="form-label">Progress</label>
        <input type="number" min="0" max="100" step="0.0001" :class="['form-control', checkInvalid('progressValue', checkFields) ? 'is-invalid' : '']" id="progressValue" v-model.number="formParam.progressValue">
        <div v-if="checkInvalid('progressValue', checkFields)" class="invalid-feedback">{{ invalidFeedback('progressValue', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="confidenceScore" class="form-label">Confidence</label>
        <input type="number" min="0" max="100" step="0.0001" :class="['form-control', checkInvalid('confidenceScore', checkFields) ? 'is-invalid' : '']" id="confidenceScore" v-model.number="formParam.confidenceScore">
        <div v-if="checkInvalid('confidenceScore', checkFields)" class="invalid-feedback">{{ invalidFeedback('confidenceScore', checkFields) }}</div>
      </div>
      <div class="col-md-4">
        <label for="status" class="form-label">Status</label>
        <select :class="['form-select', checkInvalid('status', checkFields) ? 'is-invalid' : '']" id="status" v-model="formParam.status">
          <option value="DRAFT">Draft</option>
          <option value="ACTIVE">Active</option>
          <option value="CLOSED">Closed</option>
          <option value="ARCHIVED">Archived</option>
        </select>
        <div v-if="checkInvalid('status', checkFields)" class="invalid-feedback">{{ invalidFeedback('status', checkFields) }}</div>
      </div>

      <div class="col-md-6">
        <label for="orgOwner" class="form-label">Organization Owner</label>
        <div class="input-group">
          <select class="form-select" id="orgOwner" v-model="selectedOrgOid">
            <option :value="pleaseSelectId">{{ pleaseSelectLabel }}</option>
            <option v-for="item in orgList" :key="item.oid" :value="item.oid">{{ item.orgCode }} - {{ item.orgName }}</option>
          </select>
          <button type="button" class="btn btn-outline-primary" @click="addOrgOwner"><i class="bi bi-plus"></i></button>
        </div>
        <div class="mt-2 d-flex flex-column gap-2">
          <div v-for="(item, idx) in orgOwnerList" :key="item.orgOid" class="d-flex align-items-center gap-2">
            <span class="badge text-bg-secondary">{{ orgName(item.orgOid) }}</span>
            <select class="form-select form-select-sm w-auto" v-model="item.ownerRole">
              <option value="OWNER">Owner</option>
              <option value="VIEWER">Viewer</option>
              <option value="APPROVER">Approver</option>
            </select>
            <button type="button" class="btn btn-sm btn-outline-danger" @click="removeOrgOwner(idx)"><i class="bi bi-x"></i></button>
          </div>
        </div>
      </div>
      <div class="col-md-6">
        <label for="accountOwner" class="form-label">Account Owner</label>
        <div class="input-group">
          <select class="form-select" id="accountOwner" v-model="selectedAccount">
            <option :value="pleaseSelectId">{{ pleaseSelectLabel }}</option>
            <option v-for="item in memberList" :key="item.account" :value="item.account">{{ item.account }}<template v-if="item.displayName"> - {{ item.displayName }}</template></option>
          </select>
          <button type="button" class="btn btn-outline-primary" @click="addAccountOwner"><i class="bi bi-plus"></i></button>
        </div>
        <div class="mt-2 d-flex flex-column gap-2">
          <div v-for="(item, idx) in accountOwnerList" :key="item.account" class="d-flex align-items-center gap-2">
            <span class="badge text-bg-secondary">{{ accountName(item.account) }}</span>
            <select class="form-select form-select-sm w-auto" v-model="item.ownerRole">
              <option value="OWNER">Owner</option>
              <option value="VIEWER">Viewer</option>
              <option value="APPROVER">Approver</option>
            </select>
            <button type="button" class="btn btn-sm btn-outline-danger" @click="removeAccountOwner(idx)"><i class="bi bi-x"></i></button>
          </div>
        </div>
      </div>

      <div class="col-md-12">
        <label for="description" class="form-label">Description</label>
        <textarea class="form-control" id="description" rows="3" v-model="formParam.description"></textarea>
      </div>
    </div>
    <div class="mt-4 d-flex gap-2">
      <button type="button" class="btn btn-primary" @click="btnSave"><i class="bi bi-save"></i> Save</button>
      <button type="button" class="btn btn-outline-secondary" @click="btnClear"><i class="bi bi-eraser"></i> Clear</button>
    </div>
  </div>
</div>
</template>
