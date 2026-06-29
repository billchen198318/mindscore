export const PageConstants = {
    frontendNamespace: '/md_prog010d0003',
    eventNamespace: '/MD_PROG010D0003',
    QueryId: 'MD_PROG010D0003Q',
    CreateId: 'MD_PROG010D0003A',
    EditId: 'MD_PROG010D0003E'
};

export const ruleTypeOptions = [
    { value: 'SIGNAL', label: 'Signal' },
    { value: 'INSIGHT', label: 'Insight' },
    { value: 'RECOMMENDATION', label: 'Recommendation' }
];

export const sourceTypeOptions = [
    { value: '', label: 'All sources' },
    { value: 'KPI', label: 'KPI' },
    { value: 'OKR', label: 'OKR' },
    { value: 'STRATEGY', label: 'Strategy' },
    { value: 'ACTION', label: 'Action' }
];

export const severityOptions = [
    { value: '', label: 'All severities' },
    { value: 'LOW', label: 'Low' },
    { value: 'MEDIUM', label: 'Medium' },
    { value: 'HIGH', label: 'High' },
    { value: 'CRITICAL', label: 'Critical' }
];