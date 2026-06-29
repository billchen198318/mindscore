export const PageConstants = {
    frontendNamespace: '/md_prog010d0004',
    eventNamespace: '/MD_PROG010D0004',
    QueryId: 'MD_PROG010D0004Q',
    EditId: 'MD_PROG010D0004E'
};

export const insightTypeOptions = [
    { value: '', label: 'All insight types' },
    { value: 'PERFORMANCE_RISK', label: 'Performance Risk' },
    { value: 'RECOMMENDATION', label: 'Recommendation' },
    { value: 'ACTION_REQUIRED', label: 'Action Required' }
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

export const statusOptions = [
    { value: '', label: 'All statuses' },
    { value: 'OPEN', label: 'Open' },
    { value: 'ACCEPTED', label: 'Accepted' },
    { value: 'DISMISSED', label: 'Dismissed' },
    { value: 'RESOLVED', label: 'Resolved' }
];
