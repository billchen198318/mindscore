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
}
