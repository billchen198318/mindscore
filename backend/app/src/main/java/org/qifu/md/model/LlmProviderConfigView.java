package org.qifu.md.model;
import java.util.Date;
public record LlmProviderConfigView(String oid, String providerCode, String providerName,
        String providerType, String apiBaseUrl, String defaultModel, String apiKeyMasked,
        boolean apiKeyConfigured, String enabledFlag, String defaultFlag, String connectStatus,
        Date lastTestAt, String lastErrorMessage, String configJson, Date cdate, Date udate) { }
