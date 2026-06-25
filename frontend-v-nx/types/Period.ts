export const PERIOD_TYPES = ['DAY', 'WEEK', 'MONTH', 'QUARTER', 'HALFYEAR', 'YEAR'] as const;

export type PeriodType = typeof PERIOD_TYPES[number];

export const isPeriodType = (value: string): value is PeriodType => PERIOD_TYPES.includes(value as PeriodType);

const pad2 = (value: number) => String(value).padStart(2, '0');

export const isoWeek = (date: Date) => {
    const value = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
    value.setUTCDate(value.getUTCDate() + 4 - (value.getUTCDay() || 7));
    const yearStart = new Date(Date.UTC(value.getUTCFullYear(), 0, 1));
    return { year: value.getUTCFullYear(), week: Math.ceil((((value.getTime() - yearStart.getTime()) / 86400000) + 1) / 7) };
};

export const weeksInIsoYear = (year: number) => isoWeek(new Date(year, 11, 28)).week;

export const formatDateValue = (date: Date) => `${date.getFullYear()}-${pad2(date.getMonth() + 1)}-${pad2(date.getDate())}`;

export const formatPeriodKey = (periodType: string, date: Date) => {
    if (!isPeriodType(periodType) || Number.isNaN(date.getTime())) return '';
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    if (periodType === 'DAY') return formatDateValue(date);
    if (periodType === 'WEEK') {
        const week = isoWeek(date);
        return `${week.year}-W${pad2(week.week)}`;
    }
    if (periodType === 'MONTH') return `${year}-${pad2(month)}`;
    if (periodType === 'QUARTER') return `${year}-Q${Math.floor((month - 1) / 3) + 1}`;
    if (periodType === 'HALFYEAR') return `${year}-H${month <= 6 ? 1 : 2}`;
    return String(year);
};

export const parsePeriodKey = (periodType: string, periodKey: string): Date | null => {
    let date: Date | null = null;
    if (periodType === 'DAY' && /^\d{4}-\d{2}-\d{2}$/.test(periodKey)) {
        const [year, month, day] = periodKey.split('-').map(Number);
        date = new Date(year, month - 1, day);
    } else if (periodType === 'WEEK') {
        const match = /^(\d{4})-W(\d{2})$/.exec(periodKey);
        if (match) {
            const januaryFourth = new Date(Date.UTC(Number(match[1]), 0, 4));
            const day = januaryFourth.getUTCDay() || 7;
            januaryFourth.setUTCDate(januaryFourth.getUTCDate() - day + 1 + (Number(match[2]) - 1) * 7);
            date = new Date(januaryFourth.getUTCFullYear(), januaryFourth.getUTCMonth(), januaryFourth.getUTCDate());
        }
    } else if (periodType === 'MONTH' && /^\d{4}-(0[1-9]|1[0-2])$/.test(periodKey)) {
        const [year, month] = periodKey.split('-').map(Number);
        date = new Date(year, month - 1, 1);
    } else if (periodType === 'QUARTER') {
        const match = /^(\d{4})-Q([1-4])$/.exec(periodKey);
        if (match) date = new Date(Number(match[1]), (Number(match[2]) - 1) * 3, 1);
    } else if (periodType === 'HALFYEAR') {
        const match = /^(\d{4})-H([1-2])$/.exec(periodKey);
        if (match) date = new Date(Number(match[1]), (Number(match[2]) - 1) * 6, 1);
    } else if (periodType === 'YEAR' && /^\d{4}$/.test(periodKey)) {
        date = new Date(Number(periodKey), 0, 1);
    }
    return date && formatPeriodKey(periodType, date) === periodKey ? date : null;
};

export const defaultPeriodKey = (periodType: string, date = new Date()) => formatPeriodKey(periodType, date);

export const nextPeriodDate = (periodType: string, date: Date, offset = 1) => {
    const next = new Date(date.getFullYear(), date.getMonth(), date.getDate());
    if (periodType === 'DAY') next.setDate(next.getDate() + offset);
    else if (periodType === 'WEEK') next.setDate(next.getDate() + offset * 7);
    else if (periodType === 'MONTH') next.setMonth(next.getMonth() + offset);
    else if (periodType === 'QUARTER') next.setMonth(next.getMonth() + offset * 3);
    else if (periodType === 'HALFYEAR') next.setMonth(next.getMonth() + offset * 6);
    else if (periodType === 'YEAR') next.setFullYear(next.getFullYear() + offset);
    return next;
};

export const shiftPeriodKey = (periodType: string, periodKey: string, offset: number) => {
    const date = parsePeriodKey(periodType, periodKey) || new Date();
    return formatPeriodKey(periodType, nextPeriodDate(periodType, date, offset));
};

export const countPeriodRange = (periodType: string, from: string, to: string, stopAfter?: number) => {
    let current = parsePeriodKey(periodType, from);
    const end = parsePeriodKey(periodType, to);
    if (!current || !end) return null;
    if (current.getTime() > end.getTime()) return 0;
    let count = 0;
    while (current.getTime() <= end.getTime()) {
        count++;
        if (stopAfter !== undefined && count > stopAfter) return count;
        current = nextPeriodDate(periodType, current);
    }
    return count;
};
