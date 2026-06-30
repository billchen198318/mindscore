export const PageConstants = {
    frontendNamespace: '/md_prog010d0005',
    eventNamespace: '/MD_PROG010D0005',
    QueryId: 'MD_PROG010D0005Q',
    CreateId: 'MD_PROG010D0005A',
    EditId: 'MD_PROG010D0005E',
    DeleteId: 'MD_PROG010D0005D'
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

export const insightStatusOptions = [
    { value: '', label: 'All insight statuses' },
    { value: 'OPEN', label: 'Open' },
    { value: 'ACCEPTED', label: 'Accepted' },
    { value: 'DISMISSED', label: 'Dismissed' },
    { value: 'RESOLVED', label: 'Resolved' }
];

export const recommendationTypeOptions = [
    { value: '', label: 'All recommendation types' },
    { value: 'NEXT_STEP', label: 'Next Step' },
    { value: 'FOLLOW_UP', label: 'Follow Up' },
    { value: 'ACTION', label: 'Action' },
    { value: 'ESCALATION', label: 'Escalation' }
];

export const recommendationStatusOptions = [
    { value: '', label: 'All recommendation statuses' },
    { value: 'OPEN', label: 'Open' },
    { value: 'ACCEPTED', label: 'Accepted' },
    { value: 'DISMISSED', label: 'Dismissed' },
    { value: 'COMPLETED', label: 'Completed' }
];
