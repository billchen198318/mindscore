export const PageConstants = {
    frontendNamespace: '/md_prog010d0002',
    eventNamespace: '/MD_PROG010D0002',
    QueryId: 'MD_PROG010D0002Q'
};

export const signalTypeOptions = [
    { value: '', label: 'All signal types' },
    { value: 'SCORE_STATUS', label: 'Score Status' },
    { value: 'TARGET_VARIANCE', label: 'Target Variance' },
    { value: 'TREND_DOWN', label: 'Trend' },
    { value: 'OVERDUE', label: 'Overdue' }
];

export const sourceTypeOptions = [
    { value: '', label: 'All sources' },
    { value: 'KPI', label: 'KPI' },
    { value: 'OKR', label: 'OKR' },
    { value: 'STRATEGY', label: 'Strategy' },
    { value: 'ACTION', label: 'Action' }
];
