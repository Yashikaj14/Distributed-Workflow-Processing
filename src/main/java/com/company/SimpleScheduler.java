package com.company;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class SimpleScheduler implements WorkflowScheduler{

    PriorityQueue<Task> readyTaskQueue;
    Queue<Task> runningTaskQueue;

    static class byStartTime implements Comparator<Workflow>{
        @Override
        public int compare(Workflow o1, Workflow o2) {
            return (int) ((o1.getScheduled_at() - o2.getScheduled_at())%1000000007);
        }
    }

    @Override
    public void processWorkflow(Workflow[] workflows, int worker_count) {
        Arrays.sort(workflows, new byStartTime());
        long time = workflows[0].getScheduled_at();
        readyTaskQueue = new PriorityQueue<>(Comparator.comparingInt(Task::getCost).reversed());
        runningTaskQueue = new PriorityQueue<>(Comparator.comparingLong(Task::getCompleted_at));
        boolean[] workers = new boolean[worker_count];
        for(int i=0;i<worker_count;i++)
            workers[i] = false;

        readyTaskQueue.addAll(workflows[0].level_graph.get(0));
        int totalProcessed = 0;


        int totalCount = 0;
        for(Workflow workflow: workflows){
            totalCount += workflow.tasks.size();
        }
        // main loop for the algorithm
        while(totalProcessed < totalCount){
            boolean prs = true;
            updatePendingTaskQueue(workflows, time);
            // transferring tasks from ready queue to processing queue
            while(!readyTaskQueue.isEmpty() && (runningTaskQueue.size() < worker_count)){

                Task t = readyTaskQueue.poll();
                t.setStarted_at(time);
                t.setCompleted_at(time + t.getCost());
                runningTaskQueue.add(t);
                // to set the worker
                for(int i=0;i<worker_count;i++){
                    if(!workers[i]){
                        t.setWorker("w"+(i+1));
                        workers[i] = true;
                        break;
                    }
                }
            }
            boolean flag = true;
            // executing the processing queue
            while (!runningTaskQueue.isEmpty() && flag){
                if(runningTaskQueue.peek().getCompleted_at() <= time){
                    Task t = runningTaskQueue.poll();
                    t.setProcessed(true);
                    totalProcessed++;
                    prs = false;
                    int worker = t.getWorker().charAt(1) - '1';
                    workers[worker] = false;
                }
                else {
                    flag = false;
                }
            }
            if(prs)
                time++;
        }
    }
    // check if any task is available to enter in the ready queue
    void updatePendingTaskQueue(Workflow[] workflows, long time){
        for(Workflow workflow: workflows){
            if(workflow.getScheduled_at() <= time){
                for(Task task: workflow.getTasks()){
                    if(!task.isProcessed() && !readyTaskQueue.contains(task) && !runningTaskQueue.contains(task)){
                        boolean dependenciesProcessed = true;
                        for (String dep: task.getDependencies()){
                            if(!workflow.taskMap.get(dep).isProcessed()){
                                dependenciesProcessed = false;
                                break;
                            }
                        }
                        if (dependenciesProcessed)
                            readyTaskQueue.add(task);
                    }
                }
            }
        }
    }
}
