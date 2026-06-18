package org.qifu.md.model;

import java.util.ArrayList;
import java.util.List;

public class ManagementDashboardResult implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private ManagementDashboardDomainSummary kpi = new ManagementDashboardDomainSummary();
    private ManagementDashboardDomainSummary okr = new ManagementDashboardDomainSummary();
    private ManagementDashboardDomainSummary strategy = new ManagementDashboardDomainSummary();
    private ManagementDashboardDomainSummary action = new ManagementDashboardDomainSummary();
    private List<ManagementDashboardAlert> alerts = new ArrayList<>();
    private List<ManagementDashboardOrgSummary> organizationSummaries = new ArrayList<>();
    private List<ManagementDashboardStrategyScorecardRow> strategyScorecards = new ArrayList<>();
    private List<ActionReportRow> delayedActions = new ArrayList<>();
    private List<ManagementDashboardAtRiskObjectiveRow> atRiskObjectives = new ArrayList<>();

    public ManagementDashboardDomainSummary getKpi() {
        return kpi;
    }

    public void setKpi(ManagementDashboardDomainSummary kpi) {
        this.kpi = kpi;
    }

    public ManagementDashboardDomainSummary getOkr() {
        return okr;
    }

    public void setOkr(ManagementDashboardDomainSummary okr) {
        this.okr = okr;
    }

    public ManagementDashboardDomainSummary getStrategy() {
        return strategy;
    }

    public void setStrategy(ManagementDashboardDomainSummary strategy) {
        this.strategy = strategy;
    }

    public ManagementDashboardDomainSummary getAction() {
        return action;
    }

    public void setAction(ManagementDashboardDomainSummary action) {
        this.action = action;
    }

    public List<ManagementDashboardAlert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<ManagementDashboardAlert> alerts) {
        this.alerts = alerts;
    }

    public List<ManagementDashboardOrgSummary> getOrganizationSummaries() {
        return organizationSummaries;
    }

    public void setOrganizationSummaries(List<ManagementDashboardOrgSummary> organizationSummaries) {
        this.organizationSummaries = organizationSummaries;
    }

    public List<ManagementDashboardStrategyScorecardRow> getStrategyScorecards() {
        return strategyScorecards;
    }

    public void setStrategyScorecards(List<ManagementDashboardStrategyScorecardRow> strategyScorecards) {
        this.strategyScorecards = strategyScorecards;
    }

    public List<ActionReportRow> getDelayedActions() {
        return delayedActions;
    }

    public void setDelayedActions(List<ActionReportRow> delayedActions) {
        this.delayedActions = delayedActions;
    }

    public List<ManagementDashboardAtRiskObjectiveRow> getAtRiskObjectives() {
        return atRiskObjectives;
    }

    public void setAtRiskObjectives(List<ManagementDashboardAtRiskObjectiveRow> atRiskObjectives) {
        this.atRiskObjectives = atRiskObjectives;
    }
}
