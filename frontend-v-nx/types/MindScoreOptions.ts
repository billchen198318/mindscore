export type OptionItem = {
    value: string;
    label: string;
};

export const managementModeOptions: OptionItem[] = [
    { value: 'BIGGER', label: '越大越好' },
    { value: 'SMALLER', label: '越小越好' },
    { value: 'QUASI', label: '接近目標' },
    { value: 'MANUAL', label: '人工判定' }
];

export const compareModeOptions: OptionItem[] = [
    { value: 'TARGET', label: '目標值比較' },
    { value: 'MINIMUM', label: '最低門檻' },
    { value: 'MAXIMUM', label: '最高門檻' },
    { value: 'RANGE', label: '範圍比較' },
    { value: 'CUSTOM', label: '自訂' }
];

export const periodTypeOptions: OptionItem[] = [
    { value: 'DAY', label: '日' },
    { value: 'WEEK', label: '週' },
    { value: 'MONTH', label: '月' },
    { value: 'QUARTER', label: '季' },
    { value: 'HALFYEAR', label: '半年' },
    { value: 'YEAR', label: '年' }
];

export const kpiPeriodTypeOptions: OptionItem[] = [
    { value: 'ALL', label: '所有週期' },
    ...periodTypeOptions
];

export const dataTypeOptions: OptionItem[] = [
    { value: 'NUMBER', label: '數值' },
    { value: 'PERCENT', label: '百分比' },
    { value: 'CURRENCY', label: '金額' },
    { value: 'BOOLEAN', label: '布林' },
    { value: 'MANUAL', label: '人工輸入' }
];

export const scoreCapModeOptions: OptionItem[] = [
    { value: 'CAP_100', label: '最高 100' },
    { value: 'ALLOW_OVER_100', label: '允許超過 100' },
    { value: 'CUSTOM', label: '自訂' }
];

export const formulaSelectionModeOptions: OptionItem[] = [
    { value: 'AUTO', label: '自動推薦' },
    { value: 'MANUAL_OVERRIDE', label: '人工覆寫' },
    { value: 'CUSTOM', label: '自訂公式' }
];

export const yesNoOptions: OptionItem[] = [
    { value: 'Y', label: '是' },
    { value: 'N', label: '否' }
];

export const withAllOption = (options: OptionItem[]): OptionItem[] => [
    { value: '', label: '全部' },
    ...options
];

export const withAnyOption = (options: OptionItem[]): OptionItem[] => [
    { value: '', label: '不指定' },
    ...options
];

export const optionName = (options: OptionItem[], value: string) => options.find(item => item.value === value)?.label || value;
export const yesNoName = (value: string) => value === 'Y' ? '是' : value === 'N' ? '否' : value;
