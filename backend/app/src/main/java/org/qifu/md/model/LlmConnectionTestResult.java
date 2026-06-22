package org.qifu.md.model;
public record LlmConnectionTestResult(boolean connected, int httpStatus, long durationMs, String message) { }
