package org.qifu.md.logic.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.YesNo;
import org.qifu.md.entity.MdActionItem;
import org.qifu.md.entity.MdActionOwner;
import org.qifu.md.entity.MdActionPlan;
import org.qifu.md.entity.MdActionSourceLink;
import org.qifu.md.logic.IActionReportLogicService;
import org.qifu.md.model.ActionReportQuery;
import org.qifu.md.model.ActionReportResult;
import org.qifu.md.model.ActionReportRow;
import org.qifu.md.model.ActionReportSummary;
import org.qifu.md.service.IMdActionItemService;
import org.qifu.md.service.IMdActionOwnerService;
import org.qifu.md.service.IMdActionPlanService;
import org.qifu.md.service.IMdActionSourceLinkService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, timeout = 300, readOnly = true)
public class ActionReportLogicServiceImpl implements IActionReportLogicService {

    private static final String ACTION_TYPE_ITEM = "ITEM";

    private final IMdActionPlanService<MdActionPlan, String> mdActionPlanService;
    private final IMdActionItemService<MdActionItem, String> mdActionItemService;
    private final IMdActionOwnerService<MdActionOwner, String> mdActionOwnerService;
    private final IMdActionSourceLinkService<MdActionSourceLink, String> mdActionSourceLinkService;

    public ActionReportLogicServiceImpl(IMdActionPlanService<MdActionPlan, String> mdActionPlanService,
            IMdActionItemService<MdActionItem, String> mdActionItemService,
            IMdActionOwnerService<MdActionOwner, String> mdActionOwnerService,
            IMdActionSourceLinkService<MdActionSourceLink, String> mdActionSourceLinkService) {
        this.mdActionPlanService = mdActionPlanService;
        this.mdActionItemService = mdActionItemService;
        this.mdActionOwnerService = mdActionOwnerService;
        this.mdActionSourceLinkService = mdActionSourceLinkService;
    }

    @Override
    public DefaultResult<ActionReportResult> report(ActionReportQuery query) throws ServiceException {
        ActionReportQuery q = query == null ? new ActionReportQuery() : query;

        List<MdActionPlan> plans = this.mdActionPlanService.selectList("PLAN_CODE", "ASC").getValue();
        List<MdActionItem> items = this.mdActionItemService.selectList("SORT_NO", "ASC").getValue();
        List<MdActionOwner> owners = this.mdActionOwnerService.selectList("ACTION_TYPE, ACTION_OID", "ASC").getValue();
        List<MdActionSourceLink> sourceLinks = this.mdActionSourceLinkService.selectList("ACTION_TYPE, ACTION_OID", "ASC").getValue();

        Map<String, MdActionPlan> planMap = toPlanMap(plans);
        Map<String, List<MdActionOwner>> ownerMap = groupItemOwners(owners);
        Map<String, List<MdActionSourceLink>> sourceLinkMap = groupItemSourceLinks(sourceLinks);

        LocalDate today = LocalDate.now();
        List<ActionReportRow> rows = new ArrayList<>();
        for (MdActionItem item : safeItemList(items)) {
            if (!matchesItem(item, q, today, ownerMap, sourceLinkMap)) {
                continue;
            }
            rows.add(toRow(item, planMap.get(item.getPlanOid()), ownerMap.get(item.getOid()), sourceLinkMap.get(item.getOid()), today));
        }
        rows.sort(Comparator
                .comparing(ActionReportRow::getPlanCode, Comparator.nullsLast(String::compareTo))
                .thenComparing(ActionReportRow::getActionStage, Comparator.nullsLast(String::compareTo))
                .thenComparing(ActionReportRow::getEndDate, Comparator.nullsLast(Date::compareTo))
                .thenComparing(ActionReportRow::getItemName, Comparator.nullsLast(String::compareTo)));

        ActionReportResult report = new ActionReportResult();
        report.setRows(rows);
        report.setSummary(buildSummary(rows, ownerMap, sourceLinkMap));

        DefaultResult<ActionReportResult> result = new DefaultResult<>();
        result.setSuccess(YesNo.YES);
        result.setValue(report);
        return result;
    }

    private Map<String, MdActionPlan> toPlanMap(List<MdActionPlan> plans) {
        Map<String, MdActionPlan> map = new HashMap<>();
        if (plans == null) {
            return map;
        }
        for (MdActionPlan plan : plans) {
            if (plan != null && StringUtils.isNotBlank(plan.getOid())) {
                map.put(plan.getOid(), plan);
            }
        }
        return map;
    }

    private List<MdActionItem> safeItemList(List<MdActionItem> items) {
        return items == null ? List.of() : items;
    }

    private Map<String, List<MdActionOwner>> groupItemOwners(List<MdActionOwner> owners) {
        if (owners == null) {
            return Map.of();
        }
        return owners.stream()
                .filter(owner -> owner != null && ACTION_TYPE_ITEM.equals(owner.getActionType()) && StringUtils.isNotBlank(owner.getActionOid()))
                .collect(Collectors.groupingBy(MdActionOwner::getActionOid));
    }

    private Map<String, List<MdActionSourceLink>> groupItemSourceLinks(List<MdActionSourceLink> sourceLinks) {
        if (sourceLinks == null) {
            return Map.of();
        }
        return sourceLinks.stream()
                .filter(link -> link != null && ACTION_TYPE_ITEM.equals(link.getActionType()) && StringUtils.isNotBlank(link.getActionOid()))
                .collect(Collectors.groupingBy(MdActionSourceLink::getActionOid));
    }

    private boolean matchesItem(MdActionItem item, ActionReportQuery query, LocalDate today,
            Map<String, List<MdActionOwner>> ownerMap,
            Map<String, List<MdActionSourceLink>> sourceLinkMap) {
        if (item == null) {
            return false;
        }
        if (StringUtils.isNotBlank(query.getPlanOid()) && !Strings.CS.equals(query.getPlanOid(), item.getPlanOid())) {
            return false;
        }
        if (StringUtils.isNotBlank(query.getActionStage()) && !Strings.CS.equals(query.getActionStage(), item.getActionStage())) {
            return false;
        }
        if (StringUtils.isNotBlank(query.getStatus()) && !Strings.CS.equals(query.getStatus(), item.getStatus())) {
            return false;
        }
        if (!matchesDateRange(item.getStartDate(), query.getStartDateFrom(), query.getStartDateTo())) {
            return false;
        }
        if (!matchesDateRange(item.getEndDate(), query.getEndDateFrom(), query.getEndDateTo())) {
            return false;
        }
        if (query.isOverdueOnly() && !isOverdue(item, today)) {
            return false;
        }
        if (!matchesOwner(ownerMap.get(item.getOid()), query)) {
            return false;
        }
        return matchesSourceLink(sourceLinkMap.get(item.getOid()), query);
    }

    private boolean matchesDateRange(Date value, Date from, Date to) {
        if (from == null && to == null) {
            return true;
        }
        if (value == null) {
            return false;
        }
        if (from != null && value.before(from)) {
            return false;
        }
        return to == null || !value.after(to);
    }

    private boolean matchesOwner(List<MdActionOwner> owners, ActionReportQuery query) {
        if (StringUtils.isBlank(query.getOwnerType()) && StringUtils.isBlank(query.getAccount()) && StringUtils.isBlank(query.getOrgOid())) {
            return true;
        }
        if (owners == null || owners.isEmpty()) {
            return false;
        }
        for (MdActionOwner owner : owners) {
            if (owner == null) {
                continue;
            }
            if (StringUtils.isNotBlank(query.getOwnerType()) && !Strings.CS.equals(query.getOwnerType(), owner.getOwnerType())) {
                continue;
            }
            if (StringUtils.isNotBlank(query.getAccount()) && !Strings.CS.equals(query.getAccount(), owner.getAccount())) {
                continue;
            }
            if (StringUtils.isNotBlank(query.getOrgOid()) && !Strings.CS.equals(query.getOrgOid(), owner.getOrgOid())) {
                continue;
            }
            return true;
        }
        return false;
    }

    private boolean matchesSourceLink(List<MdActionSourceLink> sourceLinks, ActionReportQuery query) {
        if (StringUtils.isBlank(query.getSourceType()) && StringUtils.isBlank(query.getSourceOid())) {
            return true;
        }
        if (sourceLinks == null || sourceLinks.isEmpty()) {
            return false;
        }
        for (MdActionSourceLink sourceLink : sourceLinks) {
            if (sourceLink == null) {
                continue;
            }
            if (StringUtils.isNotBlank(query.getSourceType()) && !Strings.CS.equals(query.getSourceType(), sourceLink.getSourceType())) {
                continue;
            }
            if (StringUtils.isNotBlank(query.getSourceOid()) && !Strings.CS.equals(query.getSourceOid(), sourceLink.getSourceOid())) {
                continue;
            }
            return true;
        }
        return false;
    }

    private ActionReportRow toRow(MdActionItem item, MdActionPlan plan, List<MdActionOwner> owners,
            List<MdActionSourceLink> sourceLinks, LocalDate today) {
        ActionReportRow row = new ActionReportRow();
        row.setOid(item.getOid());
        row.setPlanOid(item.getPlanOid());
        if (plan != null) {
            row.setPlanCode(plan.getPlanCode());
            row.setPlanName(plan.getPlanName());
        }
        row.setItemName(item.getItemName());
        row.setActionStage(item.getActionStage());
        row.setStatus(item.getStatus());
        row.setStartDate(item.getStartDate());
        row.setEndDate(item.getEndDate());
        row.setDoneDate(item.getDoneDate());
        row.setProgressValue(item.getProgressValue());
        row.setOverdue(isOverdue(item, today));
        row.setOwnerSummary(ownerSummary(owners));
        row.setSourceSummary(sourceSummary(sourceLinks));
        return row;
    }

    private ActionReportSummary buildSummary(List<ActionReportRow> rows,
            Map<String, List<MdActionOwner>> ownerMap,
            Map<String, List<MdActionSourceLink>> sourceLinkMap) {
        ActionReportSummary summary = new ActionReportSummary();
        Set<String> planOids = new HashSet<>();
        BigDecimal progressTotal = BigDecimal.ZERO;
        int progressCount = 0;
        int ownerCount = 0;
        int sourceLinkCount = 0;

        for (ActionReportRow row : rows) {
            if (StringUtils.isNotBlank(row.getPlanOid())) {
                planOids.add(row.getPlanOid());
            }
            if (row.isOverdue()) {
                summary.setOverdueCount(summary.getOverdueCount() + 1);
            }
            if (isCompleted(row)) {
                summary.setCompletedCount(summary.getCompletedCount() + 1);
            }
            if (row.getProgressValue() != null) {
                progressTotal = progressTotal.add(row.getProgressValue());
                progressCount++;
            }
            if ("PLAN".equals(row.getActionStage())) {
                summary.setPlanStageCount(summary.getPlanStageCount() + 1);
            } else if ("DO".equals(row.getActionStage())) {
                summary.setDoStageCount(summary.getDoStageCount() + 1);
            } else if ("CHECK".equals(row.getActionStage())) {
                summary.setCheckStageCount(summary.getCheckStageCount() + 1);
            } else if ("ACT".equals(row.getActionStage())) {
                summary.setActStageCount(summary.getActStageCount() + 1);
            }
            ownerCount += ownerMap.getOrDefault(row.getOid(), List.of()).size();
            sourceLinkCount += sourceLinkMap.getOrDefault(row.getOid(), List.of()).size();
        }

        summary.setPlanCount(planOids.size());
        summary.setItemCount(rows.size());
        summary.setOwnerCount(ownerCount);
        summary.setSourceLinkCount(sourceLinkCount);
        if (progressCount > 0) {
            summary.setAvgProgress(progressTotal.divide(BigDecimal.valueOf(progressCount), 2, RoundingMode.HALF_UP));
        }
        return summary;
    }

    private boolean isCompleted(ActionReportRow row) {
        return row.getDoneDate() != null
                || "CLOSED".equals(row.getStatus())
                || (row.getProgressValue() != null && row.getProgressValue().compareTo(new BigDecimal("100")) >= 0);
    }

    private boolean isOverdue(MdActionItem item, LocalDate today) {
        return item.getEndDate() != null
                && toLocalDate(item.getEndDate()).isBefore(today)
                && item.getDoneDate() == null
                && !Strings.CS.equalsAny(item.getStatus(), "CLOSED", "ARCHIVED");
    }

    private LocalDate toLocalDate(Date value) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);
        return LocalDate.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    private String ownerSummary(List<MdActionOwner> owners) {
        if (owners == null || owners.isEmpty()) {
            return "";
        }
        return owners.stream()
                .map(owner -> "ACCOUNT".equals(owner.getOwnerType()) ? owner.getAccount() : owner.getOrgOid())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(", "));
    }

    private String sourceSummary(List<MdActionSourceLink> sourceLinks) {
        if (sourceLinks == null || sourceLinks.isEmpty()) {
            return "";
        }
        return sourceLinks.stream()
                .map(sourceLink -> sourceLink.getSourceType() + ":" + sourceLink.getSourceOid())
                .collect(Collectors.joining(", "));
    }
}
