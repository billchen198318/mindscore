export const PageConstants = {
    frontendNamespace: '/md_prog010d0001',
    eventNamespace: '/MD_PROG010D0001',
    QueryId: 'MD_PROG010D0001Q',
    CreateId: 'MD_PROG010D0001A',
    EditId: 'MD_PROG010D0001E'
};

export const providerTypeOptions = [
    { value: 'OPENAI', label: 'OpenAI' },
    { value: 'GEMINI', label: 'Google Gemini' }
];

export const defaultBaseUrl = (providerType: string) => providerType === 'GEMINI'
    ? 'https://generativelanguage.googleapis.com/v1beta'
    : 'https://api.openai.com/v1';
