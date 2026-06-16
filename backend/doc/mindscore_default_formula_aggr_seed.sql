-- MindScore default Formula / Formula Recommend Rule / Aggregation Method seed data.
-- Target DB: MySQL / MariaDB.
-- Safe to rerun: unique keys are updated, existing OID values are preserved.

SET @seed_user = 'system';

-- ---------------------------------------------------------------------
-- md_formula
-- ---------------------------------------------------------------------
SET @f_bigger = COALESCE((SELECT OID FROM md_formula WHERE FORMULA_CODE = 'BIGGER_IS_BETTER_LINEAR' AND VERSION_NO = 1), UUID());
SET @f_smaller = COALESCE((SELECT OID FROM md_formula WHERE FORMULA_CODE = 'SMALLER_IS_BETTER_LINEAR' AND VERSION_NO = 1), UUID());
SET @f_range = COALESCE((SELECT OID FROM md_formula WHERE FORMULA_CODE = 'RANGE_IN_BOUNDS' AND VERSION_NO = 1), UUID());
SET @f_quasi = COALESCE((SELECT OID FROM md_formula WHERE FORMULA_CODE = 'QUASI_IS_BETTER_TARGET' AND VERSION_NO = 1), UUID());
SET @f_manual = COALESCE((SELECT OID FROM md_formula WHERE FORMULA_CODE = 'MANUAL_SCORE' AND VERSION_NO = 1), UUID());
SET @f_boolean = COALESCE((SELECT OID FROM md_formula WHERE FORMULA_CODE = 'BOOLEAN_PASS_FAIL' AND VERSION_NO = 1), UUID());

INSERT INTO md_formula
    (OID, FORMULA_CODE, FORMULA_NAME, FORMULA_TYPE, SCRIPT_TYPE, EXPRESSION, RETURN_TYPE, VERSION_NO,
     IS_SYSTEM, IS_RECOMMENDABLE, DESCRIPTION, EXAMPLE_TEXT, ENABLED, CUSERID, CDATE)
VALUES
    (@f_bigger, 'BIGGER_IS_BETTER_LINEAR', 'Bigger Is Better Linear', 'BUILTIN', 'GROOVY',
     '$P{actual} == null || $P{target} == null || $P{target} == 0 ? 0 : (($P{actual} * 100 / $P{target}) > 100 ? 100 : ($P{actual} * 100 / $P{target}))',
     'DECIMAL', 1, 'Y', 'Y',
     'For KPIs where larger actual value is better. Score is actual / target * 100, capped at 100.',
     'actual=80,target=100 => 80; actual=120,target=100 => 100',
     'Y', @seed_user, NOW()),
    (@f_smaller, 'SMALLER_IS_BETTER_LINEAR', 'Smaller Is Better Linear', 'BUILTIN', 'GROOVY',
     '$P{actual} == null || $P{target} == null ? 0 : ($P{actual} <= $P{target} ? 100 : ($P{actual} == 0 ? 0 : ($P{target} * 100 / $P{actual})))',
     'DECIMAL', 1, 'Y', 'Y',
     'For KPIs where smaller actual value is better. Values at or below target score 100; values above target decay by target / actual.',
     'actual=8,target=10 => 100; actual=20,target=10 => 50',
     'Y', @seed_user, NOW()),
    (@f_range, 'RANGE_IN_BOUNDS', 'Range In Bounds', 'BUILTIN', 'GROOVY',
     '$P{actual} == null ? 0 : ($P{kpi.min} != null && $P{actual} < $P{kpi.min} ? ($P{kpi.min} == 0 ? 0 : ($P{actual} * 100 / $P{kpi.min})) : ($P{kpi.max} != null && $P{actual} > $P{kpi.max} ? ($P{actual} == 0 ? 0 : ($P{kpi.max} * 100 / $P{actual})) : 100))',
     'DECIMAL', 1, 'Y', 'Y',
     'For KPIs that pass when actual value stays inside min and max. Outside the range, score decays linearly.',
     'min=90,max=110,actual=100 => 100; actual=120 => 91.6667',
     'Y', @seed_user, NOW()),
    (@f_quasi, 'QUASI_IS_BETTER_TARGET', 'Quasi Is Better Target', 'BUILTIN', 'GROOVY',
     '$P{actual} == null || $P{target} == null || $P{target} == 0 ? 0 : (((100 - (($P{actual} - $P{target}).abs() * 100 / $P{target}.abs())) < 0) ? 0 : (100 - (($P{actual} - $P{target}).abs() * 100 / $P{target}.abs())))',
     'DECIMAL', 1, 'Y', 'Y',
     'For KPIs where closest to target is best. Score decreases by percentage distance from target.',
     'actual=95,target=100 => 95; actual=110,target=100 => 90',
     'Y', @seed_user, NOW()),
    (@f_manual, 'MANUAL_SCORE', 'Manual Score', 'BUILTIN', 'GROOVY',
     '$P{actual} == null ? 0 : ($P{actual} > 100 ? 100 : ($P{actual} < 0 ? 0 : $P{actual}))',
     'DECIMAL', 1, 'Y', 'Y',
     'Use actual value as already-scored manual input, capped to 0-100.',
     'actual=85 => 85',
     'Y', @seed_user, NOW()),
    (@f_boolean, 'BOOLEAN_PASS_FAIL', 'Boolean Pass Fail', 'BUILTIN', 'GROOVY',
     '$P{actual} == null ? 0 : ($P{actual} > 0 ? 100 : 0)',
     'DECIMAL', 1, 'Y', 'Y',
     'For yes/no KPIs. Non-zero actual means pass and scores 100; zero scores 0.',
     'actual=1 => 100; actual=0 => 0',
     'Y', @seed_user, NOW())
ON DUPLICATE KEY UPDATE
    FORMULA_NAME = VALUES(FORMULA_NAME),
    FORMULA_TYPE = VALUES(FORMULA_TYPE),
    SCRIPT_TYPE = VALUES(SCRIPT_TYPE),
    EXPRESSION = VALUES(EXPRESSION),
    RETURN_TYPE = VALUES(RETURN_TYPE),
    IS_SYSTEM = VALUES(IS_SYSTEM),
    IS_RECOMMENDABLE = VALUES(IS_RECOMMENDABLE),
    DESCRIPTION = VALUES(DESCRIPTION),
    EXAMPLE_TEXT = VALUES(EXAMPLE_TEXT),
    ENABLED = VALUES(ENABLED),
    UUSERID = @seed_user,
    UDATE = NOW();

-- ---------------------------------------------------------------------
-- md_aggregation_method
-- Built-in aggregation expressions must stay NULL, otherwise calculation uses script mode.
-- ---------------------------------------------------------------------
SET @a_sum = COALESCE((SELECT OID FROM md_aggregation_method WHERE AGGR_CODE = 'SUM'), UUID());
SET @a_avg = COALESCE((SELECT OID FROM md_aggregation_method WHERE AGGR_CODE = 'AVG'), UUID());
SET @a_max = COALESCE((SELECT OID FROM md_aggregation_method WHERE AGGR_CODE = 'MAX'), UUID());
SET @a_min = COALESCE((SELECT OID FROM md_aggregation_method WHERE AGGR_CODE = 'MIN'), UUID());
SET @a_cnt = COALESCE((SELECT OID FROM md_aggregation_method WHERE AGGR_CODE = 'CNT'), UUID());
SET @a_distinct = COALESCE((SELECT OID FROM md_aggregation_method WHERE AGGR_CODE = 'DISTINCT'), UUID());
SET @a_latest = COALESCE((SELECT OID FROM md_aggregation_method WHERE AGGR_CODE = 'LATEST_ACTUAL'), UUID());
SET @a_first = COALESCE((SELECT OID FROM md_aggregation_method WHERE AGGR_CODE = 'FIRST_ACTUAL'), UUID());
SET @a_non_null = COALESCE((SELECT OID FROM md_aggregation_method WHERE AGGR_CODE = 'NON_NULL_CNT'), UUID());
SET @a_valid_rate = COALESCE((SELECT OID FROM md_aggregation_method WHERE AGGR_CODE = 'VALID_RATE'), UUID());
SET @a_achievement = COALESCE((SELECT OID FROM md_aggregation_method WHERE AGGR_CODE = 'ACHIEVEMENT_RATE'), UUID());
SET @a_pass_rate = COALESCE((SELECT OID FROM md_aggregation_method WHERE AGGR_CODE = 'PASS_RATE'), UUID());

INSERT INTO md_aggregation_method
    (OID, AGGR_CODE, AGGR_NAME, AGGR_TYPE, EXPRESSION, DESCRIPTION, ENABLED, CUSERID, CDATE)
VALUES
    (@a_sum, 'SUM', 'Sum', 'BUILTIN', NULL, 'Sum all calculated score values.', 'Y', @seed_user, NOW()),
    (@a_avg, 'AVG', 'Average', 'BUILTIN', NULL, 'Average all calculated score values.', 'Y', @seed_user, NOW()),
    (@a_max, 'MAX', 'Maximum', 'BUILTIN', NULL, 'Use maximum calculated score value.', 'Y', @seed_user, NOW()),
    (@a_min, 'MIN', 'Minimum', 'BUILTIN', NULL, 'Use minimum calculated score value.', 'Y', @seed_user, NOW()),
    (@a_cnt, 'CNT', 'Count', 'BUILTIN', NULL, 'Count calculated score values.', 'Y', @seed_user, NOW()),
    (@a_distinct, 'DISTINCT', 'Distinct Count', 'BUILTIN', NULL, 'Count distinct calculated score values.', 'Y', @seed_user, NOW()),
    (@a_latest, 'LATEST_ACTUAL', 'Latest Actual', 'BUILTIN', NULL, 'Use latest measure actual value.', 'Y', @seed_user, NOW()),
    (@a_first, 'FIRST_ACTUAL', 'First Actual', 'BUILTIN', NULL, 'Use first measure actual value.', 'Y', @seed_user, NOW()),
    (@a_non_null, 'NON_NULL_CNT', 'Non-null Count', 'BUILTIN', NULL, 'Count measure rows with actual value.', 'Y', @seed_user, NOW()),
    (@a_valid_rate, 'VALID_RATE', 'Valid Rate', 'BUILTIN', NULL, 'Percentage of measure rows with actual value.', 'Y', @seed_user, NOW()),
    (@a_achievement, 'ACHIEVEMENT_RATE', 'Achievement Rate', 'BUILTIN', NULL, 'Latest actual divided by target as percentage.', 'Y', @seed_user, NOW()),
    (@a_pass_rate, 'PASS_RATE', 'Pass Rate', 'BUILTIN', NULL, 'Percentage of measure rows that pass KPI compare rule.', 'Y', @seed_user, NOW())
ON DUPLICATE KEY UPDATE
    AGGR_NAME = VALUES(AGGR_NAME),
    AGGR_TYPE = VALUES(AGGR_TYPE),
    EXPRESSION = VALUES(EXPRESSION),
    DESCRIPTION = VALUES(DESCRIPTION),
    ENABLED = VALUES(ENABLED),
    UUSERID = @seed_user,
    UDATE = NOW();

-- ---------------------------------------------------------------------
-- md_formula_recommend_rule
-- IS_DEFAULT is intentionally N for all rows because the controller allows only one enabled default rule globally.
-- ---------------------------------------------------------------------
SET @r_bigger_target = COALESCE((SELECT OID FROM md_formula_recommend_rule WHERE RULE_CODE = 'REC_BIGGER_TARGET'), UUID());
SET @r_bigger_minimum = COALESCE((SELECT OID FROM md_formula_recommend_rule WHERE RULE_CODE = 'REC_BIGGER_MINIMUM'), UUID());
SET @r_smaller_target = COALESCE((SELECT OID FROM md_formula_recommend_rule WHERE RULE_CODE = 'REC_SMALLER_TARGET'), UUID());
SET @r_smaller_maximum = COALESCE((SELECT OID FROM md_formula_recommend_rule WHERE RULE_CODE = 'REC_SMALLER_MAXIMUM'), UUID());
SET @r_quasi_target = COALESCE((SELECT OID FROM md_formula_recommend_rule WHERE RULE_CODE = 'REC_QUASI_TARGET'), UUID());
SET @r_quasi_range = COALESCE((SELECT OID FROM md_formula_recommend_rule WHERE RULE_CODE = 'REC_QUASI_RANGE'), UUID());
SET @r_manual_custom = COALESCE((SELECT OID FROM md_formula_recommend_rule WHERE RULE_CODE = 'REC_MANUAL_CUSTOM'), UUID());
SET @r_manual_target = COALESCE((SELECT OID FROM md_formula_recommend_rule WHERE RULE_CODE = 'REC_MANUAL_TARGET'), UUID());
SET @r_boolean_bigger_target = COALESCE((SELECT OID FROM md_formula_recommend_rule WHERE RULE_CODE = 'REC_BOOLEAN_BIGGER_TARGET'), UUID());
SET @r_boolean_manual_custom = COALESCE((SELECT OID FROM md_formula_recommend_rule WHERE RULE_CODE = 'REC_BOOLEAN_MANUAL_CUSTOM'), UUID());

INSERT INTO md_formula_recommend_rule
    (OID, RULE_CODE, RULE_NAME, MANAGEMENT_MODE, COMPARE_MODE, PERIOD_TYPE, DATA_TYPE,
     RECOMMENDED_FORMULA_OID, PRIORITY_NO, IS_DEFAULT, ENABLED, DESCRIPTION, CUSERID, CDATE)
VALUES
    (@r_bigger_target, 'REC_BIGGER_TARGET', 'Bigger Target Formula', 'BIGGER', 'TARGET', NULL, NULL,
     @f_bigger, 100, 'N', 'Y', 'Use bigger-is-better scoring for target-based KPI.', @seed_user, NOW()),
    (@r_bigger_minimum, 'REC_BIGGER_MINIMUM', 'Bigger Minimum Formula', 'BIGGER', 'MINIMUM', NULL, NULL,
     @f_bigger, 100, 'N', 'Y', 'Use bigger-is-better scoring for minimum-threshold KPI.', @seed_user, NOW()),
    (@r_smaller_target, 'REC_SMALLER_TARGET', 'Smaller Target Formula', 'SMALLER', 'TARGET', NULL, NULL,
     @f_smaller, 100, 'N', 'Y', 'Use smaller-is-better scoring for target-based KPI.', @seed_user, NOW()),
    (@r_smaller_maximum, 'REC_SMALLER_MAXIMUM', 'Smaller Maximum Formula', 'SMALLER', 'MAXIMUM', NULL, NULL,
     @f_smaller, 100, 'N', 'Y', 'Use smaller-is-better scoring for maximum-threshold KPI.', @seed_user, NOW()),
    (@r_quasi_target, 'REC_QUASI_TARGET', 'Quasi Target Formula', 'QUASI', 'TARGET', NULL, NULL,
     @f_quasi, 100, 'N', 'Y', 'Use closest-to-target scoring for quasi KPI.', @seed_user, NOW()),
    (@r_quasi_range, 'REC_QUASI_RANGE', 'Quasi Range Formula', 'QUASI', 'RANGE', NULL, NULL,
     @f_range, 100, 'N', 'Y', 'Use in-range scoring for quasi range KPI.', @seed_user, NOW()),
    (@r_manual_custom, 'REC_MANUAL_CUSTOM', 'Manual Custom Formula', 'MANUAL', 'CUSTOM', NULL, NULL,
     @f_manual, 100, 'N', 'Y', 'Use actual value as manual score.', @seed_user, NOW()),
    (@r_manual_target, 'REC_MANUAL_TARGET', 'Manual Target Formula', 'MANUAL', 'TARGET', NULL, NULL,
     @f_manual, 100, 'N', 'Y', 'Use actual value as manual score.', @seed_user, NOW()),
    (@r_boolean_bigger_target, 'REC_BOOLEAN_BIGGER_TARGET', 'Boolean Bigger Target Formula', 'BIGGER', 'TARGET', NULL, 'BOOLEAN',
     @f_boolean, 110, 'N', 'Y', 'Use pass/fail scoring for boolean KPI.', @seed_user, NOW()),
    (@r_boolean_manual_custom, 'REC_BOOLEAN_MANUAL_CUSTOM', 'Boolean Manual Custom Formula', 'MANUAL', 'CUSTOM', NULL, 'BOOLEAN',
     @f_boolean, 110, 'N', 'Y', 'Use pass/fail scoring for boolean KPI.', @seed_user, NOW())
ON DUPLICATE KEY UPDATE
    RULE_NAME = VALUES(RULE_NAME),
    MANAGEMENT_MODE = VALUES(MANAGEMENT_MODE),
    COMPARE_MODE = VALUES(COMPARE_MODE),
    PERIOD_TYPE = VALUES(PERIOD_TYPE),
    DATA_TYPE = VALUES(DATA_TYPE),
    RECOMMENDED_FORMULA_OID = VALUES(RECOMMENDED_FORMULA_OID),
    PRIORITY_NO = VALUES(PRIORITY_NO),
    IS_DEFAULT = VALUES(IS_DEFAULT),
    ENABLED = VALUES(ENABLED),
    DESCRIPTION = VALUES(DESCRIPTION),
    UUSERID = @seed_user,
    UDATE = NOW();
