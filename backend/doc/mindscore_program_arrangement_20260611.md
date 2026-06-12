# MindScore Program Arrangement

Date: 2026-06-11

## 1. Goal

依據 `mindscore_implementation_plan_20260611.md`，先把後續會用到的程式名稱、目錄、後端類別、前端頁面與程式碼編號先編排好，避免開發過程中名稱不一致。

本文件採用的原則：

- DB table prefix 使用 `md_`
- Program code prefix 使用 `MD_PROG`
- Backend package 使用 `org.qifu.md`
- Frontend page folder 使用 `pages/md_prog...`
- 盡量沿用 `ai-generator-code-template.md` 的規則
- 與現有 `CORE_PROG...` 平台功能分開

## 2. Naming Rules

### 2.1 Backend

| Type | Naming Rule | Example |
|---|---|---|
| Entity | `Md + Domain + [Child]` | `MdKpi`, `MdOkrObjective`, `MdActionItem` |
| Entity Key | `Md + Domain + Key` | `MdKpiKey`, `MdOkrObjectiveKey` |
| Mapper | `Md + Domain + Mapper` | `MdKpiMapper` |
| Mapper XML | `Md + Domain + Mapper.xml` | `MdKpiMapper.xml` |
| Service Interface | `IMd + Domain + Service` | `IMdKpiService` |
| Service Impl | `Md + Domain + ServiceImpl` | `MdKpiServiceImpl` |
| Controller | `MD_PROG...Controller` | `MD_PROG003D0001Controller` |
| Controller Package | `org.qifu.md.api` | `org.qifu.md.api.MD_PROG003D0001Controller` |

### 2.2 Frontend

| Type | Naming Rule | Example |
|---|---|---|
| Page Folder | `pages/md_prog.../` | `pages/md_prog003d0001/` |
| Query Page | `index.vue` | `pages/md_prog003d0001/index.vue` |
| Create Page | `create.vue` | `pages/md_prog003d0001/create.vue` |
| Edit Page | `edit/[id].vue` | `pages/md_prog003d0001/edit/[id].vue` |
| Set Param Page | `setparam/[id].vue` | `pages/md_prog003d0001/setparam/[id].vue` |
| Config | `config.ts` | `pages/md_prog003d0001/config.ts` |
| Query Store | `QueryPageStore.ts` | `pages/md_prog003d0001/QueryPageStore.ts` |

### 2.3 Program Code Structure

Program code follows the pattern:

```text
MD_PROG[section]D[item][action]
```

Examples:

```text
MD_PROG001D0001Q
MD_PROG003D0001A
MD_PROG003D0001E
MD_PROG003D0001S01Q
```

Meaning:

- `Q` = Query
- `A` = Create
- `E` = Edit / Load
- `U` = Update
- `D` = Delete
- `S01Q` = SetParam / secondary query screen

## 3. Recommended Program Families

### 3.1 MD_PROG001D - Basic Master Data

This family covers organization and employee master data.

| Program ID | Page Folder | Main Entity | Purpose | Priority |
|---|---|---|---|---|
| `MD_PROG001D0001` | `md_prog001d0001` | `MdOrgUnit` | Organization unit maintenance | High |
| `MD_PROG001D0002` | `md_prog001d0002` | `MdOrgMember` | Organization member maintenance | High |

Recommended frontend page pattern:

- `index.vue`
- `create.vue`
- `edit/[id].vue`
- `config.ts`
- `QueryPageStore.ts`

Recommended backend controllers:

- `MD_PROG001D0001Controller`
- `MD_PROG001D0002Controller`

### 3.2 MD_PROG002D - Formula / Aggregation

This family covers KPI scoring rule setup.

| Program ID | Page Folder | Main Entity | Purpose | Priority |
|---|---|---|---|---|
| `MD_PROG002D0001` | `md_prog002d0001` | `MdFormula` | Formula master maintenance | High |
| `MD_PROG002D0002` | `md_prog002d0002` | `MdAggregationMethod` | Aggregation method maintenance | High |
| `MD_PROG002D0003` | `md_prog002d0003` | `MdFormulaRecommendRule` | Formula auto-selection rule maintenance | High |

Recommended backend controllers:

- `MdPROG002D0001Controller`
- `MdPROG002D0002Controller`
- `MdPROG002D0003Controller`

### 3.3 MD_PROG003D - KPI Master

This family covers KPI definition, owner binding, formula binding, and basic KPI maintenance.

| Program ID | Page Folder | Main Entity | Purpose | Priority |
|---|---|---|---|---|
| `MD_PROG003D0001` | `md_prog003d0001` | `MdKpi` | KPI master maintenance | High |
| `MD_PROG003D0001S01` | `md_prog003d0001/setparam` | `MdKpiOwner` | KPI owner binding | High |

Recommended backend controllers:

- `MdPROG003D0001Controller`

Recommended setparam screens:

- KPI owner
- formula preview / recommendation
- formula selection mode

### 3.4 MD_PROG004D - KPI Measure / Score

This family covers KPI data input and deterministic score calculation.

| Program ID | Page Folder | Main Entity | Purpose | Priority |
|---|---|---|---|---|
| `MD_PROG004D0001` | `md_prog004d0001` | `MdKpiMeasureData` | KPI measure data input and query | High |
| `MD_PROG004D0002` | `md_prog004d0002` | `MdKpiScoreSnapshot` | KPI score snapshot query | High |

Recommended backend controllers:

- `MdPROG004D0001Controller`
- `MdPROG004D0002Controller`

This family should also host calculation APIs:

- score calculation
- recalculation by period
- calculation trace query

### 3.5 MD_PROG005D - KPI Report

This family covers KPI reporting and dashboard display.

| Program ID | Page Folder | Main Entity | Purpose | Priority |
|---|---|---|---|---|
| `MD_PROG005D0001` | `md_prog005d0001` | `MdKpiScoreSnapshot` | KPI report / dashboard | High |

Recommended backend controllers:

- `MdPROG005D0001Controller`

### 3.6 MD_PROG006D - OKR Cycle / Objective

This family covers OKR cycle, objective, key result and check-in.

| Program ID | Page Folder | Main Entity | Purpose | Priority |
|---|---|---|---|---|
| `MD_PROG006D0001` | `md_prog006d0001` | `MdOkrCycle` | OKR cycle maintenance | High |
| `MD_PROG006D0002` | `md_prog006d0002` | `MdOkrObjective` | OKR objective maintenance | High |
| `MD_PROG006D0003` | `md_prog006d0003` | `MdOkrKeyResult` | OKR key result maintenance | High |
| `MD_PROG006D0004` | `md_prog006d0004` | `MdOkrCheckin` | KR check-in / progress update | High |
| `MD_PROG006D0005` | `md_prog006d0005` | `MdOkrSnapshot` | OKR snapshot query | Medium |

Recommended backend controllers:

- `MdPROG006D0001Controller`
- `MdPROG006D0002Controller`
- `MdPROG006D0003Controller`
- `MdPROG006D0004Controller`
- `MdPROG006D0005Controller`

### 3.7 MD_PROG007D - Strategy / BSC

This family covers strategy workspace and weighted alignment.

| Program ID | Page Folder | Main Entity | Purpose | Priority |
|---|---|---|---|---|
| `MD_PROG007D0001` | `md_prog007d0001` | `MdStrategyWorkspace` | Strategy workspace maintenance | Medium |
| `MD_PROG007D0002` | `md_prog007d0002` | `MdStrategyTheme` | Strategy theme / pillar maintenance | Medium |
| `MD_PROG007D0003` | `md_prog007d0003` | `MdStrategyObjective` | Strategy objective maintenance | Medium |
| `MD_PROG007D0004` | `md_prog007d0004` | `MdStrategyObjectiveLink` | KPI / OKR alignment maintenance | Medium |
| `MD_PROG007D0005` | `md_prog007d0005` | `MdStrategySnapshot` | Strategy snapshot query | Medium |

Recommended backend controllers:

- `MdPROG007D0001Controller`
- `MdPROG007D0002Controller`
- `MdPROG007D0003Controller`
- `MdPROG007D0004Controller`
- `MdPROG007D0005Controller`

### 3.8 MD_PROG008D - Action / PDCA

This family covers action plan and follow-up items.

| Program ID | Page Folder | Main Entity | Purpose | Priority |
|---|---|---|---|---|
| `MD_PROG008D0001` | `md_prog008d0001` | `MdActionPlan` | Action plan maintenance | Medium |
| `MD_PROG008D0002` | `md_prog008d0002` | `MdActionItem` | Action item maintenance | Medium |
| `MD_PROG008D0003` | `md_prog008d0003` | `MdActionOwner` | Action owner binding | Medium |
| `MD_PROG008D0004` | `md_prog008d0004` | `MdActionSourceLink` | Action source linkage | Medium |

Recommended backend controllers:

- `MdPROG008D0001Controller`
- `MdPROG008D0002Controller`
- `MdPROG008D0003Controller`
- `MdPROG008D0004Controller`

### 3.9 MD_PROG009D - Dashboard

This family covers operational dashboard and summary page.

| Program ID | Page Folder | Main Entity | Purpose | Priority |
|---|---|---|---|---|
| `MD_PROG009D0001` | `md_prog009d0001` | Mixed | Personal dashboard / management dashboard | Medium |

Recommended backend controllers:

- `MD_PROG009D0001Controller`

### 3.10 MD_PROG010D - Insight / LLM

This family covers signal generation, insight, recommendation and LLM config.

| Program ID | Page Folder | Main Entity | Purpose | Priority |
|---|---|---|---|---|
| `MD_PROG010D0001` | `md_prog010d0001` | `MdPerformanceSignal` | Signal list / generation | Low |
| `MD_PROG010D0002` | `md_prog010d0002` | `MdInterpretationRule` | Rule maintenance | Low |
| `MD_PROG010D0003` | `md_prog010d0003` | `MdInsight` | Insight inbox | Low |
| `MD_PROG010D0004` | `md_prog010d0004` | `MdInsightEvidence` / `MdInsightRecommendation` | Insight detail | Low |
| `MD_PROG010D0005` | `md_prog010d0005` | `MdLlmProviderConfig` / `MdLlmRunLog` | LLM config and audit log | Low |

Recommended backend controllers:

- `MdPROG010D0001Controller`
- `MdPROG010D0002Controller`
- `MdPROG010D0003Controller`
- `MdPROG010D0004Controller`
- `MdPROG010D0005Controller`

## 4. Backend Package Arrangement

建議 backend/app 的 MindScore 業務碼統一放在：

```text
org.qifu.md
```

Suggested subpackages:

```text
org.qifu.md.entity
org.qifu.md.mapper
org.qifu.md.service
org.qifu.md.service.impl
org.qifu.md.api
org.qifu.md.logic
org.qifu.md.logic.impl
org.qifu.md.util
org.qifu.md.vo
```

## 5. Frontend Page Arrangement

建議 frontend-v-nx 依 program folder 建立對應頁面：

```text
pages/md_prog001d0001/
pages/md_prog001d0002/
pages/md_prog002d0001/
pages/md_prog002d0002/
pages/md_prog002d0003/
pages/md_prog003d0001/
pages/md_prog004d0001/
pages/md_prog004d0002/
pages/md_prog005d0001/
pages/md_prog006d0001/
pages/md_prog006d0002/
pages/md_prog006d0003/
pages/md_prog006d0004/
pages/md_prog006d0005/
pages/md_prog007d0001/
pages/md_prog007d0002/
pages/md_prog007d0003/
pages/md_prog007d0004/
pages/md_prog007d0005/
pages/md_prog008d0001/
pages/md_prog008d0002/
pages/md_prog008d0003/
pages/md_prog008d0004/
pages/md_prog009d0001/
pages/md_prog010d0001/
pages/md_prog010d0002/
pages/md_prog010d0003/
pages/md_prog010d0004/
pages/md_prog010d0005/
```

Each page folder should follow the standard files:

- `index.vue`
- `create.vue`
- `edit/[id].vue`
- `config.ts`
- `QueryPageStore.ts`

If a program only supports query mode, then only keep:

- `index.vue`
- `config.ts`
- `QueryPageStore.ts`

## 6. Program Registration Rule

新增程式時，建議同步準備以下資料：

1. `TB_SYS_PROG` folder item
2. `TB_SYS_PROG` query item
3. create / edit / setparam item if needed
4. role permission mapping
5. frontend config `QueryId / CreateId / EditId / SetParamId`
6. backend controller `@ControllerMethodAuthority(programId = "...")`

Example pattern:

```text
Folder:  MD_PROG003D
Query:   MD_PROG003D0001Q
Create:  MD_PROG003D0001A
Edit:    MD_PROG003D0001E
SetParam: MD_PROG003D0001S01Q
```

## 7. Recommended Development Start Order

依目前 implementation plan，建議先做以下順序：

1. `MD_PROG001D` 基本資料
2. `MD_PROG002D` 計算規則
3. `MD_PROG003D` KPI 主檔
4. `MD_PROG004D` KPI measure / score
5. `MD_PROG005D` KPI report
6. `MD_PROG006D` OKR cycle / objective
7. `MD_PROG007D` Strategy / BSC
8. `MD_PROG008D` Action / PDCA
9. `MD_PROG009D` Dashboard
10. `MD_PROG010D` Insight / LLM

## 8. Notes

- `CORE_PROG...` should continue to represent platform-level functions.
- MindScore business modules should not reuse `CORE_PROG...` unless the function is truly platform-wide.
- If the project later wants to separate namespaces further, `MD_` at the program level can be kept stable, while frontend paths remain `pages/md_prog...`.
- This arrangement is intentionally aligned with the `md_` table prefix and the current `org.qifu.md` backend package.

## 9. Functional Detail Analysis

以下補上各模組的實作明細。這一層是用來決定 `create / edit / setparam / report / detail` 是否要拆頁，以及每個畫面要放哪些欄位與動作。

### 9.1 MD_PROG001D - Basic Master Data

#### MD_PROG001D0001 - Organization Unit

核心用途：

- 建立組織樹
- 提供 KPI / OKR / Action 的歸屬對象
- 提供查詢條件中的 `org` 維度

建議畫面：

- `index.vue`: 組織樹 + 清單
- `create.vue`: 新增組織
- `edit/[id].vue`: 修改組織

建議欄位：

- `ORG_CODE`
- `ORG_NAME`
- `PARENT_OID`
- `ORG_LEVEL`
- `SORT_NO`
- `ENABLED`
- `DESCRIPTION`

關鍵調整點：

- `PARENT_OID` 必須支援樹狀選取
- `ORG_LEVEL` 建議由後端自動推導，避免人工錯誤
- 組織刪除需先檢查是否仍被 KPI / Objective / Action 使用

#### MD_PROG001D0002 - Organization Member

核心用途：

- 綁定 qifu4 `ACCOUNT`
- 定義組織成員與主管角色
- 做 KPI / OKR / Action owner 的候選名單

建議畫面：

- `index.vue`: 組織成員清單
- `create.vue`: 新增成員
- `edit/[id].vue`: 修改成員

建議欄位：

- `ORG_OID`
- `ACCOUNT`
- `DISPLAY_NAME`
- `JOB_TITLE`
- `IS_MANAGER`
- `ENABLED`

關鍵調整點：

- `ACCOUNT` 應做 autocomplete
- `DISPLAY_NAME` 建議可從 qifu4 account 帶入後允許覆寫
- 同一個 `ORG_OID + ACCOUNT` 必須唯一

### 9.2 MD_PROG002D - Formula / Aggregation

#### MD_PROG002D0001 - Formula

核心用途：

- 定義 KPI 的公式
- 支援 built-in / custom / script
- 供 KPI 計分引擎使用

建議畫面：

- `index.vue`: 公式清單
- `create.vue`: 新增公式
- `edit/[id].vue`: 編輯公式

建議欄位：

- `FORMULA_CODE`
- `FORMULA_NAME`
- `FORMULA_TYPE`
- `SCRIPT_TYPE`
- `EXPRESSION`
- `RETURN_TYPE`
- `IS_SYSTEM`
- `IS_RECOMMENDABLE`
- `DESCRIPTION`
- `EXAMPLE_TEXT`

關鍵調整點：

- `EXPRESSION` 不應只接受一種語言，需保留未來擴充空間
- 需支援公式測試 / 預覽，避免使用者盲填

#### MD_PROG002D0002 - Aggregation Method

核心用途：

- 定義 KPI 多筆 measure data 的彙總規則
- 支援平均、加總、最大、最小、筆數等

建議畫面：

- `index.vue`
- `create.vue`
- `edit/[id].vue`

建議欄位：

- `AGGR_CODE`
- `AGGR_NAME`
- `AGGR_TYPE`
- `EXPRESSION`
- `DESCRIPTION`
- `ENABLED`

關鍵調整點：

- 聚合方法應可被 KPI 直接選用
- `EXPRESSION` 必須可在後端測試執行
- 不同 `frequency` 下，聚合規則可能不同，必要時需加註適用條件

#### MD_PROG002D0003 - Formula Recommend Rule

核心用途：

- 依 `management mode / compare mode / data type` 推薦公式
- 降低 KPI 建置門檻

建議畫面：

- `index.vue`
- `create.vue`
- `edit/[id].vue`

建議欄位：

- `RULE_CODE`
- `RULE_NAME`
- `MANAGEMENT_MODE`
- `COMPARE_MODE`
- `PERIOD_TYPE`
- `DATA_TYPE`
- `RECOMMENDED_FORMULA_OID`
- `PRIORITY_NO`
- `IS_DEFAULT`
- `ENABLED`
- `DESCRIPTION`

關鍵調整點：

- 規則查找要支援 priority fallback
- 若沒有命中規則，要回傳預設公式
- `IS_DEFAULT` 只能存在一筆有效預設值

### 9.3 MD_PROG003D - KPI Master

#### MD_PROG003D0001 - KPI Master

核心用途：

- 定義 KPI 主檔
- 綁定 formula / aggregation / owner
- 提供後續 measure data 與 score snapshot 的基礎

建議畫面：

- `index.vue`: KPI 清單與搜尋
- `create.vue`: KPI 建立
- `edit/[id].vue`: KPI 修改
- `setparam/[id].vue`: KPI owner / formula / 進階設定

建議欄位：

- `KPI_CODE`
- `KPI_NAME`
- `DESCRIPTION`
- `UNIT_NAME`
- `DATA_TYPE`
- `PERIOD_TYPE`
- `MANAGEMENT_MODE`
- `COMPARE_MODE`
- `MIN_VALUE`
- `TARGET_VALUE`
- `MAX_VALUE`
- `QUASI_RANGE`
- `SCORE_CAP_MODE`
- `SCORING_POLICY`
- `FORMULA_OID`
- `RECOMMENDED_FORMULA_OID`
- `FORMULA_SELECTION_MODE`
- `AGGR_METHOD_OID`
- `FORMULA_VERSION_NO`
- `WEIGHT_VALUE`
- `ENABLED`

建議 setparam 分頁：

- Owner 綁定
- 公式推薦與選擇
- KPI 進階設定

關鍵調整點：

- `MANAGEMENT_MODE` 改變時要同步影響 `COMPARE_MODE`、`QUASI_RANGE`、推薦公式
- `FORMULA_SELECTION_MODE` 要能區分 auto / manual override / custom
- KPI 刪除前需檢查 measure data / score snapshot / report reference

### 9.4 MD_PROG004D - KPI Measure / Score

#### MD_PROG004D0001 - KPI Measure Data

核心用途：

- 輸入 KPI 的 target / actual
- 依 period bucket 管理資料
- 支援手動輸入與匯入

建議畫面：

- `index.vue`: measure data 清單與查詢
- `create.vue`: 新增 data
- `edit/[id].vue`: 修改 data

建議欄位：

- `KPI_OID`
- `PERIOD_TYPE`
- `PERIOD_KEY`
- `MEASURE_DATE`
- `DATA_FOR_TYPE`
- `ACCOUNT`
- `ORG_OID`
- `TARGET_VALUE`
- `ACTUAL_VALUE`
- `SOURCE_TYPE`
- `SOURCE_REF`
- `EVIDENCE_TEXT`
- `LOCKED`

關鍵調整點：

- `PERIOD_KEY` 必須依 `PERIOD_TYPE` 自動生成
- `DATA_FOR_TYPE` 要支援 GLOBAL / ACCOUNT / ORG
- 若資料已被計分使用，建議透過 `LOCKED` 控制是否可改

#### MD_PROG004D0002 - KPI Score Snapshot

核心用途：

- 儲存 KPI 計分結果
- 保留歷史分數與 trace
- 提供報表快速查詢

建議畫面：

- `index.vue`: snapshot 清單
- 詳細頁可用彈窗或 side panel

建議欄位：

- `KPI_OID`
- `PERIOD_TYPE`
- `PERIOD_KEY`
- `DATA_FOR_TYPE`
- `ACCOUNT`
- `ORG_OID`
- `RAW_TARGET`
- `RAW_ACTUAL`
- `SCORE_VALUE`
- `SCORE_STATUS`
- `FORMULA_OID`
- `FORMULA_VERSION_NO`
- `AGGR_METHOD_OID`
- `CALCULATION_TRACE`
- `CALCULATED_AT`

關鍵調整點：

- snapshot 應以不可手改為原則，必要時只允許重新計算
- `CALCULATION_TRACE` 要能回看公式、來源資料、換算過程
- 報表應優先讀 snapshot，而不是即時計算

### 9.5 MD_PROG005D - KPI Report

#### MD_PROG005D0001 - KPI Report / Dashboard

核心用途：

- 顯示 KPI 分數、趨勢、顏色、目標達成度
- 提供管理者視角的快速洞察

建議畫面：

- `index.vue`: dashboard/report 主頁

建議內容：

- KPI 清單
- gauge / progress
- target vs actual
- trend chart
- color status
- period selector

關鍵調整點：

- 報表頁要支援 period 切換
- 報表頁應允許依 org / account / KPI 類別篩選
- 若未來要做 drill-down，KPI report 應能連到 measure data 與 snapshot

### 9.6 MD_PROG006D - OKR

#### MD_PROG006D0001 - OKR Cycle

核心用途：

- 定義 OKR 週期
- 控制 objective 與 KR 的歸屬區間

建議畫面：

- `index.vue`
- `create.vue`
- `edit/[id].vue`

建議欄位：

- `CYCLE_KEY`
- `CYCLE_TYPE`
- `FREQUENCY`
- `VERSION_NO`
- `STATUS`
- `START_DATE`
- `END_DATE`
- `PUBLISHED_AT`
- `SNAPSHOT_AT`

#### MD_PROG006D0002 - OKR Objective

核心用途：

- 定義 OKR Objective
- 管理進度與信心值

建議畫面：

- `index.vue`
- `create.vue`
- `edit/[id].vue`
- `setparam/[id].vue` 或 detail drawer

建議欄位：

- `CYCLE_OID`
- `OBJ_CODE`
- `OBJ_NAME`
- `DESCRIPTION`
- `START_DATE`
- `END_DATE`
- `PROGRESS_VALUE`
- `CONFIDENCE_VALUE`
- `STATUS`
- `VERSION_NO`

#### MD_PROG006D0003 - OKR Key Result

核心用途：

- 定義 KR
- 設定計算類型與 target

建議畫面：

- `index.vue`
- `create.vue`
- `edit/[id].vue`

建議欄位：

- `OBJECTIVE_OID`
- `KR_CODE`
- `KR_NAME`
- `DESCRIPTION`
- `GP_TYPE`
- `OP_TARGET`
- `TARGET_VALUE`
- `MEASURE_VALUE`
- `PROGRESS_VALUE`
- `SORT_NO`

#### MD_PROG006D0004 - OKR Check-in

核心用途：

- 更新 KR 的進度
- 作為進度計算的輸入點

建議畫面：

- `index.vue`
- `create.vue`
- `edit/[id].vue`

建議欄位：

- `KR_OID`
- `CHECKIN_DATE`
- `CURRENT_VALUE`
- `PROGRESS_VALUE`
- `CONFIDENCE_SCORE`
- `COMMENT_TEXT`

#### MD_PROG006D0005 - OKR Snapshot

核心用途：

- 保存 Objective 的快照
- 供 report 與比較使用

建議畫面：

- `index.vue`

關鍵調整點：

- OKR 不能只看單一 progress，還要保留 check-in 歷史
- Objective / KR 之間要能做 hierarchy drill-down
- 應支援 owner / org 維度查詢

### 9.7 MD_PROG007D - Strategy / BSC

#### MD_PROG007D0001 - Strategy Workspace

核心用途：

- 策略工作區
- 取代 BSC first 的主入口

#### MD_PROG007D0002 - Strategy Theme

核心用途：

- 策略主題 / pillar

#### MD_PROG007D0003 - Strategy Objective

核心用途：

- 策略目標
- 對應 KPI / OKR 的中繼層

#### MD_PROG007D0004 - Strategy Objective Link

核心用途：

- 綁定 KPI / OKR
- 管理權重與排序

#### MD_PROG007D0005 - Strategy Snapshot

核心用途：

- 記錄策略快照
- 供年度 / 季度檢視

關鍵調整點：

- BSC 頁面建議先做查詢與樹狀 drill-down，再做複雜編輯
- `Workspace -> Theme -> Objective -> KPI/OKR` 應是主要檢視路徑
- `Link` 是核心，不要只是展示資料，後續會牽動加權計算

### 9.8 MD_PROG008D - Action / PDCA

#### MD_PROG008D0001 - Action Plan

核心用途：

- 一個改善方案或行動方案
- 可對應 KPI / OKR / Strategy / Insight

#### MD_PROG008D0002 - Action Item

核心用途：

- Action Plan 下的具體行動項
- 支援 P / D / C / A 階段

#### MD_PROG008D0003 - Action Owner

核心用途：

- 維護 action 的 owner

#### MD_PROG008D0004 - Action Source Link

核心用途：

- 維護 action 與 KPI / OKR / Strategy / Insight 的來源關聯

關鍵調整點：

- Action 不只是 task，要能回推來源與改善結果
- Action Item 建議支援 parent / dependency
- 若未來要做 gantt chart，`start/end/done` 與 stage 要保留完整

### 9.9 MD_PROG009D - Dashboard

核心用途：

- 個人儀表板
- 管理者總覽
- KPI / OKR / Action 彙總

建議優先顯示：

- 我的 KPI
- 我的 OKR
- 延遲 Action
- 風險 Objective
- 最新 snapshot

關鍵調整點：

- Dashboard 不應直接承擔複雜維護，只做聚合與導流
- 需盡量使用 snapshot / summary 資料，避免每次即時計算過重

### 9.10 MD_PROG010D - Insight / LLM

#### MD_PROG010D0001 - Performance Signal

核心用途：

- 標準化 KPI / OKR / BSC / PDCA 成為 signal

#### MD_PROG010D0002 - Interpretation Rule

核心用途：

- 定義 deterministic 風險規則

#### MD_PROG010D0003 - Insight

核心用途：

- Insight inbox
- 顯示風險、異常、落後、逾期

#### MD_PROG010D0004 - Insight Evidence / Recommendation

核心用途：

- 顯示證據與建議
- 讓使用者知道為什麼系統產生此 insight

#### MD_PROG010D0005 - LLM Provider Config / Run Log

核心用途：

- LLM provider 設定
- 追蹤 prompt / output / token / cost

關鍵調整點：

- LLM 只能解釋與建議，不能成為官方 score 計算者
- Insight 應可手動接受 / 忽略 / 關閉
- 先做規則引擎，再做 LLM，避免一開始就依賴外部模型

## 10. Page Type Decision Rules

以下是決定要不要拆 `create / edit / setparam / detail / report` 的原則：

### 10.1 建議拆 `create` + `edit`

適用於：

- 主檔資料
- 需要人工維護欄位較多的資料

例如：

- KPI
- Formula
- Aggregation Method
- OKR Objective
- Action Plan

### 10.2 建議加 `setparam`

適用於：

- 主檔本體之外，還有子表或參數設定
- 需要第二層維護畫面

例如：

- KPI owner / formula 配置
- Role permission 類型的二階段設定
- Strategy objective link

### 10.3 建議只做 `index`

適用於：

- snapshot
- log
- report
- insight inbox

這類頁面通常以查詢、篩選、drill-down 為主，不適合硬做 create/edit。

### 10.4 建議做 `detail drawer` 或 `detail page`

適用於：

- KPI snapshot 詳細
- OKR objective 詳細
- Insight 詳細
- Action 詳細

原因是這些資料通常包含多層關聯，畫面上需要比單純 edit 更偏查閱。

## 11. Priority Adjustment Notes

如果要先做最小可用版本，實作順序建議再細化為：

1. `MD_PROG001D0001`
2. `MD_PROG001D0002`
3. `MD_PROG002D0001`
4. `MD_PROG002D0002`
5. `MD_PROG002D0003`
6. `MD_PROG003D0001`
7. `MD_PROG004D0001`
8. `MD_PROG004D0002`
9. `MD_PROG005D0001`
10. `MD_PROG006D0001`
11. `MD_PROG006D0002`
12. `MD_PROG006D0003`
13. `MD_PROG006D0004`
14. `MD_PROG006D0005`
15. `MD_PROG008D0001`
16. `MD_PROG008D0002`
17. `MD_PROG009D0001`

Strategy / Insight 可後移，除非你要先驗證完整管理視覺化。
