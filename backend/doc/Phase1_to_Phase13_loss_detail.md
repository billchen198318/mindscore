# Phase 1 to Phase 13 Gap Detail

## 1. Purpose

This document records the implementation gaps found while comparing:

- `mindscore_implementation_plan_20260611.md`
- Backend code under `backend/app/src/main/java/org/qifu/md`
- Frontend code under `frontend-v-nx/pages/md_prog*`
- Frontend Axios calls and backend controller mappings

This is a code-level review. It does not represent browser, database, or end-to-end acceptance testing.

## 2. Summary

| Phase | Result | Confirmed gap |
|---|---|---|
| Phase 1 | No explicit gap found | None found during this review |
| Phase 2 | No explicit gap found | None found during this review |
| Phase 3 | No explicit gap found | None found during this review |
| Phase 4 | Completed in code | CSV template download, validation preview, and atomic UPSERT import are implemented |
| Phase 5 | No explicit gap found | None found during this review |
| Phase 6 | Completed in code | Calculation trace and configured score color are displayed by the KPI report frontend |
| Phase 7 | Completed in code | OKR cycle status transition rules are enforced in backend and constrained in frontend |
| Phase 8 | No explicit gap found | None found during this review |
| Phase 9 | No explicit gap found | None found during this review |
| Phase 10 | No explicit gap found | None found during this review |
| Phase 11 | No explicit gap found | None found during this review |
| Phase 12 | Partial | Source-driven Action creation from report pages is missing |
| Phase 13 | No explicit gap found | None found during this review |

`No explicit gap found` means the planned frontend page, backend implementation, and API connection were found in code. It does not mean the Phase has passed runtime acceptance testing.

## 3. Phase 4 - KPI Measure Data Import

### Resolution status

Resolved in code on 2026-06-22.

### Existing implementation

- Measure Data query, load, create/update, and delete are available.
- KPI, organization, and account dimensions are supported.
- `DAY`, `WEEK`, `MONTH`, `QUARTER`, `HALFYEAR`, and `YEAR` period rules are implemented.
- The frontend contains `IMPORT` as a Measure Data source type.

### Implemented resolution

- A downloadable UTF-8 BOM CSV example is provided by the backend.
- The Measure Data page provides `Download CSV Example` and `Import CSV` actions.
- CSV upload is limited to `.csv`, 2 MB, and 1,000 data rows.
- Preview resolves `kpi_code`, `org_code`, and `account` without exposing database OIDs.
- Preview validates enabled master data, KPI period type, period key format, dimensions, numeric precision, note length, duplicate natural keys, and locked existing data.
- Each row is classified as `INSERT`, `UPDATE`, or invalid before confirmation.
- Confirmation revalidates all submitted raw fields on the backend instead of trusting preview flags or resolved IDs from the client.
- Import uses the existing natural-key `saveOrUpdate` behavior, writes `SOURCE_TYPE=IMPORT`, and stores the CSV filename in `SOURCE_REF`.
- The confirmed batch is atomic; any validation or persistence error rolls back the entire import.

### CSV format

```csv
kpi_code,period_type,period_key,data_for_type,org_code,account,target_value,actual_value,note
KPI_CODE_HERE,MONTH,2026-06,GLOBAL,,,1000000,980000,Global monthly example
KPI_CODE_HERE,MONTH,2026-06,ORG,ORG_CODE_HERE,,100,92,Organization example
KPI_CODE_HERE,WEEK,2026-W25,ACCOUNT,,ACCOUNT_HERE,100,88,Account example
```

### Remaining runtime verification

- Verify template download and UTF-8 display in supported spreadsheet applications.
- Verify quoted commas, quotes, CRLF/LF, and UTF-8 note values.
- Verify insert, update, locked-row rejection, duplicate-row rejection, and transaction rollback against the database.
- Verify authorization behavior for users with query-only and add permissions.

### Acceptance criteria status

1. Implemented: downloadable and documented CSV template.
2. Implemented: CSV upload and preview API.
3. Implemented: row-level master-data, period, dimension, and value validation.
4. Implemented: UPSERT against the Measure Data natural key; duplicates inside one CSV are rejected.
5. Implemented: row-level validation errors.
6. Implemented: atomic import with rollback on any failure.
7. Implemented: frontend download, upload, preview, confirmation, and result flow.

## 4. Phase 6 - KPI Report Trace and Score Color

### Resolution status

Resolved in code on 2026-06-22.

### Existing implementation

- Report query triggers backend recalculation before reading score snapshots.
- Trend, gauge, target-versus-actual, summary, personal, and organization views exist.
- Formula and aggregation information are displayed.
- Backend report models return `calculationTrace` and configured color information.

### Implemented resolution

- KPI score and status badges use backend `fontColor` and `bgColor` values after CSS color validation.
- Missing or invalid configured colors fall back to the standard status colors.
- The score gauge uses the selected report row's configured background color.
- Each report row provides a calculation-trace action.
- The trace dialog displays KPI context, formula, aggregation method, target, actual, score, color rule, and formatted JSON trace.
- Missing or legacy non-JSON trace data is handled without breaking the report.

### Remaining runtime verification

- Verify configured colors with light, dark, named, RGB, and hex color values.
- Verify fallback colors for missing and invalid configuration.
- Verify JSON, legacy text, and empty calculation traces.
- Verify the trace dialog at desktop and mobile widths.

### Acceptance criteria status

1. Implemented: display configured background/font colors.
2. Implemented: provide a trace action for every score row.
3. Implemented: show formula, aggregation, target, actual, score, color rule, and full calculation trace.
4. Implemented: handle missing, invalid JSON, and legacy text trace data.

## 5. Phase 7 - OKR Cycle Status Flow

### Resolution status

Resolved in code on 2026-06-22.

### Existing implementation

- OKR cycle CRUD and selection APIs exist.
- The supported values are `DRAFT`, `ACTIVE`, `CLOSED`, and `ARCHIVED`.
- Frontend create/edit pages expose the status field.
- Backend validation rejects values outside the supported set.

### Implemented resolution

- A new cycle can only be created in `DRAFT` status.
- Backend service logic reads the stored cycle and enforces the transition before update.
- Allowed transitions are `DRAFT -> ACTIVE`, `DRAFT -> ARCHIVED`, `ACTIVE -> CLOSED`, and `CLOSED -> ARCHIVED`.
- Updating other cycle fields without changing the status remains allowed.
- `ARCHIVED` is terminal and reopening is not supported.
- The create page displays a fixed Draft status.
- The edit page only lists the current status and its allowed next statuses.

### Remaining runtime verification

- Verify every allowed transition through the browser and API.
- Verify rejected direct transitions return the expected message.
- Confirm whether closing a cycle must create a final OKR snapshot. This was not required by the original Phase 7 list and remains a business decision.

### Proposed transition baseline

| Current status | Allowed next status |
|---|---|
| `DRAFT` | `ACTIVE`, `ARCHIVED` |
| `ACTIVE` | `CLOSED` |
| `CLOSED` | `ARCHIVED` |
| `ARCHIVED` | None |

Reopen behavior should be added only when there is a confirmed business requirement and audit trail.

### Acceptance criteria status

1. Implemented: enforce transitions using the stored status.
2. Implemented: enforce the rule in the domain service used by backend callers.
3. Implemented: show only allowed statuses in the frontend.
4. Implemented: return a clear service error for invalid transitions.
5. Pending business decision: whether closing creates a final snapshot.
6. Pending: automated transition tests.

## 6. Phase 12 - Source-Driven Action Creation

### Existing implementation

- Action Plan and Action Item CRUD pages exist.
- Owner lists and source links are embedded in create/edit flows.
- KPI, OKR, Strategy, and Insight source types are supported by Action maintenance.
- Action status, PDCA stage, progress roll-up, overdue reporting, owner workload, and source coverage are implemented.

### Missing implementation

- KPI report pages do not provide a create-Action action for the selected KPI result.
- OKR report pages do not provide a create-Action action for the selected Objective or Key Result.
- Strategy report pages do not provide a create-Action action for the selected Strategy Objective.
- No shared source context or route contract was found for pre-populating Action creation from a report.

Users can manually create an Action and select its source, but this is not the planned source-driven workflow.

### Impact

- The underlying Action/source data model works.
- Users must manually locate and bind the source again, which increases operational effort and binding mistakes.
- This gap affects workflow efficiency rather than core Action data integrity.

### Recommended timing

Implement after the core Phase 1 to Phase 13 workflow has passed acceptance. The current manual source binding provides a functional fallback.

### Acceptance criteria

1. Add `Create Action` to KPI, OKR, and Strategy report/detail contexts.
2. Route to Action Plan or Action Item creation with a typed source context.
3. Pre-populate `sourceType`, `sourceOid`, and a human-readable source description.
4. Preserve the source context across authentication redirects and page refresh when appropriate.
5. Validate source existence and access permission in the backend.
6. Prevent duplicate source links in the Action request.

## 7. Recommended Implementation Order

1. Complete Phase 1 to Phase 13 code and runtime acceptance.
2. Phase 12 source-driven Action creation.

## 8. Remaining Verification

After these gaps are implemented, Phase completion should still be verified with:

- Frontend route and permission checks
- API request/response integration tests
- Database constraint and transaction tests
- KPI calculation fixtures covering each formula and aggregation method
- OKR check-in, roll-up, snapshot, and lifecycle tests
- Strategy snapshot/report tests
- Action progress, overdue, owner, and source-link tests
- Browser-level acceptance tests for Phase 1 through Phase 13
