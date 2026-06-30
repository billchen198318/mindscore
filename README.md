# MindScore

MindScore 是一套整合 KPI、OKR、Strategy / BSC、Action / PDCA 與管理儀表板的績效管理與營運洞察系統。

系統以可信任的結構化資料與確定性計算為核心，先建立組織、指標、目標、策略與行動的管理流程，再逐步延伸到 Insight / LLM 輔助分析與建議。

<img src="https://raw.githubusercontent.com/billchen198318/mindscore/main/backend/doc/0003.png">
<br>

## 系統定位

MindScore 用來協助組織回答幾個關鍵問題：

- 目前 KPI 表現如何？
- OKR 目標與 Key Result 是否如期推進？
- 策略 / BSC 是否與 KPI、OKR 對齊？
- 異常或落後項目是否已轉成 Action / PDCA 持續追蹤？
- 管理者是否能在同一個儀表板看到跨領域風險與待處理事項？

## 主要功能

### 基本資料

- 組織單位管理
- 組織成員管理
- 提供 KPI、OKR、Action、Dashboard 使用的組織與人員資料

### 計算規則

- Formula 公式管理
- Aggregation Method 彙總方式管理
- Formula Recommend Rule 公式推薦規則管理
- 支援 KPI 分數計算與評分規則設定

### KPI 管理

- KPI 主檔管理
- KPI Owner 綁定，可指定組織或人員
- KPI 公式與彙總方式設定
- KPI Measure Data 資料輸入
- KPI Score Snapshot 查詢
- KPI Report / Dashboard，提供分數、趨勢、目標與實績、計算來源與追蹤資訊

### OKR 管理

- OKR Cycle 管理
- Objective 管理
- Key Result 管理
- Check-in / Progress 更新
- OKR Snapshot 查詢
- OKR Report，檢視 Objective、Key Result、Initiative、進度、信心分數與狀態

### Strategy / BSC

- Strategy Workspace 管理
- Strategy Theme 管理
- Strategy Objective 管理
- Strategy Objective 與 KPI / OKR 的連結
- Strategy Snapshot Report，保存策略報表與評分紀錄

### Action / PDCA

- Action Plan 管理
- Action Item 管理
- 負責人與來源連結管理
- PDCA 階段、進度、開始日、到期日、完成日與狀態追蹤
- Action / PDCA Report，檢視逾期項目、階段分布、完成狀況、負責人負載與來源覆蓋情況

### 管理儀表板

`MD_PROG009D0001` 是整合式管理儀表板，集中呈現 KPI、OKR、Strategy 與 Action / PDCA 的管理資訊。

目前包含以下視圖：

- Overview / Personal Dashboard
- Organization Dashboard
- Scorecard / Strategy Report
- Delayed Action View
- At-risk Objective View

儀表板可協助管理者快速掌握跨領域風險，例如 KPI 警示、OKR 風險目標、策略分數偏低、逾期 Action 等。

### Insight / LLM

Insight / LLM 是 MindScore 的解釋與建議層，用於：

- Performance Signal 產生
- Interpretation Rule 解讀規則
- Insight 清單與明細
- Recommendation 建議
- 從 Insight 建立 Action
- LLM Provider 設定，支援 OpenAI 與 Gemini
- LLM Run Log 稽核紀錄

## 程式模組

| 模組 | 選單名稱 | 說明 |
|---|---|---|
| `MD_PROG001D` | `AA. 基本資料` | 組織單位與組織成員 |
| `MD_PROG002D` | `AB. 計算規則` | 公式、彙總方式、公式推薦規則 |
| `MD_PROG003D` | `AC. KPI` | KPI 主檔與 Owner 綁定 |
| `MD_PROG004D` | `AD. KPI Measure data` | KPI 資料輸入與分數快照 |
| `MD_PROG005D` | `AE. KPI Report` | KPI 報表與 Dashboard |
| `MD_PROG006D` | `AF. OKR` | OKR Cycle、Objective、Key Result、Check-in、Snapshot、Report |
| `MD_PROG007D` | `AG. Strategy / BSC` | 策略工作區、主題、目標、連結與策略快照報表 |
| `MD_PROG008D` | `AH. Action / PDCA` | Action Plan、Action Item 與 Action / PDCA Report |
| `MD_PROG009D` | `AI. Dashboard` | 跨 KPI / OKR / Strategy / Action 的管理儀表板 |
| `MD_PROG010D` | Insight / LLM | Signal, Rule, Insight, Evidence, Recommendation, LLM and Action follow-up |

## 專案結構

```text
backend/app
  後端應用程式

frontend-v-nx
  前端應用程式

backend/doc
  DDL、功能規劃與程式安排文件
```

## 資料庫密碼加密

資料庫連線設定位於
`backend/app/src/main/resources/db1-config.properties`。`db1.datasource.password`
可使用明文，也可使用 Jasypt 的 `ENC(...)` 加密格式。

### 使用 DatabasePasswordJasyptTest

可使用
`backend/app/src/test/java/org/qifu/test/DatabasePasswordJasyptTest.java`
產生加密後的資料庫密碼：

1. 在 `testEncryptAndDecryptDatabasePassword()` 中，將 `rawPassword` 改成實際的資料庫密碼。
2. 將 `encryptorPassword` 改成應用程式使用的 Jasypt 密鑰。本機使用預設設定時，密鑰為 `mindscore-dev-jasypt-key`。
3. 以 JUnit 執行 `testEncryptAndDecryptDatabasePassword()`。
4. 從 Console 找到 `cipherText>>>` 後面的密文，包成 `ENC(...)` 後寫入 `db1-config.properties`：

```properties
db1.datasource.password=ENC(cipherText 輸出的密文)
```

### 使用 Jasypt 1.9.3 CLI

也可以將 `jasypt-1.9.3.jar` 放在目前目錄，然後於 PowerShell 執行：

```powershell
java -cp "jasypt-1.9.3.jar" `
  org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI `
  input="mariadb-password" `
  password="mindscore-dev-jasypt-key" `
  algorithm="PBEWithMD5AndDES" `
  ivGeneratorClassName="org.jasypt.iv.NoIvGenerator" `
  verbose=false
```

CLI 只會輸出密文本身，寫入 `db1-config.properties` 前需自行加上 `ENC(...)`：

```properties
db1.datasource.password=ENC(CLI 輸出的密文)
```

加密使用的密鑰必須與 `JASYPT_ENCRYPTOR_PASSWORD` 完全一致。若未設定此環境變數，
`application.properties` 的本機預設密鑰為 `mindscore-dev-jasypt-key`。生產環境應設定自己的
`JASYPT_ENCRYPTOR_PASSWORD`，並以相同密鑰重新產生密文；否則應用程式啟動時將無法解密及綁定
`db1.datasource.password`。

## 目前狀態

目前主要功能已推進到 Phase 14 Insight / LLM：

- 基本資料
- 計算規則
- KPI 主檔、資料輸入、分數快照與報表
- OKR Cycle、Objective、Key Result、Check-in、Snapshot 與 Report
- Strategy / BSC 維護與策略快照報表
- Action / PDCA 維護與報表
- 跨領域管理儀表板
- Insight / LLM
- Insight Evidence / Recommendation
- LLM Recommendation generation
- Recommendation to Action follow-up

Phase 14 已完成。

## 專案來源

MindScore 基於 Qifu 應用架構開發，並延伸自：

- https://github.com/billchen198318/hillfog
- https://github.com/billchen198318/qifu4
