package org.qifu.md.model;
public record LlmProviderConfigRequest(String oid, String providerCode, String providerName,
        String providerType, String apiBaseUrl, String defaultModel, String apiKey,
        String enabledFlag, String defaultFlag, String configJson) { }
