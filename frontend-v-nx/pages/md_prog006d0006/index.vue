<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { toast } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

import Toolbar from '@/components/Toolbar.vue';
import HiddenQueryFieldAlertInfo from '@/components/HiddenQueryFieldAlertInfo.vue';
import { PageConstants } from './config';
import { useMdProg006d0006Store } from './QueryPageStore';
import {
    getAxiosInstance,
    escapeQifuHtmlMsg
} from '../../components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';
import { useActionSourceNavigation } from '@/composables/useActionSourceNavigation';

definePageMeta({ middleware: ['auth'] });

const queryPageStore = useMdProg006d0006Store();
const { showLoading, hideLoading } = useSwalLoading();
const { createActionFromSource } = useActionSourceNavigation();

const pageProgramId = ref(PageConstants.QueryId);
const hierarchyList = ref<any[]>([]);
const cycleList = ref<any[]>([]);
const orgList = ref<any[]>([]);
const memberList = ref<any[]>([]);
const selectedView = ref<any>(null);
const qFieldShow = ref(true);

const emptySummary = () => ({
    objectiveCount: 0,
    keyResultCount: 0,
    initiativeCount: 0,
    avgProgress: 0,
    goodCount: 0,
    warningCount: 0,
    badCount: 0,
    unknownCount: 0
});
const summary = ref<any>(emptySummary());

const objectiveStatusList = [
    { value: '', label: 'All' },
    { value: 'ACTIVE', label: 'ACTIVE' },
    { value: 'CLOSED', label: 'CLOSED' },
    { value: 'DRAFT', label: 'DRAFT' },
    { value: 'CANCELLED', label: 'CANCELLED' }
];

const statusClass = (status: string) => {
    if (status === 'GOOD') {
        return 'text-bg-success';
    }
    if (status === 'WARNING') {
        return 'text-bg-warning';
    }
    if (status === 'BAD') {
        return 'text-bg-danger';
    }
    return 'text-bg-secondary';
};

const cycleName = (cycleOid: string) => {
    const item = cycleList.value.find((cycle: any) => cycle.oid === cycleOid);
    return item ? item.cycleCode + ' - ' + item.cycleName : cycleOid;
};

const orgName = (orgOid: string) => {
    const item = orgList.value.find((org: any) => org.oid === orgOid);
    return item ? item.orgCode + ' - ' + item.orgName : orgOid;
};

const accountName = (account: string) => {
    const item = memberList.value.find((member: any) => member.account === account);
    return item ? item.account + (item.displayName ? ' - ' + item.displayName : '') : account;
};

const ownerName = (owner: any) => {
    if (!owner) {
        return '';
    }
    if (owner.ownerType === 'ORG') {
        return orgName(owner.orgOid);
    }
    if (owner.ownerType === 'ACCOUNT') {
        return accountName(owner.account);
    }
    return owner.ownerType;
};

const createObjectiveAction = async (objective: any) => {
    const name = objective.objectiveCode + ' - ' + objective.objectiveName;
    if (!await createActionFromSource('OKR_OBJECTIVE', objective.oid, name)) {
        toast.warning('You do not have permission to create an Action Plan.');
    }
};

const createKeyResultAction = async (keyResult: any) => {
    const name = keyResult.krCode + ' - ' + keyResult.krName;
    if (!await createActionFromSource('OKR_KR', keyResult.oid, name)) {
        toast.warning('You do not have permission to create an Action Plan.');
    }
};

const numberText = (value: any) => {
    if (value === null || value === undefined || value === '') {
        return '-';
    }
    const numericValue = Number(value);
    return Number.isNaN(numericValue) ? value : numericValue.toFixed(2);
};

const queryPayload = () => ({
    cycleOid: queryPageStore.queryParam.cycleOid,
    periodKey: queryPageStore.queryParam.periodKey,
    status: queryPageStore.queryParam.status,
    orgOid: queryPageStore.queryParam.orgOid,
    account: queryPageStore.queryParam.account
});

const flattenHierarchy = (items: any[], level = 0): any[] => {
    const result: any[] = [];
    (items || []).forEach((item: any) => {
        result.push({ ...item, level });
        result.push(...flattenHierarchy(item.children || [], level + 1));
    });
    return result;
};

const flatHierarchyList = computed(() => flattenHierarchy(hierarchyList.value));
const hasData = computed(() => flatHierarchyList.value.length > 0);

const tbRefresh = () => btnClear();
const tbQueryFieldShow = () => qFieldShow.value = !qFieldShow.value;

const btnClear = () => {
    queryPageStore.clearData();
    hierarchyList.value = [];
    selectedView.value = null;
    summary.value = emptySummary();
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

const loadHierarchy = async () => {
    const axiosInstance = getAxiosInstance();
    const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/hierarchy', queryPayload());
    if (response.data) {
        if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
            toast.warning(escapeQifuHtmlMsg(response.data.message));
            return;
        }
        hierarchyList.value = response.data.value || [];
        selectedView.value = flatHierarchyList.value.length > 0 ? flatHierarchyList.value[0] : null;
    }
};

const loadSummary = async () => {
    const axiosInstance = getAxiosInstance();
    const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/summary', queryPayload());
    if (response.data) {
        if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
            toast.warning(escapeQifuHtmlMsg(response.data.message));
            return;
        }
        summary.value = response.data.value || emptySummary();
    }
};

const btnQuery = async () => {
    if (!queryPageStore.queryParam.cycleOid) {
        toast.warning('Please select OKR cycle.');
        return;
    }
    showLoading();
    hierarchyList.value = [];
    selectedView.value = null;
    summary.value = emptySummary();
    try {
        await Promise.all([loadHierarchy(), loadSummary()]);
    } catch (e: any) {
        toast.warning(e?.message || e);
    } finally {
        hideLoading();
    }
};

onMounted(async () => {
    await Promise.all([loadCycleList(), loadOrgList(), loadMemberList()]);
});
</script>

<template>
<div class="row">
  <div class="col-12">
    <Toolbar
        :progId="pageProgramId"
        description="OKR Report"
        refreshFlag="Y"
        @refreshMethod="tbRefresh"
        queryFieldShowSwitchFlag="Y"
        @queryFieldShowSwitcMethod="tbQueryFieldShow"
    />
  </div>
</div>

<HiddenQueryFieldAlertInfo :dataSource="flatHierarchyList" :queryFieldShowFlag="qFieldShow" />

<div v-show="qFieldShow" class="card mb-4">
  <div class="card-body">
    <div class="row g-3">
      <div class="col-md-4">
        <label for="cycleOid" class="form-label">Cycle</label>
        <select class="form-select" id="cycleOid" v-model="queryPageStore.queryParam.cycleOid">
          <option value="">Please select</option>
          <option v-for="item in cycleList" :key="item.oid" :value="item.oid">{{ item.cycleCode }} - {{ item.cycleName }}</option>
        </select>
      </div>
      <div class="col-md-4">
        <div class="form-group form-floating">
          <input type="date" class="form-control" id="periodKey" v-model="queryPageStore.queryParam.periodKey">
          <label for="periodKey">Snapshot Period</label>
        </div>
      </div>
      <div class="col-md-4">
        <label for="status" class="form-label">Objective Status</label>
        <select class="form-select" id="status" v-model="queryPageStore.queryParam.status">
          <option v-for="item in objectiveStatusList" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
      </div>
      <div class="col-md-4">
        <label for="orgOid" class="form-label">Organization Owner</label>
        <select class="form-select" id="orgOid" v-model="queryPageStore.queryParam.orgOid">
          <option value="">All</option>
          <option v-for="item in orgList" :key="item.oid" :value="item.oid">{{ orgName(item.oid) }}</option>
        </select>
      </div>
      <div class="col-md-4">
        <label for="account" class="form-label">Account Owner</label>
        <select class="form-select" id="account" v-model="queryPageStore.queryParam.account">
          <option value="">All</option>
          <option v-for="item in memberList" :key="item.account" :value="item.account">{{ accountName(item.account) }}</option>
        </select>
      </div>
      <div class="col-md-4 d-flex align-items-end gap-2">
        <button type="button" class="btn btn-primary" @click="btnQuery"><i class="bi bi-search"></i> Query</button>
        <button type="button" class="btn btn-outline-secondary" @click="btnClear"><i class="bi bi-eraser"></i> Clear</button>
      </div>
    </div>
  </div>
</div>

<div class="row g-3 mb-4">
  <div class="col-md-3 col-lg">
    <div class="border rounded p-3 h-100">
      <div class="small text-muted">Objectives</div>
      <div class="fs-4 fw-semibold">{{ summary.objectiveCount }}</div>
    </div>
  </div>
  <div class="col-md-3 col-lg">
    <div class="border rounded p-3 h-100">
      <div class="small text-muted">Key Results</div>
      <div class="fs-4 fw-semibold">{{ summary.keyResultCount }}</div>
    </div>
  </div>
  <div class="col-md-3 col-lg">
    <div class="border rounded p-3 h-100">
      <div class="small text-muted">Initiatives</div>
      <div class="fs-4 fw-semibold">{{ summary.initiativeCount }}</div>
    </div>
  </div>
  <div class="col-md-3 col-lg">
    <div class="border rounded p-3 h-100">
      <div class="small text-muted">Avg Progress</div>
      <div class="fs-4 fw-semibold">{{ numberText(summary.avgProgress) }}</div>
    </div>
  </div>
  <div class="col-md-3 col-lg">
    <div class="border rounded p-3 h-100">
      <div class="small text-muted">Snapshot Status</div>
      <div class="d-flex flex-wrap gap-1 mt-1">
        <span class="badge text-bg-success">GOOD {{ summary.goodCount }}</span>
        <span class="badge text-bg-warning">WARNING {{ summary.warningCount }}</span>
        <span class="badge text-bg-danger">BAD {{ summary.badCount }}</span>
        <span class="badge text-bg-secondary">UNKNOWN {{ summary.unknownCount }}</span>
      </div>
    </div>
  </div>
</div>

<div class="row g-3">
  <div class="col-lg-7">
    <div class="border rounded overflow-hidden">
      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
          <thead>
            <tr>
              <th>Objective</th>
              <th>Progress</th>
              <th>Snapshot</th>
              <th>Owners</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="!hasData">
              <td colspan="4" class="text-center text-muted py-4">No report data.</td>
            </tr>
            <tr
                v-for="item in flatHierarchyList"
                :key="item.objective.oid"
                role="button"
                :class="{ 'table-primary': selectedView?.objective?.oid === item.objective.oid }"
                @click="selectedView = item">
              <td>
                <div class="fw-semibold" :style="{ paddingLeft: (item.level * 1.25) + 'rem' }">
                  {{ item.objective.objectiveCode }} - {{ item.objective.objectiveName }}
                </div>
                <div class="small text-muted" :style="{ paddingLeft: (item.level * 1.25) + 'rem' }">
                  {{ item.objective.status }} / {{ cycleName(item.objective.cycleOid) }}
                </div>
              </td>
              <td>{{ numberText(item.snapshot?.progressValue ?? item.objective.progressValue) }}</td>
              <td>
                <span class="badge" :class="statusClass(item.snapshot?.scoreStatus)">
                  {{ item.snapshot?.scoreStatus || 'UNKNOWN' }}
                </span>
                <div class="small text-muted">{{ item.snapshot?.periodKey || '-' }}</div>
              </td>
              <td>
                <span v-for="owner in item.ownerList" :key="owner.oid" class="badge text-bg-light border me-1 mb-1">
                  {{ ownerName(owner) }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>

  <div class="col-lg-5">
    <div v-if="selectedView" class="border rounded p-3">
      <div class="d-flex justify-content-between align-items-start gap-3 mb-3">
        <div>
          <div class="fw-bold">{{ selectedView.objective.objectiveCode }} - {{ selectedView.objective.objectiveName }}</div>
          <div class="small text-muted">{{ selectedView.snapshot?.periodKey || queryPageStore.queryParam.periodKey }} / {{ selectedView.snapshot?.snapshotAt || 'No snapshot' }}</div>
        </div>
        <div class="d-flex align-items-center gap-2">
          <span class="badge" :class="statusClass(selectedView.snapshot?.scoreStatus)">
            {{ selectedView.snapshot?.scoreStatus || 'UNKNOWN' }}
          </span>
          <button type="button" class="btn btn-sm btn-outline-success" @click="createObjectiveAction(selectedView.objective)">
            <i class="bi bi-clipboard-plus"></i> Action
          </button>
        </div>
      </div>

      <div class="row g-2 mb-3">
        <div class="col-4"><span class="text-muted">Progress</span> {{ numberText(selectedView.snapshot?.progressValue ?? selectedView.objective.progressValue) }}</div>
        <div class="col-4"><span class="text-muted">Confidence</span> {{ numberText(selectedView.snapshot?.confidenceScore) }}</div>
        <div class="col-4"><span class="text-muted">KR</span> {{ selectedView.keyResultDetailList?.length || 0 }}</div>
      </div>

      <div v-if="selectedView.ownerList && selectedView.ownerList.length > 0" class="mb-3">
        <div class="small text-muted mb-1">Owners</div>
        <span v-for="owner in selectedView.ownerList" :key="owner.oid" class="badge text-bg-secondary me-1 mb-1">
          {{ owner.ownerType }} / {{ owner.ownerRole }} / {{ ownerName(owner) }}
        </span>
      </div>

      <div class="small text-muted mb-2">Initiatives</div>
      <div v-if="!selectedView.initiativeList || selectedView.initiativeList.length === 0" class="text-muted mb-3">
        No initiative data.
      </div>
      <div v-for="initiative in selectedView.initiativeList" :key="initiative.oid" class="border rounded p-2 mb-2">
        <div class="d-flex justify-content-between gap-3">
          <div class="fw-semibold">{{ initiative.initiativeCode }} - {{ initiative.initiativeName }}</div>
          <span class="badge text-bg-light border">{{ initiative.status }}</span>
        </div>
        <div v-if="initiative.content" class="small text-muted mt-1">{{ initiative.content }}</div>
      </div>

      <div class="small text-muted mb-2">Key Results</div>
      <div v-if="!selectedView.keyResultDetailList || selectedView.keyResultDetailList.length === 0" class="text-muted">
        No key result data.
      </div>
      <div v-for="krDetail in selectedView.keyResultDetailList" :key="krDetail.keyResult.oid" class="border rounded p-2 mb-2">
        <div class="d-flex justify-content-between gap-3">
          <div class="fw-semibold">{{ krDetail.keyResult.krCode }} - {{ krDetail.keyResult.krName }}</div>
          <div class="d-flex align-items-center gap-2">
            <div class="text-muted">{{ krDetail.keyResult.status }}</div>
            <button type="button" class="btn btn-sm btn-outline-success" @click="createKeyResultAction(krDetail.keyResult)">
              <i class="bi bi-clipboard-plus"></i> Action
            </button>
          </div>
        </div>
        <div class="row g-2 mt-1 small">
          <div class="col-4"><span class="text-muted">Current</span> {{ numberText(krDetail.keyResult.currentValue) }}</div>
          <div class="col-4"><span class="text-muted">Target</span> {{ numberText(krDetail.keyResult.targetValue) }}</div>
          <div class="col-4"><span class="text-muted">Progress</span> {{ numberText(krDetail.keyResult.progressValue) }}</div>
        </div>
        <div v-if="krDetail.checkinList && krDetail.checkinList.length > 0" class="table-responsive mt-2">
          <table class="table table-sm table-bordered mb-0">
            <thead>
              <tr>
                <th>Check-in</th>
                <th>Current</th>
                <th>Progress</th>
                <th>Confidence</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="checkin in krDetail.checkinList" :key="checkin.oid">
                <td>{{ checkin.checkinDate }}</td>
                <td>{{ numberText(checkin.currentValue) }}</td>
                <td>{{ numberText(checkin.progressValue) }}</td>
                <td>{{ numberText(checkin.confidenceScore) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div v-else class="text-muted small mt-2">No check-in history before this snapshot period.</div>
      </div>
    </div>
    <div v-else class="border rounded p-4 text-center text-muted">
      Select one objective to view detail.
    </div>
  </div>
</div>
</template>
