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
| Phase 4 | Partial | Measure Data import API and file-import UI are missing |
| Phase 5 | No explicit gap found | None found during this review |
| Phase 6 | Partial | Calculation trace and configured score color are not displayed by the frontend |
| Phase 7 | Partial | OKR cycle status transition rules are not enforced |
| Phase 8 | No explicit gap found | None found during this review |
| Phase 9 | No explicit gap found | None found during this review |
| Phase 10 | No explicit gap found | None found during this review |
| Phase 11 | No explicit gap found | None found during this review |
| Phase 12 | Partial | Source-driven Action creation from report pages is missing |
| Phase 13 | No explicit gap found | None found during this review |

`No explicit gap found` means the planned frontend page, backend implementation, and API connection were found in code. It does not mean the Phase has passed runtime acceptance testing.

## 3. Phase 4 - KPI Measure Data Import

### Existing implementation

- Measure Data query, load, create/update, and delete are available.
- KPI, organization, and account dimensions are supported.
- `DAY`, `WEEK`, `MONTH`, `QUARTER`, `HALFYEAR`, and `YEAR` period rules are implemented.
- The frontend contains `IMPORT` as a Measure Data source type.

### Missing implementation

- No CSV or Excel file selector/upload UI was found.
- No import preview or validation result UI was found.
- No backend file-import endpoint was found in `MdPROG004D0001Controller`.
- No batch import service covering row validation, duplicate handling, and transaction behavior was found.

The existing `IMPORT` value only identifies the source type of a manually submitted record. It is not an import function.

### Impact

- Users must enter Measure Data manually or call the single-record API.
- Bulk initialization and periodic data loading cannot be performed through the planned UI/API.

### Recommended timing

Implement after the core KPI workflow is stable. This item does not block manual Measure Data entry or KPI calculation.

### Acceptance criteria

1. Provide a documented CSV or Excel template.
2. Provide file upload and preview APIs.
3. Validate KPI, period, target/actual, organization, and account values per row.
4. Define duplicate handling as reject, replace, or upsert.
5. Return row-level validation errors without silently dropping records.
6. Define whether import is atomic or allows partial success.
7. Provide a frontend upload, preview, confirmation, and result flow.

## 4. Phase 6 - KPI Report Trace and Score Color

### Existing implementation

- Report query triggers backend recalculation before reading score snapshots.
- Trend, gauge, target-versus-actual, summary, personal, and organization views exist.
- Formula and aggregation information are displayed.
- Backend report models return `calculationTrace` and configured color information.

### Missing implementation

- The KPI report frontend does not render `calculationTrace`.
- The frontend uses fixed Bootstrap status classes instead of the score color returned by the backend.
- No trace detail panel, dialog, or drill-down was found.

### Impact

- Users cannot inspect how the official KPI score was calculated.
- Configured KPI score colors may not match the colors displayed in the report.
- The report does not fully satisfy the auditability requirement in the implementation plan.

### Recommended timing

Implement now. The backend already supplies the required data, so this is mainly a frontend presentation change with limited scope.

### Acceptance criteria

1. Display the configured background/font color returned by the backend.
2. Provide a trace action for every score row.
3. Show formula, aggregation method, source values, raw score, final score, and matched color rule.
4. Handle missing or legacy trace data without breaking the report.

## 5. Phase 7 - OKR Cycle Status Flow

### Existing implementation

- OKR cycle CRUD and selection APIs exist.
- The supported values are `DRAFT`, `ACTIVE`, `CLOSED`, and `ARCHIVED`.
- Frontend create/edit pages expose the status field.
- Backend validation rejects values outside the supported set.

### Missing implementation

- The frontend allows users to select any status directly.
- The backend validates only the status value, not the transition from the stored status.
- No dedicated activate, close, reopen, or archive command was found.
- No restrictions based on cycle contents or dates were found.

### Impact

- A cycle can skip required lifecycle steps.
- Closed or archived cycles can potentially return to an earlier state without an explicit rule.
- Snapshot and reporting behavior may become inconsistent if lifecycle meaning is not enforced.

### Recommended timing

Implement now. This is a domain integrity rule, and postponing it can create invalid production data.

### Proposed transition baseline

| Current status | Allowed next status |
|---|---|
| `DRAFT` | `ACTIVE`, `ARCHIVED` |
| `ACTIVE` | `CLOSED` |
| `CLOSED` | `ARCHIVED` |
| `ARCHIVED` | None |

Reopen behavior should be added only when there is a confirmed business requirement and audit trail.

### Acceptance criteria

1. Enforce transitions in backend logic using the stored status.
2. Prevent direct status changes through generic update from bypassing the rule.
3. Show only allowed actions or next statuses in the frontend.
4. Return a clear validation error for invalid transitions.
5. Define whether closing a cycle creates a final OKR snapshot.
6. Add tests for every allowed and rejected transition.

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

1. Phase 7 OKR cycle status transition enforcement.
2. Phase 6 KPI calculation trace and configured score color display.
3. Complete Phase 1 to Phase 13 code and runtime acceptance.
4. Phase 4 Measure Data file import.
5. Phase 12 source-driven Action creation.

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
