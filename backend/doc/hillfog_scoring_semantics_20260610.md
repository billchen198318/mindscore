# hillfog KPI Scoring Semantics Review

Date: 2026-06-10

This document records the recovered meaning of hillfog KPI scoring fields, based on the original code and `hillfog.sql`.

The key conclusion is:

```text
hillfog already designed KPI management mode, compare mode, target / min / max, and quasi range as formula-capable KPI metadata.
However, the seed database only contains one default formula, so the default behavior does not fully demonstrate Bigger / Smaller / Quasi scoring differences.
```

## 1. Relevant Source Files

Main scoring chain:

- `hillfog\core-hillfog\src\org\qifu\hillfog\util\FormulaUtils.java`
- `hillfog\core-hillfog\src\org\qifu\hillfog\util\AggregationMethodUtils.java`
- `hillfog\core-hillfog\src\org\qifu\hillfog\util\AggregationMethod.java`
- `hillfog\core-hillfog\src\org\qifu\hillfog\callable\ScoreCalculationCallable.java`
- `hillfog\core-hillfog\src\org\qifu\hillfog\util\KpiScore.java`
- `hillfog\core-hillfog\src\org\qifu\hillfog\util\BalancedScorecard.java`

KPI code definitions:

- `hillfog\core-hillfog\src\org\qifu\hillfog\model\KpiBasicCode.java`
- `hillfog\core-hillfog\src\org\qifu\hillfog\model\KpiBasicCode.json`
- `hillfog\core-hillfog\src\org\qifu\hillfog\model\FormulaVariable.java`

KPI UI:

- `hillfog\core-app\resources\templates\view\hillfog_kpib\create-page.ftl`
- `hillfog\core-app\resources\templates\view\hillfog_kpib\edit-page.ftl`

Report UI:

- `D:\home\hillfog\core-app\resources\static\js\hillfog\HF_PROG002D0001Q.js`
- `D:\home\hillfog\core-app\resources\static\js\hillfog\HF_PROG005D0001Q.js`

Database seed:

- `hillfog\doc\hillfog.sql`

## 2. Actual Scoring Chain In hillfog

The default KPI scoring flow is:

```text
hf_measure_data.TARGET / ACTUAL
-> hf_formula expression
-> hf_aggregation_method expression
-> ScoreColorUtils
-> KPI report / BSC report / personal dashboard
```

The scoring engine does not hard-code one fixed KPI algorithm. Instead, hillfog stores:

- formula expression in `hf_formula`
- aggregation expression in `hf_aggregation_method`
- KPI metadata in `hf_kpi`
- measure records in `hf_measure_data`

This means the original architecture was formula-driven.

## 3. Default Formula In hillfog.sql

The seed data only contains one formula:

```text
F001 - percent of target
```

Its main logic is equivalent to:

```text
score = measureData.actual / measureData.target * 100
```

The real formula has additional branches for zero and negative values, but the normal case is percent-of-target.

This explains why `management`, `compareType`, and `quasiRange` appear underused in the default project:

```text
They are available to the formula system, but the only seeded formula does not use them.
```

## 4. Formula Variables

`FormulaUtils.getParameter(...)` exposes measure data and KPI metadata to formula scripts.

Explicit formula variables:

| Formula variable | Source |
|---|---|
| `$P{actual}` | `hf_measure_data.ACTUAL` |
| `$P{target}` | `hf_measure_data.TARGET` |
| `$P{kpi.max}` | `hf_kpi.MAX` |
| `$P{kpi.min}` | `hf_kpi.MIN` |
| `$P{kpi.target}` | `hf_kpi.TARGET` |
| `$P{kpi.weight}` | `hf_kpi.WEIGHT` |
| `$P{kpi}` | `hf_kpi.*` |

The full KPI object is also passed into the formula parameter map:

```text
$kpi -> HfKpi object
```

Therefore, a Groovy formula or aggregation expression can theoretically read:

```groovy
$kpi.getManagement()
$kpi.getCompareType()
$kpi.getQuasiRange()
$kpi.getMax()
$kpi.getMin()
$kpi.getTarget()
$kpi.getWeight()
```

The important distinction:

```text
hillfog supports these fields as formula-capable metadata.
hillfog.sql does not provide seeded formulas that fully use those metadata fields.
```

## 5. Target / Min / Max

`hf_kpi` contains:

| Field | Meaning |
|---|---|
| `MAX` | Upper performance reference, worst or best boundary depending on management mode |
| `TARGET` | KPI master target |
| `MIN` | Lower performance reference, minimum acceptable boundary |

`hf_measure_data` also contains:

| Field | Meaning |
|---|---|
| `TARGET` | Period-specific target |
| `ACTUAL` | Period-specific actual result |

So hillfog has two target levels:

| Level | Field | Use |
|---|---|---|
| KPI master | `hf_kpi.TARGET` | KPI definition baseline |
| Measure data | `hf_measure_data.TARGET` | Period-specific calculation baseline |

The seeded `F001` formula mainly uses the measure-data target, not the KPI master target.

This is not a bug. It means period-level targets can vary by month / quarter / year while the KPI master still keeps general target metadata.

## 6. Management Mode

Defined in `KpiBasicCode.java` and `KpiBasicCode.json`.

| Value | Name | Business meaning |
|---|---|---|
| `1` | Bigger is better | Higher actual value is better |
| `2` | Smaller is better | Lower actual value is better |
| `3` | Quasi is better | Actual value should stay near target |

Examples:

| Mode | Example KPI |
|---|---|
| Bigger is better | Revenue, completion rate, customer satisfaction |
| Smaller is better | Cost, defect rate, delay days, turnover rate |
| Quasi is better | Inventory level, budget usage, machine utilization, staffing ratio |

In the current hillfog default implementation, `management` is:

- stored in `hf_kpi`
- validated by `KpiBaseController`
- displayed in KPI and BSC reports
- available to formula / aggregation scripts through the KPI object

But it is not hard-coded into the default Java scoring functions.

## 7. Compare Type

Defined in `KpiBasicCode.java` and `KpiBasicCode.json`.

| Value | Name | Meaning |
|---|---|---|
| `1` | Target | Compare against target |
| `2` | Minimum | Compare against minimum acceptable value |

Recovered design intent:

```text
compareType decides which KPI threshold should be treated as the main comparison baseline.
```

Possible interpretation:

- `Target`: performance is judged against target achievement.
- `Minimum`: performance is judged against minimum acceptable threshold.

In the current default seed formula, this is not used directly. It remains available as KPI metadata for formula or aggregation scripts.

## 8. Quasi Range

Defined in `KpiBasicCode.java`.

Allowed values:

```text
0, 1, 3, 5, 7, 9, 10, 15, 20, 25, 30, 35, 40, 45, 50
```

The UI behavior confirms the intended meaning:

```text
If management == "3" Quasi is better:
    quasiRange is enabled
Else:
    quasiRange is reset to 0 and disabled
```

Recovered business meaning:

```text
quasiRange is the acceptable percentage tolerance around target.
```

Example:

```text
target = 100
quasiRange = 5
acceptable range = 95 to 105
```

For a quasi KPI, both too low and too high are bad. The best value is not maximum or minimum; the best value is near target.

In the current default seed formula, `quasiRange` is not used directly. It remains available through the KPI object.

## 9. Aggregation Method

The default aggregation methods in `hillfog.sql` include:

| ID | Name |
|---|---|
| `AVG_001` | Average |
| `AVG_002` | Average distinct |
| `CNT_001` | Count |
| `CNT_002` | Count distinct |
| `MAX_001` | Max |
| `MIN_001` | Min |
| `SUM_001` | Sum |
| `SUM_002` | Sum distinct |

Each aggregation method is stored as a script expression and calls `AggregationMethod`.

Example pattern:

```groovy
score = AggregationMethod.build().average(kpi, formula, measureDatas);
```

So the default process is:

```text
1. Formula converts each measure data row into a score.
2. Aggregation combines multiple scores into one KPI score.
```

## 10. Score Color

`ScoreColorUtils` maps final score to colors.

Default color ranges are stored in:

```text
D:\home\hillfog\core-hillfog\src\org\qifu\hillfog\util\ScoreColorUtils.json
```

Default score interpretation:

| Score range | General meaning |
|---|---|
| `<= 0` | Very poor |
| `1 - 10` | Very poor |
| `11 - 59` | Poor |
| `60 - 69` | Warning |
| `70 - 79` | Normal |
| `80 - 89` | Good |
| `90+` | Excellent |

BSC report can override colors through scorecard-specific color settings.

Important:

```text
Coloring is based on the final score value, not directly on target / min / max.
```

## 11. Correct Reading Of Original Design

The most accurate interpretation is:

```text
hillfog did not intend to hard-code every KPI scoring rule in Java.
It intended KPI scoring to be formula-driven.
```

The fields:

- `management`
- `compareType`
- `quasiRange`
- `max`
- `target`
- `min`

are best understood as:

```text
formula-capable KPI metadata
```

The current limitation is not the data model. The limitation is seed/template coverage:

```text
Only one default formula was provided, so many KPI modes are not demonstrated out of the box.
```

## 12. MindScore Upgrade Direction

MindScore should keep formula flexibility, but should not require every user to write formulas.

Recommended design:

```text
Built-in deterministic scoring policies
+ formula override
+ AI explanation layer
```

The scoring result must remain deterministic. LLM should explain and recommend, not calculate official KPI score.

## 13. Recommended Built-In Scoring Policies

### 13.1 Bigger Is Better

Use case:

```text
Revenue, completion rate, sales amount, satisfaction score.
```

Simple policy:

```text
if actual >= target:
    score = 100
else if actual <= min:
    score = 0
else:
    score = (actual - min) / (target - min) * 100
```

Optional stretch handling:

```text
if actual > target:
    score may be capped at 100 or allowed to exceed 100.
```

### 13.2 Smaller Is Better

Use case:

```text
Cost, defect count, delay days, error rate.
```

Simple policy:

```text
if actual <= target:
    score = 100
else if actual >= max:
    score = 0
else:
    score = (max - actual) / (max - target) * 100
```

### 13.3 Quasi Is Better

Use case:

```text
Inventory, budget usage, utilization, staffing ratio.
```

Simple policy:

```text
lowerBound = target * (1 - quasiRange / 100)
upperBound = target * (1 + quasiRange / 100)

if actual >= lowerBound and actual <= upperBound:
    score = 100
else if actual < lowerBound:
    score = max(0, 100 - deviationPenalty)
else if actual > upperBound:
    score = max(0, 100 - deviationPenalty)
```

The penalty can be linear:

```text
distance = abs(actual - nearestBound)
score = max(0, 100 - distance / target * 100)
```

Or normalized by min / max:

```text
if actual < lowerBound:
    score = (actual - min) / (lowerBound - min) * 100

if actual > upperBound:
    score = (max - actual) / (max - upperBound) * 100
```

### 13.4 Percent Of Target

This is closest to hillfog `F001`.

```text
score = actual / target * 100
```

This is simple and useful, but it does not express smaller-is-better or quasi-is-better by itself.

### 13.5 Threshold Score

Use case:

```text
Compliance KPI, SLA KPI, pass/fail target.
```

Example:

```text
if actual >= target:
    score = 100
else:
    score = 0
```

### 13.6 Manual Score

Use case:

```text
Qualitative KPI or management review KPI.
```

Official score is entered directly, but still tracked with evidence and approval.

## 14. Recommended MindScore Data Design Impact

MindScore should distinguish:

```text
metric raw value
target/min/max threshold
scoring policy
official score
score explanation
```

Recommended fields in KPI definition:

| Field | Purpose |
|---|---|
| `MANAGEMENT_MODE` | Bigger / smaller / quasi |
| `COMPARE_MODE` | Target / minimum / maximum / custom |
| `SCORING_POLICY` | Built-in policy ID |
| `FORMULA_OID` | Optional custom formula |
| `MIN_VALUE` | Lower boundary |
| `TARGET_VALUE` | Desired target |
| `MAX_VALUE` | Upper boundary |
| `QUASI_RANGE` | Acceptable range percentage |
| `SCORE_CAP_MODE` | Cap at 100 or allow over-achievement |

Recommended fields in score result:

| Field | Purpose |
|---|---|
| `RAW_ACTUAL` | Actual input value |
| `RAW_TARGET` | Period target |
| `SCORE_VALUE` | Official deterministic score |
| `SCORE_STATUS` | GOOD / WARNING / BAD / UNKNOWN |
| `CALCULATION_TRACE` | JSON explanation of calculation |
| `FORMULA_VERSION` | Formula/policy version used |

## 15. Management Mode To Formula Recommendation

MindScore should preserve the hillfog formula architecture, but improve the user experience.

The preferred flow is:

```text
User selects KPI management mode
-> system recommends and auto-selects a suitable formula
-> user can accept or manually choose another formula
-> system records both recommended formula and actual selected formula
```

This keeps two things true at the same time:

```text
1. Normal users do not need to understand formula scripting.
2. Advanced users still keep formula-level flexibility.
```

### 15.1 Suggested UI Behavior

When the user selects a KPI management mode:

| Selected management mode | Auto-selected formula |
|---|---|
| Bigger is better | `BIGGER_IS_BETTER_LINEAR` |
| Smaller is better | `SMALLER_IS_BETTER_LINEAR` |
| Quasi is better | `QUASI_IS_BETTER_RANGE` |
| Percent of target | `PERCENT_OF_TARGET` |
| Threshold / pass-fail | `THRESHOLD_PASS_FAIL` |
| Manual score | `MANUAL_SCORE` |

Example UI flow:

```text
1. User selects "Bigger is better".
2. Formula dropdown automatically selects "BIGGER_IS_BETTER_LINEAR".
3. UI shows formula explanation and sample calculation.
4. User can keep the formula or manually choose another formula.
5. Save action records the recommendation and the final selected formula.
```

The formula field should remain visible and editable, not hidden.

Reason:

```text
Management mode expresses business meaning.
Formula expresses calculation implementation.
```

The system should recommend, not force.

### 15.2 Formula Explanation In UI

The formula dropdown should show a plain-language explanation.

Example for `BIGGER_IS_BETTER_LINEAR`:

```text
Suitable for revenue, completion rate, production volume, satisfaction score.
If actual >= target, score is 100.
If actual <= min, score is 0.
Values between min and target are scored linearly.
```

Example for `SMALLER_IS_BETTER_LINEAR`:

```text
Suitable for cost, defect count, delay days, complaint count.
If actual <= target, score is 100.
If actual >= max, score is 0.
Values between target and max are scored linearly.
```

Example for `QUASI_IS_BETTER_RANGE`:

```text
Suitable for inventory level, budget usage, staffing ratio, utilization rate.
If actual is within target +/- quasi range, score is 100.
If actual is outside the accepted range, score is reduced by deviation.
```

### 15.3 Formula Selection State

MindScore should record whether the formula was accepted from recommendation or manually changed.

Suggested values:

| Value | Meaning |
|---|---|
| `AUTO` | Formula was auto-selected from management mode and accepted |
| `MANUAL_OVERRIDE` | Formula was recommended but user selected another existing formula |
| `CUSTOM` | User selected or created a custom formula |

Suggested KPI fields:

| Field | Purpose |
|---|---|
| `MANAGEMENT_MODE` | Business meaning, such as bigger / smaller / quasi |
| `FORMULA_OID` | Actual formula used for calculation |
| `RECOMMENDED_FORMULA_OID` | Formula recommended by the system |
| `FORMULA_SELECTION_MODE` | `AUTO`, `MANUAL_OVERRIDE`, or `CUSTOM` |

This makes the score auditable:

```text
The KPI was configured as "Bigger is better".
The system recommended "BIGGER_IS_BETTER_LINEAR".
The user actually selected "PERCENT_OF_TARGET".
Formula selection mode is "MANUAL_OVERRIDE".
```

### 15.4 Why This Fits hillfog

This is a direct evolution of the original hillfog design.

hillfog already had:

```text
KPI management metadata
+ formula selection
+ formula variables
+ aggregation method
```

MindScore should not remove that flexibility.

The upgrade is to add:

```text
management-mode-driven formula recommendation
+ built-in formula templates
+ formula explanation
+ formula test panel
+ formula selection audit trail
```

This approach keeps the old architecture while making it usable for normal SaaS users.

## 16. Important Product Decision

For MindScore, do not make LLM the official scoring engine.

Correct split:

```text
Deterministic engine:
    calculates KPI / OKR / BSC score

LLM:
    explains score
    summarizes trend
    finds risks
    recommends actions
    drafts report text
```

This keeps KPI score auditable and enterprise-safe.

## 17. Final Summary

The original hillfog design was stronger than it first appears:

```text
It already had formula-driven KPI scoring, custom aggregation, frequency-based measure data, BSC weighted scoring, OKR progress, PDCA linkage, and report rendering.
```

The confusing part is:

```text
management / compareType / quasiRange were designed as metadata available to formulas,
but hillfog.sql only seeded one percent-of-target formula.
```

Therefore, the right modernization path is not to discard the idea. The right path is:

```text
1. Preserve formula-driven extensibility.
2. Add built-in scoring policy templates.
3. Make management / compare / quasi rules explicit and testable.
4. Store calculation trace for every official score.
5. Use LLM only for interpretation, not official calculation.
```
