<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps<{
    label: string;
    modelValue: string;
    periodType: string;
}>();

const emit = defineEmits<{
    (event: 'update:modelValue', value: string): void;
}>();

const quarterOptions = [
    { value: '1', label: 'Q1' },
    { value: '2', label: 'Q2' },
    { value: '3', label: 'Q3' },
    { value: '4', label: 'Q4' }
];
const halfYearOptions = [
    { value: '1', label: 'H1' },
    { value: '2', label: 'H2' }
];
const monthOptions = [
    { value: '01', label: '01' },
    { value: '02', label: '02' },
    { value: '03', label: '03' },
    { value: '04', label: '04' },
    { value: '05', label: '05' },
    { value: '06', label: '06' },
    { value: '07', label: '07' },
    { value: '08', label: '08' },
    { value: '09', label: '09' },
    { value: '10', label: '10' },
    { value: '11', label: '11' },
    { value: '12', label: '12' }
];

const pad2 = (value: number) => String(value).padStart(2, '0');
const currentDate = () => new Date(new Date().toISOString().slice(0, 10) + 'T00:00:00');
const yearOptions = computed(() => {
    const currentYear = new Date().getFullYear();
    const years = [];
    for (let year = currentYear + 5; year >= currentYear - 10; year--) {
        years.push(String(year));
    }
    return years;
});
const weekOptions = computed(() => {
    const count = weeksInIsoYear(Number(selectedYear.value || new Date().getFullYear()));
    return Array.from({ length: count }, (_, index) => pad2(index + 1));
});

const emitValue = (value: string) => emit('update:modelValue', value);
const isoWeek = (date: Date) => {
    const d = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
    d.setUTCDate(d.getUTCDate() + 4 - (d.getUTCDay() || 7));
    const yearStart = new Date(Date.UTC(d.getUTCFullYear(), 0, 1));
    return {
        year : d.getUTCFullYear(),
        week : Math.ceil((((d.getTime() - yearStart.getTime()) / 86400000) + 1) / 7)
    };
};
const weeksInIsoYear = (year: number) => isoWeek(new Date(year, 11, 28)).week;
const parsePeriodDate = () => {
    const key = props.modelValue || '';
    if (props.periodType === 'DAY' && /^\d{4}-\d{2}-\d{2}$/.test(key)) {
        return new Date(key + 'T00:00:00');
    }
    if (props.periodType === 'WEEK') {
        const match = /^(\d{4})-W(\d{2})$/.exec(key);
        if (match) {
            const date = new Date(Date.UTC(Number(match[1]), 0, 4));
            const day = date.getUTCDay() || 7;
            date.setUTCDate(date.getUTCDate() - day + 1 + (Number(match[2]) - 1) * 7);
            return new Date(date.toISOString().slice(0, 10) + 'T00:00:00');
        }
    }
    if (props.periodType === 'MONTH' && /^\d{4}-\d{2}$/.test(key)) {
        const [year, month] = key.split('-').map(Number);
        return new Date(year, month - 1, 1);
    }
    if (props.periodType === 'QUARTER') {
        const match = /^(\d{4})-Q([1-4])$/.exec(key);
        if (match) {
            return new Date(Number(match[1]), (Number(match[2]) - 1) * 3, 1);
        }
    }
    if (props.periodType === 'HALFYEAR') {
        const match = /^(\d{4})-H([1-2])$/.exec(key);
        if (match) {
            return new Date(Number(match[1]), (Number(match[2]) - 1) * 6, 1);
        }
    }
    if (props.periodType === 'YEAR' && /^\d{4}$/.test(key)) {
        return new Date(Number(key), 0, 1);
    }
    return currentDate();
};
const buildPeriodKey = (date: Date) => {
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    if (props.periodType === 'DAY') {
        return year + '-' + pad2(month) + '-' + pad2(date.getDate());
    }
    if (props.periodType === 'WEEK') {
        const info = isoWeek(date);
        return info.year + '-W' + pad2(info.week);
    }
    if (props.periodType === 'MONTH') {
        return year + '-' + pad2(month);
    }
    if (props.periodType === 'QUARTER') {
        return year + '-Q' + Math.floor((month - 1) / 3 + 1);
    }
    if (props.periodType === 'HALFYEAR') {
        return year + '-H' + (month <= 6 ? '1' : '2');
    }
    if (props.periodType === 'YEAR') {
        return String(year);
    }
    return '';
};
const shiftPeriod = (offset: number) => {
    if (!props.periodType) {
        return;
    }
    const date = parsePeriodDate();
    if (props.periodType === 'DAY') {
        date.setDate(date.getDate() + offset);
    } else if (props.periodType === 'WEEK') {
        date.setDate(date.getDate() + offset * 7);
    } else if (props.periodType === 'MONTH') {
        date.setMonth(date.getMonth() + offset);
    } else if (props.periodType === 'QUARTER') {
        date.setMonth(date.getMonth() + offset * 3);
    } else if (props.periodType === 'HALFYEAR') {
        date.setMonth(date.getMonth() + offset * 6);
    } else {
        date.setFullYear(date.getFullYear() + offset);
    }
    emitValue(buildPeriodKey(date));
};

const selectedYear = computed(() => {
    const key = props.modelValue || '';
    const match = /^(\d{4})/.exec(key);
    return match ? match[1] : '';
});
const selectedMonth = computed(() => {
    const match = /^\d{4}-(\d{2})$/.exec(props.modelValue || '');
    return match ? match[1] : '';
});
const selectedWeek = computed(() => {
    const match = /^\d{4}-W(\d{2})$/.exec(props.modelValue || '');
    return match ? match[1] : '';
});
const selectedQuarter = computed(() => {
    const match = /^\d{4}-Q([1-4])$/.exec(props.modelValue || '');
    return match ? match[1] : '';
});
const selectedHalfYear = computed(() => {
    const match = /^\d{4}-H([1-2])$/.exec(props.modelValue || '');
    return match ? match[1] : '';
});

const updateDay = (event: Event) => emitValue((event.target as HTMLInputElement).value);
const updateYear = (event: Event) => {
    const year = (event.target as HTMLSelectElement).value;
    if (!year) {
        emitValue('');
        return;
    }
    if (props.periodType === 'MONTH') {
        emitValue(year + '-' + (selectedMonth.value || '01'));
    } else if (props.periodType === 'WEEK') {
        const week = selectedWeek.value || '01';
        emitValue(year + '-W' + (Number(week) > weeksInIsoYear(Number(year)) ? pad2(weeksInIsoYear(Number(year))) : week));
    } else if (props.periodType === 'QUARTER') {
        emitValue(year + '-Q' + (selectedQuarter.value || '1'));
    } else if (props.periodType === 'HALFYEAR') {
        emitValue(year + '-H' + (selectedHalfYear.value || '1'));
    } else {
        emitValue(year);
    }
};
const updateMonth = (event: Event) => emitValue((selectedYear.value || String(new Date().getFullYear())) + '-' + (event.target as HTMLSelectElement).value);
const updateWeek = (event: Event) => emitValue((selectedYear.value || String(new Date().getFullYear())) + '-W' + (event.target as HTMLSelectElement).value);
const updateQuarter = (event: Event) => emitValue((selectedYear.value || String(new Date().getFullYear())) + '-Q' + (event.target as HTMLSelectElement).value);
const updateHalfYear = (event: Event) => emitValue((selectedYear.value || String(new Date().getFullYear())) + '-H' + (event.target as HTMLSelectElement).value);
</script>

<template>
  <div class="period-picker">
    <label class="form-label">{{ label }}</label>
    <div class="input-group">
      <button type="button" class="btn btn-outline-secondary" :disabled="!periodType" @click="shiftPeriod(-1)">
        <i class="bi bi-chevron-left"></i>
      </button>
      <template v-if="periodType === 'DAY'">
        <input type="date" class="form-control" :value="modelValue" @input="updateDay">
      </template>
      <template v-else-if="periodType === 'MONTH'">
        <select class="form-select period-year" :value="selectedYear" @change="updateYear">
          <option value=""></option>
          <option v-for="year in yearOptions" :key="year" :value="year">{{ year }}</option>
        </select>
        <select class="form-select period-part" :value="selectedMonth" @change="updateMonth">
          <option v-for="month in monthOptions" :key="month.value" :value="month.value">{{ month.label }}</option>
        </select>
      </template>
      <template v-else-if="periodType === 'WEEK'">
        <select class="form-select period-year" :value="selectedYear" @change="updateYear">
          <option value=""></option>
          <option v-for="year in yearOptions" :key="year" :value="year">{{ year }}</option>
        </select>
        <select class="form-select period-part" :value="selectedWeek" @change="updateWeek">
          <option v-for="week in weekOptions" :key="week" :value="week">Week {{ week }}</option>
        </select>
      </template>
      <template v-else-if="periodType === 'QUARTER'">
        <select class="form-select period-year" :value="selectedYear" @change="updateYear">
          <option value=""></option>
          <option v-for="year in yearOptions" :key="year" :value="year">{{ year }}</option>
        </select>
        <select class="form-select period-part" :value="selectedQuarter" @change="updateQuarter">
          <option v-for="item in quarterOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
      </template>
      <template v-else-if="periodType === 'HALFYEAR'">
        <select class="form-select period-year" :value="selectedYear" @change="updateYear">
          <option value=""></option>
          <option v-for="year in yearOptions" :key="year" :value="year">{{ year }}</option>
        </select>
        <select class="form-select period-part" :value="selectedHalfYear" @change="updateHalfYear">
          <option v-for="item in halfYearOptions" :key="item.value" :value="item.value">{{ item.label }}</option>
        </select>
      </template>
      <template v-else-if="periodType === 'YEAR'">
        <select class="form-select" :value="selectedYear" @change="updateYear">
          <option value=""></option>
          <option v-for="year in yearOptions" :key="year" :value="year">{{ year }}</option>
        </select>
      </template>
      <template v-else>
        <input type="text" class="form-control" disabled>
      </template>
      <button type="button" class="btn btn-outline-secondary" :disabled="!periodType" @click="shiftPeriod(1)">
        <i class="bi bi-chevron-right"></i>
      </button>
    </div>
  </div>
</template>

<style scoped>
.period-picker {
    min-width: 0;
}
.period-year {
    max-width: 92px;
}
.period-part {
    min-width: 96px;
}
</style>
