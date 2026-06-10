# Hillfog 現代化改版整合方案

文件目的：
- 彙整 `hillfog_20260610.md` 的既有分析
- 彙整 `hillfog_new_idea.txt` 的新產品方向
- 對齊 `D:\home\mindscore\README.md` 的新架構
- 形成可直接用於重構與改版規劃的現代化藍圖

---

## 1. 現況結論

`hillfog` 不是只是一個 KPI / OKR / BSC demo，而是一個可運作的企業績效系統：

- KPI 有計算鏈、日期區間、色階、報表、PDCA
- OKR 有 Objective / Key Result / Initiative / Progress / Hierarchy
- BSC 有 Vision / Perspective / Strategy Objective / KPI 的完整樹狀結構
- PDCA 是執行與追蹤閉環
- 組織圖可呈現員工層級與個人 OKR

但它的實作風格屬於舊式企業系統：

- server-side rendered page
- FreeMarker + jQuery + inline JS
- 報表邏輯大量寫在 controller / util / template / JS
- 模組耦合偏高
- 週期模板與畫面組裝偏 hard-code

所以，`hillfog` 的問題不是「功能不夠」，而是「架構與產品形態已經不符合現代化要求」。

---

## 2. 產品方向

`hillfog_new_idea.txt` 已經把新方向講得很明確，核心不是單純的績效工具，而是：

- `MindScore = Enterprise Performance Intelligence Layer`
- 從 `Performance Management System` 升級成 `Decision Execution System`
- 從 `BSC / OKR / KPI` 的工具，升級成能理解、推理、建議與執行的績效平台

新的產品概念可以整理成四層：

1. `Data Layer`
2. `KPI / Score Engine`
3. `OKR / Intent Layer`
4. `Insight Layer`

最上層則是多種視圖：

- BSC view
- KPI dashboard
- OKR management
- organization / hierarchy view
- reports
- AI insight cards

---

## 3. 新架構基準

`README.md` 對新架構的方向已經很清楚，建議 hillfog 現代化時直接採用同一套分層思維：

- `backend/app`
- qifu4 base / core projects
- `frontend-v-nx`

這代表新系統不應再沿用舊式「一大坨 controller + template + static js」的方式，而要改成：

- backend 提供標準化 API
- frontend 以 Nuxt 3 負責 UI 與互動
- domain / calculation / shared utilities 拆成可重用模組
- 共用基底與業務核心分離

### 3.1 建議責任切分

- qifu4 base / core projects
  - 共用例外、共用回應模型、共用驗證、共用工具、共用安全元件
- qifu4 core projects
  - reused as platform dependencies
  - no MindScore / hillfog business code should be added
- `backend/app`
  - MindScore / hillfog backend code only
  - REST API
  - application service
  - KPI / OKR / BSC / PDCA / measure data / score engine / report service
  - qifu4 JWT / httpOnly / permission / menu integration
  - REST API、認證、路由、組態、啟動入口、整合第三方
- `frontend-v-nx`
  - Nuxt 3 前端
  - 組織圖、儀表板、報表、維護頁、表單頁、AI insight UI

---

## 4. hillfog 現況到新架構的轉換原則

### 4.1 保留的部分

以下能力應保留，不要推倒重來：

- KPI 計算模型
- Formula / Aggregation 機制
- OKR progress 規則
- BSC 加權模型
- PDCA 的結構與關聯
- 組織樹與個人 OKR 綁定邏輯
- score color / state color 的概念

### 4.2 必須重做的部分

以下內容建議改成新架構：

- FreeMarker 頁面
- jQuery inline DOM 組裝
- 大量 controller 中直接拼裝報表資料
- 前端以字串拼表格 / 圖表
- hard-code 的週期模板
- 較重的 page-level AJAX flow
- 舊式 permission/menu/page code 與 UI 強耦合

---

## 5. 現代化後的目標系統定義

### 5.1 新系統定位

新的 hillfog 不應只是「績效管理系統」，而應定位成：

- `Enterprise Performance Intelligence Platform`
- `MindScore`
- `績效數據 + 目標管理 + 執行追蹤 + 智能洞察` 平台

### 5.2 功能核心

核心功能應明確分成五大域：

1. `KPI Engine`
2. `OKR Intent Layer`
3. `BSC Strategy View`
4. `PDCA Execution Layer`
5. `Insight / AI Layer`

### 5.3 產品視角

- `KPI` 是事實與數據
- `OKR` 是意圖與方向
- `BSC` 是策略視角與管理框架
- `PDCA` 是執行閉環
- `Insight` 是解釋、建議與推動行動

---

## 6. 建議新資料模型

### 6.1 KPI 領域

KPI 保留以下核心概念：

- KPI master
- KPI owner
- KPI organization mapping
- formula
- aggregation method
- measure data
- date range score
- score color

建議在新架構中再補上：

- version
- status
- calculation trace
- recalculation log
- data source reference
- goal binding reference

### 6.2 OKR 領域

OKR 保留以下核心概念：

- objective
- key result
- initiative
- key result values
- objective progress
- organization owner
- employee owner

建議補強：

- cycle / quarter
- parent objective / alignment
- confidence level
- risk flag
- status
- progress evidence
- approval / review history

### 6.3 BSC 領域

BSC 保留：

- vision
- perspective
- strategy objective
- kpi linkage
- scorecard color
- weight

建議補強：

- strategy map node metadata
- alignment relation
- display order
- year / quarter snapshot
- dashboard layout metadata

### 6.4 PDCA 領域

PDCA 保留：

- main PDCA
- owner
- P / D / C / A item
- attachment
- close request

建議補強：

- approval workflow
- item dependency graph
- due date SLA
- reminder
- audit trail
- status machine

---

## 7. 計算引擎現代化

### 7.1 KPI Score Engine

目前 KPI 已有：

- aggregation
- formula
- date range scoring
- report color

現代化後應升級成獨立計算服務，具備：

- deterministic calculation
- traceable input / output
- calculation versioning
- recalculation by event or schedule
- dependency graph

建議把它拆成：

- `Input Resolver`
- `Aggregation Engine`
- `Formula Engine`
- `Score Normalizer`
- `Color Resolver`
- `Trace Recorder`

### 7.2 OKR Progress Engine

目前 OKR 是：

- key result value aggregation
- opTarget comparison
- average progress

現代化後應增加：

- weight support
- confidence support
- lag / lead indicator
- partial progress evidence
- manual override with audit

### 7.3 BSC Score Engine

BSC 現在是：

- KPI weighted sum
- perspective / strategy / vision aggregation

現代化後應增加：

- snapshot mode
- monthly / quarterly versioning
- what-if analysis
- historical comparison
- drill-down path

---

## 8. 前端現代化方向

### 8.1 從 server-rendered 到 Nuxt 3

舊版 hillfog 的 UI 依賴：

- FreeMarker
- jQuery
- inline JS
- script-based chart rendering

新架構應改成：

- Nuxt 3 page
- Vue component
- composable
- chart component
- org chart component
- table / card / drawer / dialog component

### 8.2 建議頁面拆分

- KPI base management
- KPI report dashboard
- OKR base management
- OKR hierarchy view
- OKR detail view
- BSC strategy map
- Scorecard report
- PDCA management
- measure data input
- formula / aggregation management

### 8.3 前端互動原則

- chart 以 component 管理，不再拼字串
- 表單驗證前後端一致
- autocomplete 改成 API search
- 遞迴樹狀資料用 Vue component
- 報表支援 loading / empty / error 狀態
- 所有查詢條件都可保存成 view preset

---

## 9. API 設計方向

### 9.1 原則

- 以 REST / JSON 為主
- 不再依賴 page-level controller 回傳 HTML
- 報表資料與維護資料分離
- 查詢條件、計算結果、顯示模型分開

### 9.2 建議 API 分層

- `/api/kpi`
- `/api/kpi/reports`
- `/api/okr`
- `/api/okr/reports`
- `/api/scorecards`
- `/api/scorecards/reports`
- `/api/pdca`
- `/api/measure-data`
- `/api/organizations`
- `/api/employees`
- `/api/formulas`
- `/api/aggregation-methods`

### 9.3 回傳模型

建議統一使用：

- `success`
- `message`
- `data`
- `meta`
- `traceId`

報表資料建議額外帶：

- `calculationTrace`
- `scoreColor`
- `sourceDataSummary`
- `lastUpdatedAt`

---

## 10. 權限與多租戶方向

### 10.1 現有問題

舊 hillfog 將 menu / program / permission 與頁面綁得太緊，未來改版會造成：

- 路由難搬
- 權限難拆
- 前後端耦合高
- API 無法單獨授權

### 10.2 新方向

建議改為：

- RBAC 為主
- 若未來要 SaaS 化，再加 tenant / org / workspace
- permission 以 API scope 與 page scope 分離

### 10.3 權限粒度

建議至少分成：

- view
- query
- create
- update
- delete
- approve
- export
- admin

---

## 11. AI / Insight Layer

### 11.1 目標

`hillfog_new_idea.txt` 很明確提到：

- KPI fact
- OKR intent
- Score interpretation
- Insight

也就是說，新系統不應只是算分，而是要回答：

- 為什麼分數變了
- 哪些 data source 造成變化
- 哪些 KR / KPI 是風險點
- 哪些 action 建議優先做

### 11.2 AI 可以做的事

- KPI trend explanation
- anomaly summary
- root cause suggestion
- management summary
- objective recommendation
- action suggestion
- risk detection

### 11.3 AI 不應取代的事

AI 不應直接取代 deterministic engine。  
正確做法是：

- KPI / OKR / BSC 的計算仍由 deterministic engine 負責
- AI 只負責解釋、摘要、建議、互動輔助

---

## 12. 與 qifu4 新架構對齊方式

`README.md` 的價值在於它已經提供一個可直接借鑑的現代骨架：

- `backend/app`：應用層
- qifu4 base / core projects：平台底層，沿用不改
- `frontend-v-nx`：前端層

### 12.1 對 hillfog 的對應

- 舊 `core-app` 的 HTML page、FreeMarker、static js
  - 轉成 `frontend-v-nx`
- 舊 `core-hillfog` 的 entity / service / logic / util
  - 轉成 `backend/app`
- 舊共用工具與基礎服務
  - 優先沿用 qifu4 base / core projects；只有 MindScore 專用工具才放 `backend/app`
- 新 API 與啟動入口
  - 轉成 `backend/app`

### 12.2 重要原則

- 不要把舊頁面邏輯直接搬到新前端
- 不要把舊 controller 直接變成 REST controller 而不整理 domain
- 不要把 chart / tree / report 邏輯散在 page 層

---

## 13. 遷移路線建議

### Phase 1. 先固化領域模型

- 整理 KPI / OKR / BSC / PDCA 的 canonical model
- 明確每個 entity 的責任
- 移除與 UI 綁定的計算邏輯

### Phase 2. 抽出 API

- 先把現有查詢與維護功能變成 JSON API
- 保留舊 UI 作為過渡層

### Phase 3. 新前端並行

- Nuxt 3 開始做新的 dashboard / report / management pages
- 舊 FreeMarker 頁面逐步退場

### Phase 4. 計算引擎重構

- KPI / OKR / BSC / PDCA 相關邏輯拆為服務
- 加入 calculation trace
- 加入 versioning 與 snapshot

### Phase 5. Insight 層

- 先做摘要與解釋
- 再做 anomaly / recommendation
- 最後才做 workflow action

---

## 14. 改版優先順序

如果目標是「快速把 hillfog 現代化」，優先順序建議如下：

1. API 化
2. 前端 Nuxt 3 化
3. KPI / OKR / BSC 模型整理
4. 報表 component 化
5. PDCA 視圖與流程重構
6. 組織圖 OKR 視圖重做
7. Insight / AI layer 補上

---

## 15. 術語說明與實作方式

這一節把前文常用但較抽象的詞，改寫成可直接落地的設計語言。

### 15.1 frequency

`frequency` 在原本 hillfog 裡的意思是：

- 資料輸入的時間粒度
- 報表查詢的時間粒度
- date range score 的 bucket 粒度

它不是版本，不是 cycle，也不是 snapshot。

#### 實作方式

- KPI / measure data 仍保留 `day / week / month / quarter / halfyear / year`
- 前端依 `frequency` 切換輸入 UI
- 後端依 `frequency` 決定 query date 與計算 bucket

### 15.2 versioning

`versioning` 是新的概念，表示：

- 這份 KPI / OKR / Scorecard 屬於哪一版策略
- 哪一版是 draft
- 哪一版是 published
- 哪一版被 archive

#### 實作方式

建議新增獨立版本資料：

- `cycleId`
- `versionNo`
- `status`
- `effectiveStartDate`
- `effectiveEndDate`
- `publishedAt`
- `snapshotAt`

用途：

- 對照不同季度或月份的策略變化
- 回溯當時為什麼分數是那樣
- 支援審核與歷史比較

### 15.3 snapshot

`snapshot` 是某一時間點的凍結視圖。

例如：

- 2026-06-10 的 scorecard snapshot
- 2026-Q2 的 OKR snapshot

#### 實作方式

- 在發佈或結算時把現況寫入 snapshot table
- snapshot 不直接依賴當下的可變資料
- snapshot 用於歷史比較與稽核

### 15.4 lag indicator / lead indicator

這兩個是績效管理常用術語，意思如下：

- `lag indicator`：結果指標，反映已經發生的成果
- `lead indicator`：先行指標，反映可能導向結果的前置行為

#### 例子

`lag indicator`：

- 月營收
- 流失率
- 客訴結案率
- 最終交付達成率

`lead indicator`：

- 潛在客戶數
- Demo 預約數
- PR merge throughput
- 訓練完成率
- 週檢查次數

#### 在 hillfog 的實作方式

建議在 KPI model 新增：

- `indicatorType = lag | lead | mixed`

用途：

- `lag` 用來看結果
- `lead` 用來做預警與行動管理
- `mixed` 可同時兼顧結果與前置行為

### 15.5 intent layer

`OKR intent layer` 的意思不是再做一個 OKR 表單，而是把 OKR 視為「意圖層」。

也就是：

- 目標是什麼
- 這個目標為什麼存在
- 這個目標受到哪些限制
- 這個目標優先順序如何

#### 實作方式

OKR 不只存：

- objective name
- key result
- progress

還應該存：

- objective context
- business theme
- priority
- constraint
- confidence
- owner
- alignment parent

### 15.6 dependency graph

`dependency graph` 是指：

- KPI 與 KPI 之間有依賴
- OKR 與 OKR 之間有對齊
- PDCA item 之間有前後依賴
- action 可能依賴某個 KPI 的完成

#### 實作方式

建議不要只靠單純 parentId，最好區分：

- `parent`
- `dependsOn`
- `blocks`
- `alignedTo`

用途：

- 讓系統知道哪個指標影響哪個目標
- 讓 AI 與報表可以做關聯分析
- 讓使用者看到上下游影響路徑

### 15.7 insight layer

`insight layer` 不是再多做一個 dashboard，而是把資料轉成可行動的洞察。

它回答的不是「發生了什麼」，而是：

- 為什麼發生
- 可能怎麼改善
- 哪些行動最值得先做

#### 實作方式

可拆成四步：

1. 彙整 KPI / OKR / PDCA / activity data
2. 產出分析摘要
3. 產生風險與機會建議
4. 提供 action recommendation

### 15.8 feedback loop

`feedback loop` 是指：

- 數據被收集
- 系統做計算與分析
- 使用者看見結果
- 使用者採取行動
- 新的行動資料回到系統

#### 實作方式

hillfog 現代化後應建立閉環：

- measure data 進來
- KPI / OKR score 計算
- insight 產生
- action 產生
- PDCA 跟進
- 再回寫結果

---

## 16. 風險與注意事項

- 舊系統的 KPI / OKR / BSC 語意混雜，重構時要先定義清楚，不要直接照搬畫面
- `Scorecard score` 與 `OKR progress` 在現有系統是分離的，是否要合併是產品級決策
- 現有資料表與權限設計有歷史包袱，遷移時要保留兼容層
- 前端改版如果太快切斷舊畫面，會影響維運團隊
- AI 層必須建立在穩定的 deterministic engine 上，不能反過來

---

## 17. 最終結論

`hillfog` 其實已經具備：

- KPI
- OKR
- BSC
- PDCA
- 組織圖
- 報表
- 計算引擎

它缺的不是功能，而是現代化產品形態。

所以正確的改版策略不是「重做一個一模一樣的 hillfog」，而是：

- 保留既有績效領域模型
- 重新設計成 API-first
- 以前後端分離取代 server-rendered
- 以 Nuxt 3 重建體驗
- 以 MindScore 的概念把系統升級成 `Enterprise Performance Intelligence Platform`

---

## 18. Development Boundary

MindScore is built on top of qifu4. New business code must stay out of the qifu4 platform layers.

- Backend MindScore code goes under `backend/app`
- Frontend MindScore code goes under `frontend-v-nx`
- `core-base`, `core-standard`, `core-std`, `core-lib`, and `backend/core` are reused as platform dependencies
- JWT, httpOnly cookie handling, base security, role permission, menu, program configuration, upload, token, and common BaseService behavior are provided by qifu4
- Do not add KPI / OKR / BSC / PDCA / report / insight business code into the base or core projects
