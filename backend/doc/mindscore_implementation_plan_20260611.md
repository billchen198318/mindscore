# MindScore Implementation Plan

Date: 2026-06-11

## Current Status

已完成:

- Entity
- Mapper
- Service

目前建議先往上完成：

- 基本資料
- 計算規則
- KPI 輸入與計分
- OKR 輸入與進度
- Strategy / BSC 對齊
- PDCA / Action
- 報表與儀表板
- Insight / LLM

## Development Order

### Phase 1. 基本資料建立

先完成所有共用主檔，因為 KPI / OKR / Action 都會依賴它們。

1. Organization / Org Unit
2. Employee / Org Member
3. Organization tree / member relation query
4. 基本查詢與維護 API
5. Member password setup/reset flow: create `md_password_reset_token`, queue setup/reset mail in `tb_sys_mail_helper`, and add the administrator forgot-password action in `md_prog001d0002`.

### Phase 2. 計算規則建立

先把 KPI 的公式與計分規則定義好，後面才可以算分數。

1. Formula CRUD
2. Aggregation Method CRUD
3. Formula Recommend Rule CRUD
4. Formula auto-selection flow
5. Formula test / preview

### Phase 3. KPI 基本資料建立

KPI 是整個系統的第一個核心業務模組。

Implementation note: KPI owner binding can be implemented inside the KPI master create/edit flow. A separate owner-binding page is optional and should be added only when owner maintenance needs independent permissions or a separate operational workflow.

1. KPI master CRUD
2. KPI owner 綁定
3. KPI 與 organization / account 綁定
4. KPI 與 formula / aggregation method 綁定
5. KPI enable / disable

### Phase 4. KPI 資料輸入

沒有 measure data，就無法進行 KPI 計分與報表。

1. Measure Data CRUD
2. 依 frequency 建立輸入頁
3. day / week / month / quarter / halfyear / year bucket 規則
4. target / actual / org / account 維度支援
5. 匯入 API

### Phase 5. KPI 計分功能

把 KPI 的 deterministic score engine 做出來。

KPI score is calculated by the backend deterministic engine. The engine uses KPI formula and aggregation method configuration, writes the official result to score snapshot, and keeps calculation trace for audit.

Calculation can be triggered by KPI report query, manual recalculation, or future scheduled job. KPI report may trigger real-time recalculation before reading score snapshot, but frontend must not calculate the official KPI score directly.

1. Score calculation service
2. Formula-based score calculation
3. Aggregation method calculation
4. Calculation trace
5. Score snapshot
6. Score color
7. Recalculate by period
8. Real-time recalculation entry for KPI report

### Phase 6. KPI 報表

KPI 報表應該建立在已存在的 score snapshot 上。

KPI report is a real-time score entry point. Report APIs call the backend deterministic score engine first, persist or update score snapshot, then query snapshot for report rendering.

Report display should expose the score result together with its calculation source, including formula, aggregation method, score color, and calculation trace.

1. Real-time KPI report query
2. Trend chart from recalculated score snapshot
3. Gauge / score display
4. Target vs actual chart
5. Personal / org KPI view
6. Formula / aggregation / trace display

### Phase 7. OKR 週期建立

OKR 一定要先有 cycle，後面 objective / KR 才有歸屬。

1. OKR cycle CRUD
2. Cycle status flow
3. Cycle list / select API

### Phase 8. OKR 基本資料建立

先完成 OKR 結構，再做進度與報表。

1. Objective CRUD
2. Objective owner 綁定
3. Key Result CRUD
4. Initiative CRUD
5. Objective hierarchy / alignment

### Phase 9. OKR 進度更新

這一步是 OKR 的核心。

1. KR check-in / value update
2. KR progress calculation
3. Objective progress roll-up
4. OKR snapshot
5. Confidence / status update

### Phase 10. OKR 報表

把 OKR 的層級與進度顯示出來。

1. OKR hierarchy view
2. Objective detail
3. KR progress view
4. Initiative list
5. Personal / org OKR report

### Phase 11. Strategy / BSC 對齊

這是進階策略層，應在 KPI / OKR 穩定後再做。

1. Strategy workspace CRUD
2. Strategy theme CRUD
3. Strategy objective CRUD
4. Strategy objective link to KPI / OKR
5. Strategy report / BSC report generates or reuses strategy snapshot automatically

Strategy snapshot is not a normal user-maintained CRUD function.
It should be created or reused by report generation, publish/freeze, or scheduled period-close behavior.
For ordinary users, strategy snapshot should be exposed as report evidence/history, not as a daily maintenance screen.

### Phase 12. PDCA / Action 功能

把 KPI / OKR 的問題轉成可執行的行動。

Implementation decision:

- `Action Plan` and `Action Item` are the primary user-facing pages.
- `Action owner` and `Action source link` are necessary data functions, but they should be embedded into Action Plan / Action Item create, edit, and detail screens first.
- Do not build standalone owner/source-link CRUD pages unless centralized maintenance becomes a real workflow requirement.
- Action / PDCA needs a dedicated report page before the cross-domain dashboard, because the maintenance pages alone cannot show overdue work, stage distribution, owner workload or source coverage clearly.

Recommended implementation order:

1. Action plan CRUD, including plan-level owner list and source links
2. Action item CRUD, including item-level owner list, source links, PDCA stage, progress and due dates
3. Action status flow and progress rollup from item to plan
4. Action / PDCA report for status distribution, PDCA stage distribution, overdue actions, owner workload, source coverage and plan-item drill-down
5. Source-driven action creation from KPI / OKR / Strategy / Insight reports
6. Optional centralized Action owner / Action source link maintenance only if needed later

### Phase 13. 管理儀表板與綜合報表

整合 KPI / OKR / Strategy / Action 的管理視角。

1. Personal dashboard
2. Organization dashboard
3. Scorecard / strategy report
4. Delayed action view
5. At-risk objective view

### Phase 14. Insight / LLM

最後才做 insight，因為它依賴前面 deterministic 資料。Phase 14 應先建立 LLM 基礎設施，再建立 signal / rule 與實際 insight 流程；規則引擎仍須先於任何 LLM-driven insight。

1. LLM provider config, first-class support for `OPENAI` and `GEMINI`
2. LLM run log
3. Performance signal
4. Interpretation rule
5. Insight generate / list / detail
6. Recommendation
7. Action from insight
Current implementation notes:

- `MD_PROG010D0001` implements LLM provider config, connection test, and run log query.
- `MD_PROG010D0002` implements performance signal query and signal generation for KPI, OKR, Strategy, and Action overdue cases.
- `MD_PROG010D0003` implements interpretation rule maintenance and rule evaluation through `/api/MD_PROG010D0003/evaluate`.
- Rule evaluation reads open `md_performance_signal` rows, matches enabled `md_interpretation_rule` rows, and creates or updates `md_insight`.
- `MD_PROG010D0004` implements Insight Inbox: list, detail, Accept, Dismiss, and Resolve. It does not provide manual create.
- `MD_PROG010D0005` implements Insight Evidence / Recommendation: evidence list, recommendation maintenance, recommendation status, LLM recommendation generation, and action creation from recommendation.
- Rule evaluation now persists insight evidence rows and deterministic recommendation rows from matched performance signals and interpretation rules.
- Unified LLM recommendation generation is implemented through backend provider configuration and run logging. Runtime validation still requires actual provider configuration, API key, encryption key, and network access.
- Phase 14 program implementation is complete. Permission seed data remains an environment/administration task.

Phase 14 foundation tables:

- `md_llm_provider_config`: provider type, API base URL, default model, AES-GCM encrypted API key, enabled/default flags and connection test status.
- `md_llm_run_log`: provider/model/request type, status, duration, token usage, estimated cost and error metadata.

Phase 14 LLM integration principles:

- LLM is an explanation and recommendation layer. It must not replace official KPI / OKR / Strategy / Action calculation logic.
- `LLM Provider Config` should support at least `OPENAI` and `GEMINI` from the first implementation.
- Provider configuration should allow provider type, API base URL, API key, default model, enabled flag and connection test.
- External LLM calls must be executed by the backend through a unified LLM client abstraction. Frontend pages must not call OpenAI, Gemini or other external LLM APIs directly.
- API keys must be stored securely, masked in UI, and never returned to frontend in full after saving.
- `LLM Run Log` should record provider, model, request type, success / failure, error message, token usage and cost estimate when available.

## Recommended MVP Sequence

如果只做最小可用版本，建議優先順序如下：

1. 基本資料建立
2. Formula / Aggregation / Recommend Rule
3. KPI master
4. Measure Data
5. KPI 計分
6. OKR cycle
7. OKR objective / KR / check-in
8. KPI / OKR 報表

## Implementation Principle

- 先做 deterministic domain core，再做視覺化報表
- 先做資料維護，再做查詢與分析
- 先做 KPI / OKR 主流程，再做 Strategy / PDCA
- Insight / LLM 放最後，不要搶官方計分責任

