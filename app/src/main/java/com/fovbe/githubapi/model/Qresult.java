package com.fovbe.githubapi.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Qresult {
    @SerializedName("total_count")
    @Expose
    private Integer totalCount;
    @SerializedName("incomplete_results")
    @Expose
    private Boolean incompleteResults;
    @SerializedName("items")
    @Expose
    private List<Users> items = null;

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Boolean getIncompleteResults() {
        return incompleteResults;
    }

    public void setIncompleteResults(Boolean incompleteResults) {
        this.incompleteResults = incompleteResults;
    }

    public List<Users> getItems() {
        return items;
    }

    public void setItems(List<Users> items) {
        this.items = items;
    }
}
