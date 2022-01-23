package com.company;

public interface WorkflowScheduler {
    void processWorkflow(Workflow[] workflows, int worker_count);
}
