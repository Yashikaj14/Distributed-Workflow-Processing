package com.company;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class SimpleScheduler implements WorkflowScheduler{

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
        PriorityQueue<Task> pq = new PriorityQueue<>(Comparator.comparingInt(Task::getCost).reversed());
        Queue<Task> taskQueue = new PriorityQueue<>(Comparator.comparingLong(Task::getCompleted_at));
        boolean[] workers = new boolean[worker_count];
        for(int i=0;i<worker_count;i++)
            workers[i] = false;
//        Map<Workflow, Integer> levelProcessed = new HashMap<>();
//        for(Workflow workflow: workflows)
//            levelProcessed.put(workflow, 0);

        pq.addAll(workflows[0].level_graph.get(0));
        int totalProcessed = 0;


        int totalCount = 0;
        for(Workflow workflow: workflows){
            totalCount += workflow.tasks.size();
        }
        while(totalProcessed < totalCount){
            boolean prs = true;
            for(Workflow workflow: workflows){
                if(workflow.getScheduled_at() <= time){
                    for(Task task: workflow.getTasks()){
                        if(!task.isProcessed() && !pq.contains(task) && !taskQueue.contains(task)){
                            boolean flag = true;
                            for (String dep: task.getDependencies()){
                                if(!workflow.taskMap.get(dep).isProcessed()){
                                    flag = false;
                                    break;
                                }
                            }
                            if (flag)
                                pq.add(task);
                        }
                    }
                }
            }
            while(!pq.isEmpty() && (taskQueue.size() < worker_count)){

                Task t = pq.poll();
                t.setStarted_at(time);
                t.setCompleted_at(time + t.getCost());
                taskQueue.add(t);
                for(int i=0;i<worker_count;i++){
                    if(!workers[i]){
                        t.setWorker("w"+(i+1));
                        workers[i] = true;
                        break;
                    }
                }
            }
            boolean flag = true;
            while (!taskQueue.isEmpty() && flag){
                if(taskQueue.peek().getCompleted_at() <= time){
                    Task t = taskQueue.poll();
                    t.setProcessed(true);
                    totalProcessed++;
                    prs = false;
                    int worker = t.getWorker().charAt(1) - '1';
                    workers[worker] = false;
//                    if(t!=null){
//
//                    }
//                    else {
//                        break;
//                    }
                }
                else {
                    flag = false;
                }
            }
            if(prs)
                time++;
        }
    }
}
