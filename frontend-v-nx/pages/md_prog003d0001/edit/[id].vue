<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useSwalLoading } from '@/composables/useSwalLoading';
import { toast } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

import Toolbar from '@/components/Toolbar.vue';
import { PageConstants } from '../config';
import {
    getAxiosInstance,
    invalidFeedback,
    checkInvalid,
    escapeQifuHtmlMsg,
    getProgItem,
    getUrlPrefixFromProgItem
} from '../../../components/BaseHelper';
import {
    managementModeOptions,
    compareModeOptions,
    periodTypeOptions,
    dataTypeOptions,
    scoreCapModeOptions,
    formulaSelectionModeOptions,
    yesNoOptions
} from '@/types/MindScoreOptions';

definePageMeta({ middleware: ['auth'] });

const router = useRouter();
const route = useRoute();
const pageProgramId = ref(PageConstants.EditId);
const checkFields = ref<any>({});
const formulaList = ref<any[]>([]);
const aggrList = ref<any[]>([]);
const orgList = ref<any[]>([]);
const memberList = ref<any[]>([]);
const orgOwnerList = ref<any[]>([]);
const accountOwnerList = ref<any[]>([]);
const selectedOrgOid = ref('');
const selectedAccount = ref('');
const { showLoading, hideLoading } = useSwalLoading();

const formParam = ref<any>({
    oid : route.params.id as string,
    kpiCode : '',
    kpiName : '',
    description : '',
    unitName : '',
    dataType : 'NUMBER',
    periodType : 'MONTH',
    managementMode : 'BIGGER',
    compareMode : 'TARGET',
    minValue : null,
    targetValue : null,
    maxValue : null,
    quasiRange : 0,
    scoreCapMode : 'CAP_100',
    scoringPolicy : '',
    formulaOid : '',
    recommendedFormulaOid : '',
    formulaSelectionMode : 'AUTO',
    aggrMethodOid : '',
    formulaVersionNo : 1,
    weightValue : 0,
    enabled : 'Y'
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

const loadFormulaList = async () => {
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + '/MD_PROG002D0001/findList', {});
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            formulaList.value = response.data.value || [];
        }
    } catch (e: any) {
        toast.warning(e?.message || e);
    }
};

const loadAggrList = async () => {
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + '/MD_PROG002D0002/findList', {});
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            aggrList.value = response.data.value || [];
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
            orgList.value = response.data.value || [];
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
                if (!item.account || seen[item.account]) {
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
    if (!selectedOrgOid.value || orgOwnerList.value.some((item: any) => item.orgOid === selectedOrgOid.value)) {
        return;
    }
    orgOwnerList.value.push({ ownerType: 'ORG', orgOid: selectedOrgOid.value, ownerRole: 'OWNER' });
    selectedOrgOid.value = '';
};

const removeOrgOwner = (idx: number) => {
    orgOwnerList.value.splice(idx, 1);
};

const addAccountOwner = () => {
    if (!selectedAccount.value || accountOwnerList.value.some((item: any) => item.account === selectedAccount.value)) {
        return;
    }
    accountOwnerList.value.push({ ownerType: 'ACCOUNT', account: selectedAccount.value, ownerRole: 'OWNER' });
    selectedAccount.value = '';
};

const removeAccountOwner = (idx: number) => {
    accountOwnerList.value.splice(idx, 1);
};

const normalizePayload = () => ({
    kpi: {
        ...formParam.value,
        recommendedFormulaOid : formParam.value.recommendedFormulaOid || null,
        scoringPolicy : formParam.value.scoringPolicy || null,
        description : formParam.value.description || null,
        unitName : formParam.value.unitName || null
    },
    ownerList: [
        ...orgOwnerList.value,
        ...accountOwnerList.value
    ]
});

const loadData = async () => {
    showLoading();
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/load', { oid : formParam.value.oid });
        hideLoading();
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                router.push(getUrlPrefixFromProgItem(getProgItem(PageConstants.QueryId)));
                return;
            }
            formParam.value = { ...response.data.value.kpi };
            const owners = response.data.value.ownerList || [];
            orgOwnerList.value = owners.filter((item: any) => item.ownerType === 'ORG');
            accountOwnerList.value = owners.filter((item: any) => item.ownerType === 'ACCOUNT');
        } else {
            toast.error('error, null');
            router.push(getUrlPrefixFromProgItem(getProgItem(PageConstants.QueryId)));
        }
    } catch (e: any) {
        hideLoading();
        alert(e);
        router.push(getUrlPrefixFromProgItem(getProgItem(PageConstants.QueryId)));
    }
};

const btnUpdate = async () => {
    checkFields.value = {};
    showLoading();
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/update', normalizePayload());
        hideLoading();
        if (response.data) {
            checkFields.value = response.data.checkFields || {};
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            toast.success(response.data.message);
        } else {
            toast.error('error, null');
        }
    } catch (e: any) {
        hideLoading();
        alert(e);
    }
};

onMounted(async () => {
    await Promise.all([loadFormulaList(), loadAggrList(), loadOrgList(), loadMemberList()]);
    loadData();
});
</script>

<template>
<div class="row">
    <div class="col-12">
        <Toolbar
            :progId="pageProgramId"
            description="KPI基本資料編輯"
            refreshFlag="Y"
            @refreshMethod="loadData"
            backFlag="Y"
            @backMethod="btnBack"
            saveFlag="Y"
            @saveMethod="btnUpdate"
        />
    </div>
</div>

<div class="card mb-4">
  <div class="card-body">
    <div class="row g-3">
      <div class="col-md-3">
        <label for="kpiCode" class="form-label">KPI代碼</label>
        <input type="text" class="form-control" id="kpiCode" v-model="formParam.kpiCode" readonly>
      </div>
      <div class="col-md-5">
        <label for="kpiName" class="form-label">KPI名稱</label>
        <input type="text" :class="['form-control', checkInvalid('kpiName', checkFields) ? 'is-invalid' : '']" id="kpiName" v-model="formParam.kpiName">
        <div v-if="checkInvalid('kpiName', checkFields)" class="invalid-feedback">{{ invalidFeedback('kpiName', checkFields) }}</div>
      </div>
      <div class="col-md-2">
        <label for="unitName" class="form-label">單位</label>
        <input type="text" class="form-control" id="unitName" v-model="formParam.unitName">
      </div>
      <div class="col-md-2">
        <label for="enabled" class="form-label">啟用</label>
        <select :class="['form-select', checkInvalid('enabled', checkFields) ? 'is-invalid' : '']" id="enabled" v-model="formParam.enabled">
          <option v-for="item in yesNoOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
        <div v-if="checkInvalid('enabled', checkFields)" class="invalid-feedback">{{ invalidFeedback('enabled', checkFields) }}</div>
      </div>

      <div class="col-md-3">
        <label for="dataType" class="form-label">資料型態</label>
        <select :class="['form-select', checkInvalid('dataType', checkFields) ? 'is-invalid' : '']" id="dataType" v-model="formParam.dataType">
          <option v-for="item in dataTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
        <div v-if="checkInvalid('dataType', checkFields)" class="invalid-feedback">{{ invalidFeedback('dataType', checkFields) }}</div>
      </div>
      <div class="col-md-3">
        <label for="periodType" class="form-label">週期</label>
        <select :class="['form-select', checkInvalid('periodType', checkFields) ? 'is-invalid' : '']" id="periodType" v-model="formParam.periodType">
          <option v-for="item in periodTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
        <div v-if="checkInvalid('periodType', checkFields)" class="invalid-feedback">{{ invalidFeedback('periodType', checkFields) }}</div>
      </div>
      <div class="col-md-3">
        <label for="managementMode" class="form-label">管理模式</label>
        <select :class="['form-select', checkInvalid('managementMode', checkFields) ? 'is-invalid' : '']" id="managementMode" v-model="formParam.managementMode">
          <option v-for="item in managementModeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
        <div v-if="checkInvalid('managementMode', checkFields)" class="invalid-feedback">{{ invalidFeedback('managementMode', checkFields) }}</div>
      </div>
      <div class="col-md-3">
        <label for="compareMode" class="form-label">比較模式</label>
        <select :class="['form-select', checkInvalid('compareMode', checkFields) ? 'is-invalid' : '']" id="compareMode" v-model="formParam.compareMode">
          <option v-for="item in compareModeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
        <div v-if="checkInvalid('compareMode', checkFields)" class="invalid-feedback">{{ invalidFeedback('compareMode', checkFields) }}</div>
      </div>

      <div class="col-md-3">
        <label for="minValue" class="form-label">最小值</label>
        <input type="number" step="0.0001" class="form-control" id="minValue" v-model.number="formParam.minValue">
      </div>
      <div class="col-md-3">
        <label for="targetValue" class="form-label">目標值</label>
        <input type="number" step="0.0001" class="form-control" id="targetValue" v-model.number="formParam.targetValue">
      </div>
      <div class="col-md-3">
        <label for="maxValue" class="form-label">最大值</label>
        <input type="number" step="0.0001" class="form-control" id="maxValue" v-model.number="formParam.maxValue">
      </div>
      <div class="col-md-3">
        <label for="quasiRange" class="form-label">接近範圍</label>
        <input type="number" min="0" step="0.0001" :class="['form-control', checkInvalid('quasiRange', checkFields) ? 'is-invalid' : '']" id="quasiRange" v-model.number="formParam.quasiRange">
        <div v-if="checkInvalid('quasiRange', checkFields)" class="invalid-feedback">{{ invalidFeedback('quasiRange', checkFields) }}</div>
      </div>

      <div class="col-md-3">
        <label for="scoreCapMode" class="form-label">分數上限模式</label>
        <select :class="['form-select', checkInvalid('scoreCapMode', checkFields) ? 'is-invalid' : '']" id="scoreCapMode" v-model="formParam.scoreCapMode">
          <option v-for="item in scoreCapModeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
        <div v-if="checkInvalid('scoreCapMode', checkFields)" class="invalid-feedback">{{ invalidFeedback('scoreCapMode', checkFields) }}</div>
      </div>
      <div class="col-md-3">
        <label for="formulaSelectionMode" class="form-label">公式選擇</label>
        <select :class="['form-select', checkInvalid('formulaSelectionMode', checkFields) ? 'is-invalid' : '']" id="formulaSelectionMode" v-model="formParam.formulaSelectionMode">
          <option v-for="item in formulaSelectionModeOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
        <div v-if="checkInvalid('formulaSelectionMode', checkFields)" class="invalid-feedback">{{ invalidFeedback('formulaSelectionMode', checkFields) }}</div>
      </div>
      <div class="col-md-3">
        <label for="formulaVersionNo" class="form-label">公式版本</label>
        <input type="number" min="1" :class="['form-control', checkInvalid('formulaVersionNo', checkFields) ? 'is-invalid' : '']" id="formulaVersionNo" v-model.number="formParam.formulaVersionNo">
        <div v-if="checkInvalid('formulaVersionNo', checkFields)" class="invalid-feedback">{{ invalidFeedback('formulaVersionNo', checkFields) }}</div>
      </div>
      <div class="col-md-3">
        <label for="weightValue" class="form-label">權重</label>
        <input type="number" step="0.0001" :class="['form-control', checkInvalid('weightValue', checkFields) ? 'is-invalid' : '']" id="weightValue" v-model.number="formParam.weightValue">
        <div v-if="checkInvalid('weightValue', checkFields)" class="invalid-feedback">{{ invalidFeedback('weightValue', checkFields) }}</div>
      </div>

      <div class="col-md-6">
        <label for="formulaOid" class="form-label">計算公式</label>
        <select :class="['form-select', checkInvalid('formulaOid', checkFields) ? 'is-invalid' : '']" id="formulaOid" v-model="formParam.formulaOid">
          <option value="">請選擇</option>
          <option v-for="item in formulaList" :key="item.oid" :value="item.oid">{{ item.formulaCode }} - {{ item.formulaName }}</option>
        </select>
        <div v-if="checkInvalid('formulaOid', checkFields)" class="invalid-feedback">{{ invalidFeedback('formulaOid', checkFields) }}</div>
      </div>
      <div class="col-md-6">
        <label for="recommendedFormulaOid" class="form-label">推薦公式</label>
        <select class="form-select" id="recommendedFormulaOid" v-model="formParam.recommendedFormulaOid">
          <option value="">不指定</option>
          <option v-for="item in formulaList" :key="item.oid" :value="item.oid">{{ item.formulaCode }} - {{ item.formulaName }}</option>
        </select>
      </div>
      <div class="col-md-6">
        <label for="aggrMethodOid" class="form-label">彙總方法</label>
        <select :class="['form-select', checkInvalid('aggrMethodOid', checkFields) ? 'is-invalid' : '']" id="aggrMethodOid" v-model="formParam.aggrMethodOid">
          <option value="">請選擇</option>
          <option v-for="item in aggrList" :key="item.oid" :value="item.oid">{{ item.aggrCode }} - {{ item.aggrName }}</option>
        </select>
        <div v-if="checkInvalid('aggrMethodOid', checkFields)" class="invalid-feedback">{{ invalidFeedback('aggrMethodOid', checkFields) }}</div>
      </div>
      <div class="col-md-6">
        <label for="scoringPolicy" class="form-label">計分政策</label>
        <input type="text" class="form-control" id="scoringPolicy" v-model="formParam.scoringPolicy">
      </div>

      <div class="col-md-6">
        <label for="orgOwner" class="form-label">負責單位</label>
        <div class="input-group">
          <select class="form-select" id="orgOwner" v-model="selectedOrgOid">
            <option value="">請選擇</option>
            <option v-for="item in orgList" :key="item.oid" :value="item.oid">{{ item.orgCode }} - {{ item.orgName }}</option>
          </select>
          <button type="button" class="btn btn-outline-primary" @click="addOrgOwner"><i class="bi bi-plus"></i></button>
        </div>
        <div class="mt-2 d-flex flex-wrap gap-2">
          <span v-for="(item, idx) in orgOwnerList" :key="item.orgOid" class="badge text-bg-secondary d-inline-flex align-items-center gap-2">
            {{ orgName(item.orgOid) }}
            <button type="button" class="btn-close btn-close-white" aria-label="Remove" @click="removeOrgOwner(idx)"></button>
          </span>
        </div>
      </div>
      <div class="col-md-6">
        <label for="accountOwner" class="form-label">負責人</label>
        <div class="input-group">
          <select class="form-select" id="accountOwner" v-model="selectedAccount">
            <option value="">請選擇</option>
            <option v-for="item in memberList" :key="item.account" :value="item.account">{{ item.account }}<template v-if="item.displayName"> - {{ item.displayName }}</template></option>
          </select>
          <button type="button" class="btn btn-outline-primary" @click="addAccountOwner"><i class="bi bi-plus"></i></button>
        </div>
        <div class="mt-2 d-flex flex-wrap gap-2">
          <span v-for="(item, idx) in accountOwnerList" :key="item.account" class="badge text-bg-secondary d-inline-flex align-items-center gap-2">
            {{ accountName(item.account) }}
            <button type="button" class="btn-close btn-close-white" aria-label="Remove" @click="removeAccountOwner(idx)"></button>
          </span>
        </div>
      </div>

      <div class="col-md-12">
        <label for="description" class="form-label">說明</label>
        <textarea class="form-control" id="description" rows="3" v-model="formParam.description"></textarea>
      </div>
    </div>
    <div class="mt-4 d-flex gap-2">
      <button type="button" class="btn btn-primary" @click="btnUpdate"><i class="bi bi-save"></i> 儲存</button>
    </div>
  </div>
</div>
</template>
