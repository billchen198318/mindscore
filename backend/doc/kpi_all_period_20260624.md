# KPI 所有週期設定

## 規則

- KPI 主檔 `PERIOD_TYPE` 預設為 `ALL`。
- `ALL` 只表示該 KPI 允許輸入所有實際週期，不是量測資料本身的週期。
- KPI 為 `ALL` 時，Measure Data 可選 `DAY`、`WEEK`、`MONTH`、`QUARTER`、`HALFYEAR` 或 `YEAR`。
- KPI 指定實際週期時，Measure Data 必須使用相同週期；前端鎖定欄位，後端再次驗證。
- Measure Data、Score Snapshot 與報表查詢不得使用 `ALL` 作為實際週期。
- CSV 匯入遵循相同規則。

## 現有資料庫升級

新安裝環境使用更新後的 `mdscore.sql`。現有環境執行：

```sql
ALTER TABLE `md_kpi`
  MODIFY COLUMN `PERIOD_TYPE` VARCHAR(32) NOT NULL DEFAULT 'ALL'
  COMMENT '允許輸入週期 ALL/DAY/WEEK/MONTH/QUARTER/HALFYEAR/YEAR；ALL 代表量測時可選任一實際週期';
```

這個變更只調整欄位預設值，不會修改既有 KPI 的週期。若要讓既有 KPI 接受所有週期，需由使用者在 KPI 主檔逐筆改為 `ALL`，或經確認後執行資料更新。

## 報表語意

報表仍以實際 `PERIOD_TYPE` 查詢與計分。例如同一個 `ALL` KPI 可以有週資料與月資料，但週報查 `WEEK`，月報查 `MONTH`；系統不會把 `ALL` 當成可計算的 period bucket。