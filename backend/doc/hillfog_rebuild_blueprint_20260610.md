# Hillfog / MindScore 改版實作藍圖

目的：
- 把現有 `hillfog` 的 KPI / OKR / BSC / PDCA 能力保留
- 用 `qifu4` 的新架構重做成 API-first + Nuxt 3 的現代化系統
- 讓資料模型、模組邊界、DDL、遷移順序可以直接進入開發

---

## 1. 改版目標

### 1.1 產品目標

新的系統不是單純績效管理工具，而是：

- Enterprise Performance Intelligence Platform
- MindScore
- 以 KPI 為事實、OKR 為意圖、BSC 為策略視角、PDCA 為執行閉環

### 1.2 技術目標

- 前後端分離
- API-first
- 模組化
- 可測試
- 可版本化
- 可追蹤計算過程
- 可做 snapshot / history / comparison

### 1.3 保留與重做

保留：

- KPI 計算規則
- OKR progress 規則
- BSC 加權規則
- PDCA 結構
- 組織圖與個人 OKR 視圖

重做：

- FreeMarker 頁面
- jQuery inline script
- controller 直接拼畫面
- report page 直接組 DOM
- 舊式 menu/page 依賴

---

## 2. 新架構分層

依 `qifu4` 風格，建議拆成以下層次：

### 2.1 `backend/base`

放共用基礎能力：

Provided by `qifu4` and reused as-is:

- response model
- exception
- validation
- security helpers
- common constants
- common util
- audit helpers

### 2.2 qifu4 core / base projects

放 MindScore 的核心領域：

- `core-base`
- `core-standard`
- `core-std`
- `core-lib`
- `backend/core`
- These layers are reused as platform dependencies.
- MindScore / hillfog business code must not be added here.

### 2.3 `backend/app`

放應用入口與 API：

Application layer on top of qifu4 base services:

- MindScore REST controllers
- MindScore services
- MindScore domain logic
- KPI / OKR / BSC / PDCA calculation
- report and snapshot services
- qifu4 authentication integration
- qifu4 authorization integration
- request/response mapping
- swagger/openapi
- application config

### 2.4 `frontend-v-nx`

放 Nuxt 3 前端：

- dashboard
- report pages
- maintenance pages
- org chart
- charts
- dialogs
- forms
- insights

### 2.5 Development boundary

MindScore is built on top of qifu4. New business code must stay out of the qifu4 platform layers.

- Backend MindScore code goes under `backend/app`
- Frontend MindScore code goes under `frontend-v-nx`
- `core-base`, `core-standard`, `core-std`, `core-lib`, and `backend/core` are reused as platform dependencies
- JWT, httpOnly cookie handling, base security, role permission, menu, program configuration, upload, token, and common BaseService behavior are provided by qifu4
- Do not add KPI / OKR / BSC / PDCA / report / insight business code into the base or core projects

---

## 3. 模組拆分設計

### 3.1 Domain 模組

建議拆成以下 domain package：

- `organization`
- `employee`
- `metric`
- `okr`
- `scorecard`
- `pdca`
- `reporting`
- `insight`
- `graph`

### 3.2 Service 邊界

#### Identity

Handled by qifu4 base tables and security config.

- user / role / permission
- login session / token

#### Organization

- org unit
- org tree
- employee-org relation
- employee hierarchy

#### Metric

- KPI master
- formula
- aggregation method
- measure data
- scoring

#### OKR

- objective
- key result
- initiative
- KR value
- progress calculation

#### Scorecard

- vision
- perspective
- strategy objective
- KPI / OKR link
- score color

#### PDCA

- PDCA main
- PDCA item
- owner
- attachment
- close request

#### Reporting

- KPI report
- OKR report
- scorecard report
- org chart report
- export

#### Insight

- summary
- anomaly
- root cause
- recommendation
- action suggestion

---

## 4. 概念資料模型

### 4.1 KPI

KPI 需要保留：

- master definition
- formula
- aggregation method
- target / min / max
- management mode
- compare mode
- owners
- measure data
- date range score

### 4.2 OKR

OKR 需要保留：

- objective
- key result
- initiative
- KR values
- progress
- owner
- org owner
- cycle

### 4.3 BSC

BSC 需要保留：

- scorecard vision
- perspective
- strategy objective
- weighted KPI
- linked OKR
- score color

### 4.4 PDCA

PDCA 需要保留：

- main task
- P / D / C / A items
- owner
- attachments
- close request

### 4.5 Versioning / Snapshot

現代化後應補強：

- cycle
- version
- snapshot
- published / draft / archived
- history compare

---

## 5. DDL 設計原則

### 5.1 命名

- table name 統一 `md_` 前綴
- 主鍵使用 `OID CHAR(36)`
- `code` 表示業務代碼
- `name` 表示顯示名稱

### 5.2 共用欄位

建議每張表都保留：

- `OID`
- `TENANT_OID`
- `CODE`
- `NAME`
- `STATUS`
- `DESCRIPTION`
- `CUSERID`
- `CDATE`
- `UUSERID`
- `UDATE`
- `IS_DELETED`

### 5.3 資料庫

以下 DDL 以 MariaDB / MySQL 8 為目標。

---

## 6. 基礎與權限表 DDL

```

---

## 7. 組織與員工 DDL

```sql
CREATE TABLE md_org_unit (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  ORG_CODE VARCHAR(64) NOT NULL,
  ORG_NAME VARCHAR(200) NOT NULL,
  PARENT_OID CHAR(36) NULL,
  ORG_TYPE VARCHAR(32) NOT NULL DEFAULT 'DEPARTMENT',
  SORT_NO INT NOT NULL DEFAULT 0,
  STATUS VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  DESCRIPTION VARCHAR(2000) NULL,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_ORG_UNIT_TENANT_CODE (TENANT_OID, ORG_CODE),
  KEY IDX_MD_ORG_UNIT_PARENT (PARENT_OID),
  KEY IDX_MD_ORG_UNIT_TENANT (TENANT_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_employee (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  EMP_CODE VARCHAR(64) NOT NULL,
  ACCOUNT VARCHAR(64) NOT NULL,
  NAME VARCHAR(200) NOT NULL,
  JOB_TITLE VARCHAR(200) NULL,
  EMAIL VARCHAR(200) NULL,
  AVATAR_OID CHAR(36) NULL,
  STATUS VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_EMPLOYEE_TENANT_EMP_CODE (TENANT_OID, EMP_CODE),
  UNIQUE KEY UK_MD_EMPLOYEE_TENANT_ACCOUNT (TENANT_OID, ACCOUNT),
  KEY IDX_MD_EMPLOYEE_TENANT (TENANT_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_employee_org (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  EMPLOYEE_OID CHAR(36) NOT NULL,
  ORG_OID CHAR(36) NOT NULL,
  PRIMARY_FLAG TINYINT NOT NULL DEFAULT 0,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_EMPLOYEE_ORG (TENANT_OID, EMPLOYEE_OID, ORG_OID),
  KEY IDX_MD_EMPLOYEE_ORG_EMP (EMPLOYEE_OID),
  KEY IDX_MD_EMPLOYEE_ORG_ORG (ORG_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_employee_hier (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  EMPLOYEE_OID CHAR(36) NOT NULL,
  PARENT_EMPLOYEE_OID CHAR(36) NOT NULL,
  SORT_NO INT NOT NULL DEFAULT 0,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_EMPLOYEE_HIER (TENANT_OID, EMPLOYEE_OID, PARENT_EMPLOYEE_OID),
  KEY IDX_MD_EMPLOYEE_HIER_EMP (EMPLOYEE_OID),
  KEY IDX_MD_EMPLOYEE_HIER_PARENT (PARENT_EMPLOYEE_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 8. 計算與配置 DDL

```sql
CREATE TABLE md_formula (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  CODE VARCHAR(64) NOT NULL,
  NAME VARCHAR(200) NOT NULL,
  TYPE VARCHAR(32) NOT NULL,
  RETURN_MODE VARCHAR(32) NOT NULL DEFAULT 'DEFAULT',
  RETURN_VAR VARCHAR(64) NULL,
  EXPRESSION TEXT NOT NULL,
  DESCRIPTION VARCHAR(2000) NULL,
  STATUS VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_FORMULA_TENANT_CODE (TENANT_OID, CODE)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_aggregation_method (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  CODE VARCHAR(64) NOT NULL,
  NAME VARCHAR(200) NOT NULL,
  METHOD_TYPE VARCHAR(32) NOT NULL,
  EXPRESSION TEXT NULL,
  DATE_RANGE_FLAG TINYINT NOT NULL DEFAULT 0,
  DESCRIPTION VARCHAR(2000) NULL,
  STATUS VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_AGGR_TENANT_CODE (TENANT_OID, CODE)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 9. KPI DDL

```sql
CREATE TABLE md_kpi (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  KPI_CODE VARCHAR(64) NOT NULL,
  KPI_NAME VARCHAR(200) NOT NULL,
  DESCRIPTION VARCHAR(2000) NULL,
  UNIT VARCHAR(64) NULL,
  FORMULA_OID CHAR(36) NOT NULL,
  AGGREGATION_METHOD_OID CHAR(36) NOT NULL,
  MANAGEMENT_MODE VARCHAR(32) NOT NULL,
  COMPARE_MODE VARCHAR(32) NOT NULL,
  DATA_TYPE VARCHAR(32) NOT NULL,
  FREQUENCY VARCHAR(32) NOT NULL,
  QUASI_RANGE INT NULL,
  MAX_VALUE DECIMAL(18,4) NULL,
  TARGET_VALUE DECIMAL(18,4) NULL,
  MIN_VALUE DECIMAL(18,4) NULL,
  STATUS VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  VERSION_NO INT NOT NULL DEFAULT 1,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_KPI_TENANT_CODE (TENANT_OID, KPI_CODE),
  KEY IDX_MD_KPI_FORMULA (FORMULA_OID),
  KEY IDX_MD_KPI_AGGR (AGGREGATION_METHOD_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_kpi_owner_user (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  KPI_OID CHAR(36) NOT NULL,
  USER_OID CHAR(36) NOT NULL,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_KPI_OWNER_USER (TENANT_OID, KPI_OID, USER_OID),
  KEY IDX_MD_KPI_OWNER_USER_KPI (KPI_OID),
  KEY IDX_MD_KPI_OWNER_USER_USER (USER_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_kpi_owner_org (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  KPI_OID CHAR(36) NOT NULL,
  ORG_OID CHAR(36) NOT NULL,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_KPI_OWNER_ORG (TENANT_OID, KPI_OID, ORG_OID),
  KEY IDX_MD_KPI_OWNER_ORG_KPI (KPI_OID),
  KEY IDX_MD_KPI_OWNER_ORG_ORG (ORG_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_measure_data (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  KPI_OID CHAR(36) NOT NULL,
  FREQUENCY VARCHAR(32) NOT NULL,
  MEASURE_DATE DATE NOT NULL,
  BUCKET_DATE VARCHAR(16) NOT NULL,
  DATA_FOR VARCHAR(32) NOT NULL,
  ACCOUNT VARCHAR(64) NOT NULL DEFAULT '*',
  ORG_CODE VARCHAR(64) NOT NULL DEFAULT '*',
  TARGET_VALUE DECIMAL(18,4) NULL,
  ACTUAL_VALUE DECIMAL(18,4) NULL,
  SOURCE_TYPE VARCHAR(32) NULL,
  SOURCE_REF VARCHAR(128) NULL,
  NOTE VARCHAR(2000) NULL,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_MEASURE_DATA (TENANT_OID, KPI_OID, FREQUENCY, BUCKET_DATE, DATA_FOR, ACCOUNT, ORG_CODE),
  KEY IDX_MD_MEASURE_DATA_KPI (KPI_OID),
  KEY IDX_MD_MEASURE_DATA_BUCKET (BUCKET_DATE),
  KEY IDX_MD_MEASURE_DATA_DATE (MEASURE_DATE)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_kpi_score_snapshot (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  KPI_OID CHAR(36) NOT NULL,
  CYCLE_OID CHAR(36) NULL,
  FREQUENCY VARCHAR(32) NOT NULL,
  START_DATE DATE NOT NULL,
  END_DATE DATE NOT NULL,
  SCORE_VALUE DECIMAL(18,4) NOT NULL,
  FONT_COLOR VARCHAR(32) NULL,
  BG_COLOR VARCHAR(32) NULL,
  CALCULATION_TRACE JSON NULL,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  KEY IDX_MD_KPI_SCORE_SNAPSHOT_KPI (KPI_OID),
  KEY IDX_MD_KPI_SCORE_SNAPSHOT_CYCLE (CYCLE_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 10. OKR DDL

```sql
CREATE TABLE md_okr_cycle (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  CYCLE_KEY VARCHAR(64) NOT NULL,
  CYCLE_TYPE VARCHAR(32) NOT NULL DEFAULT 'QUARTER',
  FREQUENCY VARCHAR(32) NOT NULL,
  VERSION_NO INT NOT NULL DEFAULT 1,
  STATUS VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
  START_DATE DATE NOT NULL,
  END_DATE DATE NOT NULL,
  PUBLISHED_AT DATETIME NULL,
  SNAPSHOT_AT DATETIME NULL,
  DESCRIPTION VARCHAR(2000) NULL,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_OKR_CYCLE (TENANT_OID, CYCLE_KEY, VERSION_NO),
  KEY IDX_MD_OKR_CYCLE_DATE (START_DATE, END_DATE)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_okr_objective (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  CYCLE_OID CHAR(36) NOT NULL,
  OBJ_CODE VARCHAR(64) NOT NULL,
  OBJ_NAME VARCHAR(200) NOT NULL,
  DESCRIPTION VARCHAR(4000) NULL,
  START_DATE DATE NOT NULL,
  END_DATE DATE NOT NULL,
  PROGRESS_VALUE DECIMAL(18,4) NOT NULL DEFAULT 0,
  CONFIDENCE_VALUE DECIMAL(18,4) NULL,
  STATUS VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  VERSION_NO INT NOT NULL DEFAULT 1,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_OKR_OBJECTIVE (TENANT_OID, CYCLE_OID, OBJ_CODE),
  KEY IDX_MD_OKR_OBJECTIVE_CYCLE (CYCLE_OID),
  KEY IDX_MD_OKR_OBJECTIVE_DATE (START_DATE, END_DATE)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_okr_objective_owner_user (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  OBJECTIVE_OID CHAR(36) NOT NULL,
  USER_OID CHAR(36) NOT NULL,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_OKR_OBJECTIVE_OWNER_USER (TENANT_OID, OBJECTIVE_OID, USER_OID),
  KEY IDX_MD_OKR_OBJECTIVE_OWNER_USER_OBJ (OBJECTIVE_OID),
  KEY IDX_MD_OKR_OBJECTIVE_OWNER_USER_USER (USER_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_okr_objective_owner_org (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  OBJECTIVE_OID CHAR(36) NOT NULL,
  ORG_OID CHAR(36) NOT NULL,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_OKR_OBJECTIVE_OWNER_ORG (TENANT_OID, OBJECTIVE_OID, ORG_OID),
  KEY IDX_MD_OKR_OBJECTIVE_OWNER_ORG_OBJ (OBJECTIVE_OID),
  KEY IDX_MD_OKR_OBJECTIVE_OWNER_ORG_ORG (ORG_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_okr_key_result (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  OBJECTIVE_OID CHAR(36) NOT NULL,
  KR_CODE VARCHAR(64) NOT NULL,
  KR_NAME VARCHAR(200) NOT NULL,
  DESCRIPTION VARCHAR(4000) NULL,
  GP_TYPE VARCHAR(32) NOT NULL,
  OP_TARGET VARCHAR(32) NOT NULL,
  TARGET_VALUE DECIMAL(18,4) NOT NULL,
  MEASURE_VALUE DECIMAL(18,4) NOT NULL DEFAULT 0,
  PROGRESS_VALUE DECIMAL(18,4) NOT NULL DEFAULT 0,
  SORT_NO INT NOT NULL DEFAULT 0,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_OKR_KEY_RESULT (TENANT_OID, OBJECTIVE_OID, KR_CODE),
  KEY IDX_MD_OKR_KEY_RESULT_OBJ (OBJECTIVE_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_okr_key_result_value (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  KEY_RESULT_OID CHAR(36) NOT NULL,
  VALUE_DATE DATE NOT NULL,
  VALUE_NO DECIMAL(18,4) NOT NULL,
  NOTE VARCHAR(2000) NULL,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_OKR_KEY_RESULT_VALUE (TENANT_OID, KEY_RESULT_OID, VALUE_DATE),
  KEY IDX_MD_OKR_KEY_RESULT_VALUE_KR (KEY_RESULT_OID),
  KEY IDX_MD_OKR_KEY_RESULT_VALUE_DATE (VALUE_DATE)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_okr_initiative (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  OBJECTIVE_OID CHAR(36) NOT NULL,
  INITIATIVE_CODE VARCHAR(64) NOT NULL,
  INITIATIVE_NAME VARCHAR(200) NOT NULL,
  CONTENT VARCHAR(4000) NULL,
  SORT_NO INT NOT NULL DEFAULT 0,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_OKR_INITIATIVE (TENANT_OID, OBJECTIVE_OID, INITIATIVE_CODE),
  KEY IDX_MD_OKR_INITIATIVE_OBJ (OBJECTIVE_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_okr_snapshot (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  OBJECTIVE_OID CHAR(36) NOT NULL,
  CYCLE_OID CHAR(36) NULL,
  SNAPSHOT_AT DATETIME NOT NULL,
  PROGRESS_VALUE DECIMAL(18,4) NOT NULL,
  KEY_RESULT_COUNT INT NOT NULL DEFAULT 0,
  INITIATIVE_COUNT INT NOT NULL DEFAULT 0,
  CALCULATION_TRACE JSON NULL,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  KEY IDX_MD_OKR_SNAPSHOT_OBJ (OBJECTIVE_OID),
  KEY IDX_MD_OKR_SNAPSHOT_CYCLE (CYCLE_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 11. BSC / Scorecard DDL

```sql
CREATE TABLE md_scorecard (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  SC_CODE VARCHAR(64) NOT NULL,
  SC_NAME VARCHAR(200) NOT NULL,
  CONTENT VARCHAR(4000) NULL,
  MISSION VARCHAR(4000) NULL,
  STATUS VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  VERSION_NO INT NOT NULL DEFAULT 1,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_SCORECARD (TENANT_OID, SC_CODE)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_scorecard_cycle (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  SCORECARD_OID CHAR(36) NOT NULL,
  CYCLE_OID CHAR(36) NOT NULL,
  SNAPSHOT_AT DATETIME NULL,
  STATUS VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_SCORECARD_CYCLE (TENANT_OID, SCORECARD_OID, CYCLE_OID),
  KEY IDX_MD_SCORECARD_CYCLE_SCORECARD (SCORECARD_OID),
  KEY IDX_MD_SCORECARD_CYCLE_CYCLE (CYCLE_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_perspective (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  SCORECARD_OID CHAR(36) NOT NULL,
  PERSPECTIVE_CODE VARCHAR(64) NOT NULL,
  PERSPECTIVE_NAME VARCHAR(200) NOT NULL,
  WEIGHT_VALUE DECIMAL(18,4) NOT NULL DEFAULT 0,
  SORT_NO INT NOT NULL DEFAULT 0,
  STATUS VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_PERSPECTIVE (TENANT_OID, SCORECARD_OID, PERSPECTIVE_CODE),
  KEY IDX_MD_PERSPECTIVE_SCORECARD (SCORECARD_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_strategy_objective (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  PERSPECTIVE_OID CHAR(36) NOT NULL,
  SO_CODE VARCHAR(64) NOT NULL,
  SO_NAME VARCHAR(200) NOT NULL,
  WEIGHT_VALUE DECIMAL(18,4) NOT NULL DEFAULT 0,
  SORT_NO INT NOT NULL DEFAULT 0,
  STATUS VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_STRATEGY_OBJECTIVE (TENANT_OID, PERSPECTIVE_OID, SO_CODE),
  KEY IDX_MD_STRATEGY_OBJECTIVE_PERSPECTIVE (PERSPECTIVE_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_strategy_objective_kpi (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  STRATEGY_OBJECTIVE_OID CHAR(36) NOT NULL,
  KPI_OID CHAR(36) NOT NULL,
  CARD_WEIGHT DECIMAL(18,4) NOT NULL DEFAULT 0,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_SO_KPI (TENANT_OID, STRATEGY_OBJECTIVE_OID, KPI_OID),
  KEY IDX_MD_SO_KPI_SO (STRATEGY_OBJECTIVE_OID),
  KEY IDX_MD_SO_KPI_KPI (KPI_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_strategy_objective_okr (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  STRATEGY_OBJECTIVE_OID CHAR(36) NOT NULL,
  OBJECTIVE_OID CHAR(36) NOT NULL,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_SO_OKR (TENANT_OID, STRATEGY_OBJECTIVE_OID, OBJECTIVE_OID),
  KEY IDX_MD_SO_OKR_SO (STRATEGY_OBJECTIVE_OID),
  KEY IDX_MD_SO_OKR_OBJ (OBJECTIVE_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_scorecard_color (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  SCORECARD_OID CHAR(36) NOT NULL,
  COLOR_CODE VARCHAR(64) NOT NULL,
  SCORE_MIN DECIMAL(18,4) NOT NULL,
  SCORE_MAX DECIMAL(18,4) NOT NULL,
  FONT_COLOR VARCHAR(32) NOT NULL,
  BG_COLOR VARCHAR(32) NOT NULL,
  SORT_NO INT NOT NULL DEFAULT 0,
  IS_DEFAULT TINYINT NOT NULL DEFAULT 0,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  KEY IDX_MD_SCORECARD_COLOR_SCORECARD (SCORECARD_OID),
  UNIQUE KEY UK_MD_SCORECARD_COLOR (TENANT_OID, SCORECARD_OID, COLOR_CODE)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_scorecard_snapshot (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  SCORECARD_OID CHAR(36) NOT NULL,
  CYCLE_OID CHAR(36) NULL,
  SNAPSHOT_AT DATETIME NOT NULL,
  SCORE_VALUE DECIMAL(18,4) NOT NULL,
  PERSPECTIVE_COUNT INT NOT NULL DEFAULT 0,
  STRATEGY_OBJECTIVE_COUNT INT NOT NULL DEFAULT 0,
  KPI_COUNT INT NOT NULL DEFAULT 0,
  OKR_COUNT INT NOT NULL DEFAULT 0,
  CALCULATION_TRACE JSON NULL,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  KEY IDX_MD_SCORECARD_SNAPSHOT_SCORECARD (SCORECARD_OID),
  KEY IDX_MD_SCORECARD_SNAPSHOT_CYCLE (CYCLE_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 12. PDCA DDL

```sql
CREATE TABLE md_pdca (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  PDCA_NO VARCHAR(64) NOT NULL,
  MASTER_TYPE VARCHAR(32) NOT NULL,
  MASTER_OID CHAR(36) NOT NULL,
  NAME VARCHAR(200) NOT NULL,
  DESCRIPTION VARCHAR(4000) NULL,
  START_DATE DATE NOT NULL,
  END_DATE DATE NOT NULL,
  CONFIRM_DATE DATE NULL,
  STATUS VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_PDCA (TENANT_OID, PDCA_NO),
  KEY IDX_MD_PDCA_MASTER (MASTER_TYPE, MASTER_OID),
  KEY IDX_MD_PDCA_DATE (START_DATE, END_DATE)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_pdca_owner (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  PDCA_OID CHAR(36) NOT NULL,
  USER_OID CHAR(36) NOT NULL,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_PDCA_OWNER (TENANT_OID, PDCA_OID, USER_OID),
  KEY IDX_MD_PDCA_OWNER_PDCA (PDCA_OID),
  KEY IDX_MD_PDCA_OWNER_USER (USER_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_pdca_item (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  PDCA_OID CHAR(36) NOT NULL,
  PARENT_OID CHAR(36) NULL,
  STAGE_CODE VARCHAR(8) NOT NULL,
  ITEM_NAME VARCHAR(200) NOT NULL,
  DESCRIPTION VARCHAR(4000) NULL,
  START_DATE DATE NOT NULL,
  END_DATE DATE NOT NULL,
  STATUS VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  SORT_NO INT NOT NULL DEFAULT 0,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  KEY IDX_MD_PDCA_ITEM_PDCA (PDCA_OID),
  KEY IDX_MD_PDCA_ITEM_PARENT (PARENT_OID),
  KEY IDX_MD_PDCA_ITEM_STAGE (STAGE_CODE)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_pdca_item_owner (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  PDCA_ITEM_OID CHAR(36) NOT NULL,
  USER_OID CHAR(36) NOT NULL,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_PDCA_ITEM_OWNER (TENANT_OID, PDCA_ITEM_OID, USER_OID),
  KEY IDX_MD_PDCA_ITEM_OWNER_ITEM (PDCA_ITEM_OID),
  KEY IDX_MD_PDCA_ITEM_OWNER_USER (USER_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_pdca_attachment (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  PDCA_OID CHAR(36) NOT NULL,
  UPLOAD_OID CHAR(36) NOT NULL,
  FILE_NAME VARCHAR(255) NOT NULL,
  FILE_TYPE VARCHAR(64) NULL,
  FILE_SIZE BIGINT NULL,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  KEY IDX_MD_PDCA_ATTACHMENT_PDCA (PDCA_OID),
  KEY IDX_MD_PDCA_ATTACHMENT_UPLOAD (UPLOAD_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_pdca_close_request (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  PDCA_OID CHAR(36) NOT NULL,
  REQUEST_NO VARCHAR(64) NOT NULL,
  REQUEST_NOTE VARCHAR(4000) NULL,
  REQUEST_STATUS VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
  REQUESTED_AT DATETIME NULL,
  APPROVED_AT DATETIME NULL,
  APPROVED_BY VARCHAR(64) NULL,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_PDCA_CLOSE_REQUEST (TENANT_OID, REQUEST_NO),
  KEY IDX_MD_PDCA_CLOSE_REQUEST_PDCA (PDCA_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 13. Graph / Insight / Audit DDL

```sql
CREATE TABLE md_dependency_edge (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  SOURCE_TYPE VARCHAR(32) NOT NULL,
  SOURCE_OID CHAR(36) NOT NULL,
  TARGET_TYPE VARCHAR(32) NOT NULL,
  TARGET_OID CHAR(36) NOT NULL,
  RELATION_TYPE VARCHAR(32) NOT NULL,
  WEIGHT_VALUE DECIMAL(18,4) NOT NULL DEFAULT 0,
  STATUS VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  KEY IDX_MD_DEPENDENCY_SOURCE (SOURCE_TYPE, SOURCE_OID),
  KEY IDX_MD_DEPENDENCY_TARGET (TARGET_TYPE, TARGET_OID),
  KEY IDX_MD_DEPENDENCY_RELATION (RELATION_TYPE)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_insight_report (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  REPORT_TYPE VARCHAR(32) NOT NULL,
  REF_OID CHAR(36) NULL,
  REF_TYPE VARCHAR(32) NULL,
  TITLE VARCHAR(200) NOT NULL,
  SUMMARY_TEXT VARCHAR(4000) NULL,
  INSIGHT_JSON JSON NULL,
  ACTION_JSON JSON NULL,
  GENERATED_AT DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  GENERATED_BY VARCHAR(64) NULL,
  STATUS VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  KEY IDX_MD_INSIGHT_REPORT_TYPE (REPORT_TYPE),
  KEY IDX_MD_INSIGHT_REPORT_REF (REF_TYPE, REF_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_audit_log (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  MODULE_NAME VARCHAR(64) NOT NULL,
  ENTITY_NAME VARCHAR(64) NOT NULL,
  ENTITY_OID CHAR(36) NOT NULL,
  ACTION_TYPE VARCHAR(32) NOT NULL,
  BEFORE_JSON JSON NULL,
  AFTER_JSON JSON NULL,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (OID),
  KEY IDX_MD_AUDIT_LOG_ENTITY (ENTITY_NAME, ENTITY_OID),
  KEY IDX_MD_AUDIT_LOG_MODULE (MODULE_NAME)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 14. 維護建議與實作順序

### Phase 1. 建基礎

- qifu4 base auth / menu / permission tables are reused
- `md_org_unit`
- `md_employee`

### Phase 2. 核心績效引擎

- `md_formula`
- `md_aggregation_method`
- `md_kpi`
- `md_measure_data`
- `md_okr_objective`
- `md_okr_key_result`

### Phase 3. 管理視圖

- `md_scorecard`
- `md_perspective`
- `md_strategy_objective`
- `md_scorecard_color`
- `md_pdca`

### Phase 4. 版本與洞察

- `md_okr_cycle`
- `md_scorecard_cycle`
- `md_kpi_score_snapshot`
- `md_okr_snapshot`
- `md_scorecard_snapshot`
- `md_dependency_edge`
- `md_insight_report`

---

## 15. 遷移策略

### 15.1 舊 hillfog -> 新模型

建議先做 mapping：

- `hf_kpi` -> `md_kpi`
- `hf_measure_data` -> `md_measure_data`
- `hf_objective` -> `md_okr_objective`
- `hf_key_res` -> `md_okr_key_result`
- `hf_key_res_val` -> `md_okr_key_result_value`
- `hf_initiatives` -> `md_okr_initiative`
- `hf_scorecard` -> `md_scorecard`
- `hf_perspective` -> `md_perspective`
- `hf_strategy_objective` -> `md_strategy_objective`
- `hf_pdca` -> `md_pdca`
- `hf_pdca_item` -> `md_pdca_item`

### 15.2 遷移方式

建議採雙軌：

- 先同步舊資料到新 schema
- 前端逐步切新 API
- 舊畫面作為 fallback
- 報表先對齊數字，再做 UI 重做

---

## 16. 主要風險

- 舊資料的語意不一定完全一致，尤其 OKR 與 BSC 之間的關聯
- KPI 計算結果要做數值驗證，避免遷移後分數不同
- measure data 的時間 bucket 必須完全一致
- PDCA 與 OKR / KPI 的 master reference 需要定義清楚
- 多租戶是否正式啟用，會影響所有唯一鍵與查詢策略

---

## 17. 結論

這份藍圖的核心原則是：

- 不是把 hillfog 原封不動搬到新架構
- 而是保留領域能力，重做產品形態
- 先保資料模型，再做 API，再做 Nuxt 3 UI
- 先把 deterministic engine 做穩，再補 insight / AI

如果照這個方向走，hillfog 可以升級成真正可維護、可擴充、可版本化的現代化績效平台。

---

## 18. Upgrade Outcome

After modernization, MindScore should evolve from an old-style performance management system into an Enterprise Performance Intelligence Platform.

The most important change is not only UI improvement. The system role changes from:

`Input KPI / OKR data -> calculate score -> show report`

to:

`Track strategy, objectives, indicators, actions, and organizational alignment -> explain deviation -> suggest next actions`

### 18.1 Before and after

| Area | Original hillfog | Modernized MindScore |
|---|---|---|
| System style | FreeMarker + jQuery server-side management system | qifu4 + API + Nuxt 3 frontend |
| Platform base | legacy qifu3 / local jar / old base | qifu4 JWT httpOnly / permission / menu / BaseService |
| KPI | formula, aggregation, input, report | calculation core plus snapshot, versioning, trend, anomaly analysis |
| OKR | objective, key result, initiative, progress | cycle, alignment, dependency, confidence, progress insight |
| BSC | scorecard, perspective, strategy objective, KPI weight | strategy map, versioning, monthly / quarterly snapshot |
| PDCA | task, item, Gantt, close request | action loop connected with KPI / OKR / BSC insight |
| Report | static query report | dashboard, trend comparison, drill-down, snapshot comparison |
| Org chart | personal OKR view | organization performance map across department, manager, employee, and alignment |
| Permission | legacy permission handling | qifu4 menu / role / program permission |
| Frontend | mixed forms and page scripts | Nuxt 3 operational workspace |
| Insight | none | risk warning, root cause, recommended action, management summary |

### 18.2 KPI upgrade

KPI should not only be a score.

It should support:

- lag / lead indicator classification
- trend detection
- target deviation detection
- anomaly flagging
- relation to strategy objective
- relation to PDCA improvement actions
- snapshot comparison by month / quarter / year

### 18.3 OKR upgrade

OKR should not only be a progress percentage.

It should support:

- cycle management
- objective and key result alignment
- confidence tracking
- initiative effectiveness
- dependency graph
- owner and organization view
- blocked objective detection
- progress explanation

### 18.4 BSC upgrade

BSC should become the strategy map of the system.

The structure should support:

- Vision
- Perspective
- Strategy Objective
- KPI
- OKR
- PDCA action
- weighted score
- score color
- monthly / quarterly strategy snapshot

The key change is drill-down:

`Scorecard -> Perspective -> Strategy Objective -> KPI / OKR -> PDCA`

### 18.5 PDCA upgrade

PDCA should become the execution feedback loop.

Instead of only showing tasks, it should connect:

- KPI issue
- OKR risk
- BSC strategy objective
- action owner
- execution status
- close request
- improvement result

This turns PDCA from task tracking into measurable improvement tracking.

### 18.6 Report upgrade

Reports should become a management workspace.

Expected views:

- current month performance
- current quarter performance
- red / yellow / green indicators
- delayed PDCA actions
- at-risk OKR objectives
- department comparison
- personal OKR alignment
- scorecard snapshot comparison
- target vs actual trend

### 18.7 Practical priority

Do not make AI / insight too heavy in the first phase.

The first phase should stabilize:

- KPI / OKR / BSC / PDCA data model
- API
- calculation engine
- snapshot
- report query
- Nuxt 3 operational UI

Insight should consume stable data after the deterministic engine is reliable.

---

## 19. Open Source Performance Intelligence Scope

MindScore should not try to become a closed enterprise SaaS suite in the first open source version.

The practical open source positioning is:

- self-hosted by Docker / Makefile
- qifu4 provides auth, role permission, menu, program config, upload, token, and BaseService
- MindScore provides KPI / OKR / BSC / PDCA / report / insight business modules
- LLM is optional and configured by the user with their own API key
- data source integration is API-first, not hard-coded vendor integration
- collaboration can be added later after the core performance intelligence loop is stable

### 19.1 SaaS collaboration boundary

SaaS collaboration is not the first priority.

The first open source version can provide:

- Docker deployment
- Makefile commands
- basic user / role / menu through qifu4
- report sharing by URL or permission
- insight status workflow
- action owner and due date

Advanced collaboration can be postponed:

- comment
- mention
- team workspace
- approval workflow
- notification center
- real-time collaboration

### 19.2 Data source and connector boundary

Data sources should enter the system through import APIs.

The first version does not need built-in ERP / CRM / GitHub / Jira / HR connectors. It only needs stable APIs that external jobs can call.

Target import fields:

- KPI target value
- KPI actual value
- KPI measure date
- KPI frequency bucket
- OKR key result target
- OKR key result actual / measure value
- OKR progress update
- note / source reference

Suggested APIs:

```text
POST /api/mindscore/import/kpi-measure-data
POST /api/mindscore/import/okr-key-result-value
POST /api/mindscore/import/bulk
GET  /api/mindscore/import/logs
```

### 19.3 LLM / AI Agent boundary

LLM should read already generated performance data. It should not calculate the official score.

The user provides their own API key.

LLM can consume:

- KPI score
- KPI description
- KPI target / actual / trend
- OKR score / progress
- OKR objective and key result description
- BSC scorecard report
- PDCA action status
- generated report summary

LLM should produce:

- management summary
- plain-language explanation
- possible root cause
- recommendation wording
- risk brief
- action suggestion

LLM should not own:

- KPI formula result
- BSC weight calculation
- OKR progress calculation
- rule-based status decision
- official audit result

### 19.4 Performance Signal

Performance Intelligence starts by standardizing the result of KPI / OKR / BSC / PDCA into signals.

Each signal should include:

- source type: KPI / OKR / BSC / PDCA
- source OID
- period
- owner
- score
- status
- trend
- variance
- risk level
- related objective
- related PDCA
- evidence payload
- explanation input

This makes dashboards, rules, insight, and LLM prompts consume the same performance facts.

### 19.5 Interpretation Rule

Before using LLM, the system should use deterministic interpretation rules.

Examples:

- KPI actual is below target by more than 10 percent -> warning
- KPI declines for 3 continuous periods -> trend risk
- OKR progress is lower than elapsed time ratio -> behind schedule
- key result has not been updated for N days -> stale
- BSC perspective weighted score is below threshold -> strategy risk
- PDCA item is overdue and not closed -> execution risk

These rules can be stored in DB or configuration and executed by the backend application service.

### 19.6 Insight Object

Insight should be a trackable object, not only generated text.

Insight fields should include:

- insight type: KPI_RISK / OKR_DELAY / BSC_IMBALANCE / PDCA_OVERDUE
- severity: LOW / MEDIUM / HIGH
- source type
- source OID
- title
- summary
- evidence
- recommendation
- status: OPEN / ACCEPTED / DISMISSED / RESOLVED
- owner
- due date
- generated by rule or LLM

### 19.7 Action Execution Layer

Action Execution Layer means turning insight into trackable work.

The first version can be simple:

`insight -> create PDCA / initiative / task -> track result -> close insight`

Initial actions:

- create PDCA from insight
- create OKR initiative from insight
- create follow-up task
- assign owner
- set due date
- complete action
- compare next KPI / OKR result

Future extensions:

- create Jira issue
- send Slack alert
- send email
- call webhook
- trigger workflow

### 19.8 Feedback Loop

The core loop should be:

`KPI / OKR / BSC score -> Performance Signal -> Rule Insight -> LLM Summary / Recommendation -> Action -> PDCA / Initiative -> Next Score Comparison -> Resolved or Still Risky`

This is the main difference between a reporting system and a performance intelligence system.

### 19.9 Minimum DDL extension

Suggested additional tables:

```sql
CREATE TABLE md_performance_signal (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  SIGNAL_TYPE VARCHAR(32) NOT NULL,
  SOURCE_TYPE VARCHAR(32) NOT NULL,
  SOURCE_OID CHAR(36) NOT NULL,
  SOURCE_CODE VARCHAR(64) NULL,
  SOURCE_NAME VARCHAR(200) NULL,
  PERIOD_TYPE VARCHAR(32) NOT NULL,
  PERIOD_KEY VARCHAR(32) NOT NULL,
  START_DATE DATE NOT NULL,
  END_DATE DATE NOT NULL,
  OWNER_ACCOUNT VARCHAR(64) NULL,
  ORG_CODE VARCHAR(64) NULL,
  SCORE_VALUE DECIMAL(18,4) NULL,
  TARGET_VALUE DECIMAL(18,4) NULL,
  ACTUAL_VALUE DECIMAL(18,4) NULL,
  VARIANCE_VALUE DECIMAL(18,4) NULL,
  VARIANCE_RATE DECIMAL(18,4) NULL,
  TREND_CODE VARCHAR(32) NULL,
  STATUS_CODE VARCHAR(32) NOT NULL,
  RISK_LEVEL VARCHAR(32) NOT NULL DEFAULT 'LOW',
  RELATED_OBJECTIVE_OID CHAR(36) NULL,
  RELATED_PDCA_OID CHAR(36) NULL,
  SNAPSHOT_OID CHAR(36) NULL,
  EVIDENCE_JSON JSON NULL,
  EXPLANATION_INPUT TEXT NULL,
  GENERATED_AT DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  KEY IDX_MD_SIGNAL_SOURCE (SOURCE_TYPE, SOURCE_OID),
  KEY IDX_MD_SIGNAL_PERIOD (PERIOD_TYPE, PERIOD_KEY),
  KEY IDX_MD_SIGNAL_RISK (RISK_LEVEL, STATUS_CODE),
  KEY IDX_MD_SIGNAL_OWNER (OWNER_ACCOUNT)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_interpretation_rule (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  RULE_CODE VARCHAR(64) NOT NULL,
  RULE_NAME VARCHAR(200) NOT NULL,
  RULE_TYPE VARCHAR(32) NOT NULL,
  SOURCE_TYPE VARCHAR(32) NOT NULL,
  CONDITION_EXPR TEXT NOT NULL,
  ACTION_EXPR TEXT NULL,
  SEVERITY VARCHAR(32) NOT NULL DEFAULT 'MEDIUM',
  ENABLED_FLAG VARCHAR(1) NOT NULL DEFAULT 'Y',
  PRIORITY_NO INT NOT NULL DEFAULT 0,
  DESCRIPTION VARCHAR(2000) NULL,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_RULE_CODE (TENANT_OID, RULE_CODE),
  KEY IDX_MD_RULE_SOURCE (SOURCE_TYPE, ENABLED_FLAG)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_insight (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  INSIGHT_NO VARCHAR(64) NOT NULL,
  INSIGHT_TYPE VARCHAR(32) NOT NULL,
  SEVERITY VARCHAR(32) NOT NULL DEFAULT 'MEDIUM',
  SOURCE_TYPE VARCHAR(32) NOT NULL,
  SOURCE_OID CHAR(36) NOT NULL,
  SIGNAL_OID CHAR(36) NULL,
  RULE_OID CHAR(36) NULL,
  TITLE VARCHAR(200) NOT NULL,
  SUMMARY_TEXT VARCHAR(4000) NULL,
  STATUS VARCHAR(32) NOT NULL DEFAULT 'OPEN',
  OWNER_ACCOUNT VARCHAR(64) NULL,
  DUE_DATE DATE NULL,
  GENERATED_BY_TYPE VARCHAR(32) NOT NULL DEFAULT 'RULE',
  GENERATED_AT DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ACCEPTED_AT DATETIME NULL,
  DISMISSED_AT DATETIME NULL,
  RESOLVED_AT DATETIME NULL,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_INSIGHT_NO (TENANT_OID, INSIGHT_NO),
  KEY IDX_MD_INSIGHT_SOURCE (SOURCE_TYPE, SOURCE_OID),
  KEY IDX_MD_INSIGHT_STATUS (STATUS, SEVERITY),
  KEY IDX_MD_INSIGHT_OWNER (OWNER_ACCOUNT)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_insight_evidence (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  INSIGHT_OID CHAR(36) NOT NULL,
  EVIDENCE_TYPE VARCHAR(32) NOT NULL,
  SOURCE_TYPE VARCHAR(32) NULL,
  SOURCE_OID CHAR(36) NULL,
  LABEL VARCHAR(200) NOT NULL,
  VALUE_TEXT VARCHAR(2000) NULL,
  VALUE_NO DECIMAL(18,4) NULL,
  EVIDENCE_JSON JSON NULL,
  SORT_NO INT NOT NULL DEFAULT 0,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  KEY IDX_MD_INSIGHT_EVIDENCE_INSIGHT (INSIGHT_OID),
  KEY IDX_MD_INSIGHT_EVIDENCE_SOURCE (SOURCE_TYPE, SOURCE_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_insight_recommendation (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  INSIGHT_OID CHAR(36) NOT NULL,
  RECOMMENDATION_TYPE VARCHAR(32) NOT NULL,
  TITLE VARCHAR(200) NOT NULL,
  CONTENT_TEXT VARCHAR(4000) NULL,
  PRIORITY_NO INT NOT NULL DEFAULT 0,
  STATUS VARCHAR(32) NOT NULL DEFAULT 'OPEN',
  ACCEPTED_FLAG VARCHAR(1) NOT NULL DEFAULT 'N',
  ACTION_CREATED_FLAG VARCHAR(1) NOT NULL DEFAULT 'N',
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  KEY IDX_MD_RECOMMENDATION_INSIGHT (INSIGHT_OID),
  KEY IDX_MD_RECOMMENDATION_STATUS (STATUS, ACCEPTED_FLAG)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_action_item (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  ACTION_NO VARCHAR(64) NOT NULL,
  ACTION_TYPE VARCHAR(32) NOT NULL,
  TITLE VARCHAR(200) NOT NULL,
  CONTENT_TEXT VARCHAR(4000) NULL,
  OWNER_ACCOUNT VARCHAR(64) NULL,
  ORG_CODE VARCHAR(64) NULL,
  STATUS VARCHAR(32) NOT NULL DEFAULT 'OPEN',
  PRIORITY_CODE VARCHAR(32) NOT NULL DEFAULT 'MEDIUM',
  START_DATE DATE NULL,
  DUE_DATE DATE NULL,
  COMPLETED_AT DATETIME NULL,
  RESULT_NOTE VARCHAR(4000) NULL,
  RESULT_SCORE_DELTA DECIMAL(18,4) NULL,
  CREATED_FROM_INSIGHT_OID CHAR(36) NULL,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_ACTION_NO (TENANT_OID, ACTION_NO),
  KEY IDX_MD_ACTION_OWNER (OWNER_ACCOUNT),
  KEY IDX_MD_ACTION_STATUS (STATUS, PRIORITY_CODE),
  KEY IDX_MD_ACTION_INSIGHT (CREATED_FROM_INSIGHT_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_action_link (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  ACTION_OID CHAR(36) NOT NULL,
  REF_TYPE VARCHAR(32) NOT NULL,
  REF_OID CHAR(36) NOT NULL,
  LINK_TYPE VARCHAR(32) NOT NULL DEFAULT 'RELATED',
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_ACTION_LINK (TENANT_OID, ACTION_OID, REF_TYPE, REF_OID, LINK_TYPE),
  KEY IDX_MD_ACTION_LINK_ACTION (ACTION_OID),
  KEY IDX_MD_ACTION_LINK_REF (REF_TYPE, REF_OID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_llm_provider_config (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  PROVIDER_CODE VARCHAR(64) NOT NULL,
  PROVIDER_NAME VARCHAR(200) NOT NULL,
  BASE_URL VARCHAR(500) NULL,
  MODEL_NAME VARCHAR(128) NOT NULL,
  API_KEY_REF VARCHAR(255) NOT NULL,
  API_KEY_MASKED VARCHAR(128) NULL,
  ENABLED_FLAG VARCHAR(1) NOT NULL DEFAULT 'Y',
  DEFAULT_FLAG VARCHAR(1) NOT NULL DEFAULT 'N',
  CONFIG_JSON JSON NULL,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UUSERID VARCHAR(64) NULL,
  UDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  IS_DELETED TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (OID),
  UNIQUE KEY UK_MD_LLM_PROVIDER (TENANT_OID, PROVIDER_CODE),
  KEY IDX_MD_LLM_PROVIDER_ENABLED (ENABLED_FLAG, DEFAULT_FLAG)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE md_llm_run_log (
  OID CHAR(36) NOT NULL,
  TENANT_OID CHAR(36) NOT NULL,
  RUN_TYPE VARCHAR(32) NOT NULL,
  PROVIDER_OID CHAR(36) NOT NULL,
  MODEL_NAME VARCHAR(128) NOT NULL,
  REF_TYPE VARCHAR(32) NULL,
  REF_OID CHAR(36) NULL,
  INSIGHT_OID CHAR(36) NULL,
  PROMPT_HASH VARCHAR(128) NULL,
  PROMPT_TEXT MEDIUMTEXT NULL,
  INPUT_JSON JSON NULL,
  OUTPUT_TEXT MEDIUMTEXT NULL,
  OUTPUT_JSON JSON NULL,
  TOKEN_INPUT INT NULL,
  TOKEN_OUTPUT INT NULL,
  COST_VALUE DECIMAL(18,6) NULL,
  STATUS VARCHAR(32) NOT NULL DEFAULT 'SUCCESS',
  ERROR_MESSAGE VARCHAR(4000) NULL,
  STARTED_AT DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FINISHED_AT DATETIME NULL,
  CUSERID VARCHAR(64) NULL,
  CDATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (OID),
  KEY IDX_MD_LLM_RUN_PROVIDER (PROVIDER_OID),
  KEY IDX_MD_LLM_RUN_REF (REF_TYPE, REF_OID),
  KEY IDX_MD_LLM_RUN_INSIGHT (INSIGHT_OID),
  KEY IDX_MD_LLM_RUN_STATUS (STATUS, STARTED_AT)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

Purpose:

- `md_performance_signal`: normalized KPI / OKR / BSC / PDCA signal
- `md_interpretation_rule`: deterministic rule definition
- `md_insight`: generated and trackable insight
- `md_insight_evidence`: data that supports the insight
- `md_insight_recommendation`: suggested next step
- `md_action_item`: work item created from insight
- `md_action_link`: relation between action and KPI / OKR / BSC / PDCA / insight
- `md_llm_provider_config`: user-managed LLM provider and encrypted API key reference
- `md_llm_run_log`: prompt / model / usage / output audit log

Key field notes:

- `md_performance_signal.SIGNAL_TYPE`: score / trend / variance / stale / overdue style signal category.
- `md_performance_signal.SOURCE_TYPE` and `SOURCE_OID`: points to KPI / OKR / BSC / PDCA source data.
- `md_performance_signal.PERIOD_TYPE` and `PERIOD_KEY`: keeps day / week / month / quarter / year bucket information.
- `md_performance_signal.EVIDENCE_JSON`: stores normalized evidence used by rule engine and LLM prompt.
- `md_interpretation_rule.CONDITION_EXPR`: deterministic condition expression, such as variance rate threshold or stale update rule.
- `md_interpretation_rule.ACTION_EXPR`: optional rule output mapping, such as insight type, severity, or recommendation template.
- `md_insight.GENERATED_BY_TYPE`: identifies whether the insight came from rule, LLM, or manual creation.
- `md_insight.STATUS`: tracks insight lifecycle from OPEN to ACCEPTED / DISMISSED / RESOLVED.
- `md_insight_evidence`: stores multiple evidence rows for one insight, so the UI can explain why the insight exists.
- `md_insight_recommendation`: stores recommended actions separately from insight summary, so users can accept or ignore suggestions.
- `md_action_item`: tracks actual follow-up work created from insight.
- `md_action_link`: links an action back to KPI / OKR / BSC / PDCA / insight without hard-coding one parent type.
- `md_llm_provider_config.API_KEY_REF`: stores encrypted key reference or secret reference, not plain API key.
- `md_llm_run_log`: keeps prompt, input, output, token usage, status, and error detail for audit and debugging.

### 19.10 Minimum API extension

Suggested APIs:

```text
POST /api/mindscore/signals/generate
GET  /api/mindscore/signals

POST /api/mindscore/insights/generate
GET  /api/mindscore/insights
GET  /api/mindscore/insights/{oid}
POST /api/mindscore/insights/{oid}/dismiss
POST /api/mindscore/insights/{oid}/resolve

POST /api/mindscore/insights/{oid}/recommendations/generate

POST /api/mindscore/actions
POST /api/mindscore/actions/from-insight/{insightOid}
GET  /api/mindscore/actions
POST /api/mindscore/actions/{oid}/complete

POST /api/mindscore/llm/config
POST /api/mindscore/llm/test
POST /api/mindscore/llm/summarize-report
```

### 19.11 Minimum UI extension

The first Performance Intelligence UI can be limited to three screens:

- Insight Inbox
- Insight Detail
- Action Board

Insight Inbox shows risks, anomalies, delays, and recommendations.

Insight Detail shows evidence, related KPI / OKR / BSC / PDCA, LLM summary, recommendation, and action creation.

Action Board tracks PDCA / initiative / task items generated from insights and whether the next score improved.

## Appendix: hillfog Scoring Semantics

The recovered details for `compareType`, `management`, `target / min / max`, `quasiRange`, formula variables, and the gap between the original metadata design and the seeded default formula are documented separately:

- `hillfog_scoring_semantics_20260610.md`

This is intentionally separated from the rebuild blueprint because it is a focused scoring-engine reference and should become the basis for MindScore deterministic scoring policy implementation.

## Appendix: hillfog Domain Positioning

The domain-only evaluation of hillfog, including why MindScore should not be positioned as a BSC-first product and how BSC / PDCA should be reframed for modern SaaS usage, is documented separately:

- `hillfog_domain_positioning_20260610.md`

## Appendix: MindScore Business DDL

The standalone business logic DDL, including KPI formula auto-selection fields and Chinese column descriptions, is documented separately:

- `mindscore_business_ddl_20260610.md`
