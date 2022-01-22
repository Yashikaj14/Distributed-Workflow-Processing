package com.company;

import java.util.ArrayList;
import java.util.List;

public class Task {
    private String name;
    private Long started_at = 0L;
    private Long completed_at = 0L;
    private String worker;
    private Integer cost = 0;
    private List<String> dependencies;
    private String description;
    transient public boolean processed = false;
    transient public int level = 0;

    public Task() {
    }

    public Task(String name, Long started_at, String worker, Integer cost, ArrayList<String> dependencies, String description) {
        this.name = name;
        this.started_at = started_at;
        this.worker = worker;
        this.cost = cost;
        this.dependencies = dependencies;
        this.description = description;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getStarted_at() {
        return started_at;
    }

    public void setStarted_at(Long started_at) {
        this.started_at = started_at;
    }

    public Long getCompleted_at() {
        return completed_at;
    }

    public void setCompleted_at(Long completed_at) {
        this.completed_at = completed_at;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

