<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { toast } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

import Toolbar from '@/components/Toolbar.vue';
import Grid from '@/components/Grid.vue';
import GridPagination from '@/components/GridPagination.vue';
import HiddenQueryFieldAlertInfo from '@/components/HiddenQueryFieldAlertInfo.vue';
import { PageConstants } from './config';
import { getGridConfig, setConfigRow, setConfigPage, setConfigTotal, resetConfigByOld } from '../../components/GridHelper';
import { useMdProg006d0004Store } from './QueryPageStore';
import {
    getAxiosInstance,
    invalidFeedback,
    checkInvalid,
    escapeQifuHtmlMsg
} from '../../components/BaseHelper';
import { useSwalLoading } from '@/composables/useSwalLoading';

definePageMeta({ middleware: ['auth'] });

const queryPageStore = useMdProg006d0004Store();
const { showLoading, hideLoading, confirmFire } = useSwalLoading();

const pageProgramId = ref(PageConstants.QueryId);
const dsList = ref<any[]>([]);
const cycleList = ref<any[]>([]);
const objectiveList = ref<any[]>([]);
const krList = ref<any[]>([]);
const qFieldShow = ref(true);
const checkFields = ref<any>({});
const pleaseSelectLabel = import.meta.env.VITE_PLEASE_SELECT_LABEL;

const formParam = ref<any>({
    krOid : '',
    checkinDate : new Date().toISOString().substring(0, 10),
    currentValue : null,
    progressValue : 0,
    confidenceScore : null,
    commentText : ''
});

const selectedCycleOid = ref('');
const selectedObjectiveOid = ref('');

const krName = (krOid: string) => {
    const item = krList.value.find((kr: any) => kr.oid === krOid);
    return item ? item.krCode + ' - ' + item.krName : krOid;
};

const tbRefresh = () => btnClear();
const tbQueryFieldShow = () => qFieldShow.value = !qFieldShow.value;

const clearGridConfig = () => {
    setConfigRow(queryPageStore.gridConfig, import.meta.env.VITE_DEFAULT_ROW);
    setConfigPage(queryPageStore.gridConfig, 1);
    setConfigTotal(queryPageStore.gridConfig, 0);
};

const btnClear = () => {
    queryPageStore.clearData();
    selectedCycleOid.value = '';
    selectedObjectiveOid.value = '';
    objectiveList.value = [];
    krList.value = [];
    dsList.value = [];
    checkFields.value = {};
    formParam.value = {
        krOid : '',
        checkinDate : new Date().toISOString().substring(0, 10),
        currentValue : null,
        progressValue : 0,
        confidenceScore : null,
        commentText : ''
    };
    clearGridConfig();
};

const changeQueryGridRow = (row: number) => {
    setConfigRow(queryPageStore.gridConfig, row);
    queryPageStore.gridConfig.page = 1;
    btnQuery();
};

const changePageSelect = (page: number) => {
    setConfigPage(queryPageStore.gridConfig, page);
    btnQuery();
};

const initQueryGridConfig = () => {
    return getGridConfig(
        'oid',
        [
            {
                'method'  : (val: any) => {
                    confirmFire('Delete?', delItem, val);
                },
                'icon'    : 'trash',
                'type'    : 'delete',
                'memo'    : 'Delete current item.',
                'class'   : 'btn btn-danger btn-sm'
            }
        ],
        [
            { label: '<i class="bi bi-hand-index-thumb"></i>', field: 'oid', labHtml: true },
            { label: 'KR', field: 'krOid' },
            { label: 'Check-in Date', field: 'checkinDate' },
            { label: 'Current', field: 'currentValue' },
            { label: 'Progress', field: 'progressValue' },
            { label: 'Confidence', field: 'confidenceScore' },
            { label: 'Comment', field: 'commentText' }
        ]
    );
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
    krList.value = [];
    queryPageStore.queryParam.objectiveOid = '';
    queryPageStore.queryParam.krOid = '';
    formParam.value.krOid = '';
    selectedObjectiveOid.value = '';
    if (!selectedCycleOid.value) {
        return;
    }
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findObjectiveList', {
            cycleOid: selectedCycleOid.value
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

const loadKrList = async () => {
    krList.value = [];
    queryPageStore.queryParam.krOid = '';
    formParam.value.krOid = '';
    if (!selectedObjectiveOid.value) {
        return;
    }
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findKrList', {
            objectiveOid: selectedObjectiveOid.value
        });
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            krList.value = response.data.value || [];
        }
    } catch (e: any) {
        toast.warning(e?.message || e);
    }
};

const btnQuery = async () => {
    showLoading();
    dsList.value = [];
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/findPage', {
            "field": {
                "krOid"            : queryPageStore.queryParam.krOid,
                "checkinDateStart" : queryPageStore.queryParam.checkinDateStart,
                "checkinDateEnd"   : queryPageStore.queryParam.checkinDateEnd
            },
            "pageOf": {
                "select"  : queryPageStore.gridConfig.page,
                "showRow" : queryPageStore.gridConfig.row
            }
        });
        hideLoading();
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                clearGridConfig();
                toast.warning(response.data.message);
                return;
            }
            dsList.value = response.data.value.map((item: any) => ({ ...item, krOid: krName(item.krOid) }));
            setConfigTotal(queryPageStore.gridConfig, response.data.pageOf.countSize);
        } else {
            toast.error('error, null');
            clearGridConfig();
        }
    } catch (e: any) {
        hideLoading();
        clearGridConfig();
        alert(e);
    }
};

const btnSave = async () => {
    checkFields.value = {};
    showLoading();
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/save', {
            ...formParam.value,
            currentValue: formParam.value.currentValue === '' ? null : formParam.value.currentValue,
            confidenceScore: formParam.value.confidenceScore === '' ? null : formParam.value.confidenceScore,
            commentText: formParam.value.commentText || null
        });
        hideLoading();
        if (response.data) {
            checkFields.value = response.data.checkFields || {};
            if (import.meta.env.VITE_SUCCESS_FLAG != response.data.success) {
                toast.warning(escapeQifuHtmlMsg(response.data.message));
                return;
            }
            toast.success(response.data.message);
            formParam.value.commentText = '';
            btnQuery();
        } else {
            toast.error('error, null');
        }
    } catch (e: any) {
        hideLoading();
        alert(e);
    }
};

const delItem = async (oid: string) => {
    showLoading();
    try {
        const axiosInstance = getAxiosInstance();
        const response = await axiosInstance.post(import.meta.env.VITE_API_URL + PageConstants.eventNamespace + '/delete', { "oid": oid });
        hideLoading();
        if (response.data) {
            if (import.meta.env.VITE_SUCCESS_FLAG == response.data.success) {
                toast.success(response.data.message);
            } else {
                toast.warning(response.data.message);
            }
            btnQuery();
        } else {
            toast.error('error, null');
            clearGridConfig();
        }
    } catch (e: any) {
        hideLoading();
        btnQuery();
        alert(e);
    }
};

onMounted(async () => {
    await loadCycleList();
    const newGridConfig = initQueryGridConfig();
    if (queryPageStore.gridConfig.column) {
        resetConfigByOld(newGridConfig, queryPageStore.gridConfig);
    }
    queryPageStore.gridConfig = newGridConfig;
});

watch(() => selectedCycleOid.value, () => loadObjectiveList());
watch(() => selectedObjectiveOid.value, () => loadKrList());
watch(() => formParam.value.krOid, (val) => {
    queryPageStore.queryParam.krOid = val;
});
</script>

<template>
<div class="row">
  <div class="col-12">
    <Toolbar
        :progId="pageProgramId"
        description="OKR Check-in"
        refreshFlag="Y"
        @refreshMethod="tbRefresh"
        queryFieldShowSwitchFlag="Y"
        @queryFieldShowSwitcMethod="tbQueryFieldShow"
    />
  </div>
</div>

<div class="card mb-4">
  <div class="card-body">
    <div class="row g-3">
      <div class="col-md-4">
        <label for="cycleOid" class="form-label">Cycle</label>
        <select class="form-select" id="cycleOid" v-model="selectedCycleOid">
          <option value="">All</option>
          <option v-for="item in cycleList" :key="item.oid" :value="item.oid">{{ item.cycleCode }} - {{ item.cycleName }}</option>
        </select>
      </div>
      <div class="col-md-4">
        <label for="objectiveOid" class="form-label">Objective</label>
        <select class="form-select" id="objectiveOid" v-model="selectedObjectiveOid">
          <option value="">All</option>
          <option v-for="item in objectiveList" :key="item.oid" :value="item.oid">{{ item.objectiveCode }} - {{ item.objectiveName }}</option>
        </select>
      </div>
      <div class="col-md-4">
        <label for="krOid" class="form-label">Key Result</label>
        <select :class="['form-select', checkInvalid('krOid', checkFields) ? 'is-invalid' : '']" id="krOid" v-model="formParam.krOid">
          <option value="">{{ pleaseSelectLabel }}</option>
          <option v-for="item in krList" :key="item.oid" :value="item.oid">{{ item.krCode }} - {{ item.krName }}</option>
        </select>
        <div v-if="checkInvalid('krOid', checkFields)" class="invalid-feedback">{{ invalidFeedback('krOid', checkFields) }}</div>
      </div>
      <div class="col-md-3">
        <label for="checkinDate" class="form-label">Check-in Date</label>
        <input type="date" :class="['form-control', checkInvalid('checkinDate', checkFields) ? 'is-invalid' : '']" id="checkinDate" v-model="formParam.checkinDate">
        <div v-if="checkInvalid('checkinDate', checkFields)" class="invalid-feedback">{{ invalidFeedback('checkinDate', checkFields) }}</div>
      </div>
      <div class="col-md-3">
        <label for="currentValue" class="form-label">Current Value</label>
        <input type="number" step="0.000001" class="form-control" id="currentValue" v-model.number="formParam.currentValue">
        <div class="form-text">Optional. Saved to KR current value.</div>
      </div>
      <div class="col-md-3">
        <label for="progressValue" class="form-label">Progress</label>
        <input type="number" min="0" max="100" step="0.0001" :class="['form-control', checkInvalid('progressValue', checkFields) ? 'is-invalid' : '']" id="progressValue" v-model.number="formParam.progressValue">
        <div v-if="checkInvalid('progressValue', checkFields)" class="invalid-feedback">{{ invalidFeedback('progressValue', checkFields) }}</div>
        <div class="form-text">Required. Updates KR and rolls up Objective.</div>
      </div>
      <div class="col-md-3">
        <label for="confidenceScore" class="form-label">Confidence</label>
        <input type="number" min="0" max="100" step="0.0001" :class="['form-control', checkInvalid('confidenceScore', checkFields) ? 'is-invalid' : '']" id="confidenceScore" v-model.number="formParam.confidenceScore">
        <div v-if="checkInvalid('confidenceScore', checkFields)" class="invalid-feedback">{{ invalidFeedback('confidenceScore', checkFields) }}</div>
      </div>
      <div class="col-md-12">
        <label for="commentText" class="form-label">Comment</label>
        <textarea class="form-control" id="commentText" rows="2" v-model="formParam.commentText"></textarea>
      </div>
      <div class="col-12 d-flex gap-2">
        <button type="button" class="btn btn-primary" @click="btnSave"><i class="bi bi-save"></i> Save Check-in</button>
      </div>
    </div>
  </div>
</div>

<HiddenQueryFieldAlertInfo :dataSource="dsList" :queryFieldShowFlag="qFieldShow" />

<div v-show="qFieldShow" class="card mb-4">
  <div class="card-body">
    <div class="row g-3">
      <div class="col-md-4">
        <div class="form-group form-floating">
          <input type="date" class="form-control" id="checkinDateStart" v-model="queryPageStore.queryParam.checkinDateStart">
          <label for="checkinDateStart">Date From</label>
        </div>
      </div>
      <div class="col-md-4">
        <div class="form-group form-floating">
          <input type="date" class="form-control" id="checkinDateEnd" v-model="queryPageStore.queryParam.checkinDateEnd">
          <label for="checkinDateEnd">Date To</label>
        </div>
      </div>
      <div class="col-md-4 d-flex align-items-end gap-2">
        <button type="button" class="btn btn-primary" @click="btnQuery"><i class="bi bi-search"></i> Query History</button>
        <button type="button" class="btn btn-outline-secondary" @click="btnClear"><i class="bi bi-eraser"></i> Clear</button>
      </div>
    </div>
  </div>
</div>

<div class="row">
    <div class="col-12">
        <GridPagination
            :progId="pageProgramId"
            :gridConfig="queryPageStore.gridConfig"
            :changePageSelectMethod="changePageSelect"
            :changeGridConfigRowMethod="changeQueryGridRow"
        />
        <Grid :progId="pageProgramId" :dataSource="dsList" :config="queryPageStore.gridConfig" />
    </div>
</div>
</template>
