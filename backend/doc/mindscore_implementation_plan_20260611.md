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

Recommended implementation order:

1. Action plan CRUD, including plan-level owner list and source links
2. Action item CRUD, including item-level owner list, source links, PDCA stage, progress and due dates
3. Action status flow and progress rollup from item to plan
4. Source-driven action creation from KPI / OKR / Strategy / Insight reports
5. Optional centralized Action owner / Action source link maintenance only if needed later

### Phase 13. 管理儀表板與綜合報表

整合 KPI / OKR / Strategy / Action 的管理視角。

1. Personal dashboard
2. Organization dashboard
3. Scorecard / strategy report
4. Delayed action view
5. At-risk objective view

### Phase 14. Insight / LLM

最後才做 insight，因為它依賴前面 deterministic 資料。

1. Performance signal
2. Interpretation rule
3. Insight generate / list / detail
4. Recommendation
5. Action from insight
6. LLM provider config
7. LLM run log

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

