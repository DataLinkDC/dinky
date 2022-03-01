package com.dlink.model;

/**
 * JobInstanceCount
 *
 * @author wenmo
 * @since 2022/2/28 22:20
 */
public class JobInstanceCount {

    private String status;
    private Integer counts;

    public JobInstanceCount() {
    }

    public JobInstanceCount(String status, Integer counts) {
        this.status = status;
        this.counts = counts;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCounts() {
        return counts;
    }

    public void setCounts(Integer counts) {
        this.counts = counts;
    }
}
