package com.company;

import com.google.common.collect.Sets;

import java.util.*;


public class OptimalScheduler implements WorkflowScheduler{

    int totalTaskCount = 0;
    long startTime =0;
    static class byStartTime implements Comparator<Workflow>{
        @Override
        public int compare(Workflow o1, Workflow o2) {
            return (int) ((o1.getScheduled_at() - o2.getScheduled_at())%1000000007);
        }
    }

    @Override
    public void processWorkflow(Workflow[] workflows, int worker_count) {
        Arrays.sort(workflows, new SimpleScheduler.byStartTime());
        startTime = workflows[0].getScheduled_at();
        Set<Task> readyTaskSet = new HashSet<>();
        Queue<Task> runningTaskQueue = new PriorityQueue<>(Comparator.comparingLong(Task::getCompleted_at));
        boolean[] workers = new boolean[worker_count];
        for(int i=0;i<worker_count;i++)
            workers[i] = false;

        readyTaskSet.addAll(workflows[0].level_graph.get(0));

        for(Workflow workflow: workflows){
            totalTaskCount += workflow.tasks.size();
        }
        process(workflows, readyTaskSet, runningTaskQueue, new HashSet<>(), worker_count, 0);
    }

    private Long process(Workflow[] workflows, Set<Task> readyTasks, Queue<Task> runningTasks, Set<String> completed, int workers, int taskProcessed){

        // input readyQueue and runningQueue and number of available workers
        //if readyQueue is not empty all workers are busy
        //take out task from running queue and push newly ready tasks to the ready queue
        // for all combinations of x tasks, x-> no of free workers
        //  move those tasks from ready queue to running queue
        //  call xyz() it returns the end time
        //  min time of all xyz calls will be the answer
        long time =startTime;
        Task processedTask = null;
        if (!runningTasks.isEmpty()) {
            processedTask  = runningTasks.peek();
            Task t = anyPendingWorkflow(workflows, processedTask.getCompleted_at());
            if(t != null){
                if (workers !=0) {
                    t.setStarted_at(time);
                    t.setCompleted_at(time + t.getCost());
                    runningTasks.add(t);
                    return process(workflows, readyTasks, runningTasks, completed, workers--, taskProcessed);
                }
                else{
                    readyTasks.add(t);
                }
            }
            runningTasks.poll();
            System.out.println("completed " + processedTask.workflow.getName() + "-" + processedTask.getName());
            completed.add(processedTask.workflow.getName() + "-" + processedTask.getName());
            processedTask.setProcessed(true);
            taskProcessed++;
            System.out.println("time " + processedTask.getCompleted_at());
            System.out.println("Count " + taskProcessed + " " + totalTaskCount);
            time = processedTask.getCompleted_at();
            if (taskProcessed == totalTaskCount)
                return time;
            workers++;
            readyTasks.addAll(getReadyTaskList(processedTask, completed));
        }

        Set<Set<Task>> combinations;
        System.out.println("Ready " + readyTasks.size() + " "+ workers);
        if(readyTasks.size() > workers)
            combinations = Sets.combinations(readyTasks, workers);
        else {
            combinations = new HashSet<>();
            Set<Task> combination = new HashSet<>();
            for(Task t : readyTasks){
                combination.add(t);
            }
            combinations.add(combination);
        }
        Set<Task> optimalCombination = null;
        Long bestTime= Long.MAX_VALUE;
        for(Set<Task> combination : combinations) {
            Iterator<Task> taskIterator = combination.iterator();
            while (taskIterator.hasNext()) {

                Task t = taskIterator.next();
                t.setStarted_at(time);
                t.setCompleted_at(time + t.getCost());
                runningTasks.add(t);
                readyTasks.remove(t);
            }
            Set<Task> readyTasksClone = cloneSet(readyTasks);
            Queue<Task> runningTasksClone = cloneQueue(runningTasks);
            System.out.println("" + readyTasksClone.size() +" " + runningTasksClone.size() + " work " + workers + " " + combination.size());
            Long endTime = process(workflows, readyTasksClone,runningTasksClone,clone(completed),workers-combination.size(), taskProcessed);
            for(Task t : combination) {
                runningTasks.remove(t);
                readyTasks.add(t);
            }
            if (endTime < bestTime) {
                bestTime = endTime;
                optimalCombination = combination;
            }
        }
        System.out.println("Solved");
        for(Task t : optimalCombination) {
            t.setStarted_at(time);
            t.setCompleted_at(time + t.getCost());
            runningTasks.add(t);
            readyTasks.remove(t);
        }
        return process(workflows, readyTasks,runningTasks,completed,workers-optimalCombination.size(), taskProcessed);


    }

    private Set<String> clone(Set<String> completed) {
        Set<String> cloned = new HashSet<>();
        for (String s :completed){
            cloned.add(s);
        }
        return cloned;
    }

    private Task anyPendingWorkflow(Workflow[] workflows, Long completed_at) {
        for(Workflow workflow: workflows){
            Task t = workflow.level_graph.get(0).get(0);
            if(t.getCompleted_at() == 0 && workflow.getScheduled_at() < completed_at) {
                return t;
            }
        }
        return null;
    }

    public static Set<Task> cloneSet(Set<Task> taskSet) {
        Set<Task> clonedList = new HashSet<>(taskSet.size());
        for (Task task : taskSet) {
            clonedList.add(new Task(task));
        }
        return clonedList;
    }

    public static Queue<Task> cloneQueue(Queue<Task> taskQueue) {
        Queue<Task> cloneTaskQueue = new PriorityQueue<>(Comparator.comparingLong(Task::getCompleted_at));
        for (Task task : taskQueue) {
            cloneTaskQueue.add(new Task(task));
        }
        return cloneTaskQueue;
    }

    private Set<Task> getReadyTaskList(Task t, Set<String> completed) {
        Set<Task> readyTaskSet = new HashSet<>();
        Workflow workflow = t.workflow;
        //System.out.println("t " + t.getName());
            for(Task task: workflow.getTasks()){
                //System.out.println("Task " + task.getName());
                if(!task.isProcessed()){
                    boolean dependenciesProcessed = true;
                    boolean isDependent= false;
                    for (String dep: task.getDependencies()){

                        if(dep.compareTo(t.getName()) == 0) {

                            isDependent = true;
                        }

                        if(!completed.contains(workflow.getName()+"-"+
                                dep)){
                            dependenciesProcessed = false;
                            break;
                        }
                    }
                    if (isDependent && dependenciesProcessed){
                        //System.out.println("adding "+t.getName());
                        readyTaskSet.add(task);
                    }
                }
            }


        return readyTaskSet;
    }
}
