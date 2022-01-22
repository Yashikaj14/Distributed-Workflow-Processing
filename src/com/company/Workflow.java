package com.company;

import java.util.*;

public class Workflow {
    private String name;
    private Long scheduled_at = 0L;
    private Long completed_at = 0L;
    ArrayList<Task> tasks;
    transient private Map<Task, ArrayList<Task>> task_graph = new HashMap<>();
    //    transient public Map<Integer, ArrayList<Task>> level_graph = new TreeMap<>();
    transient public ArrayList<ArrayList<Task>> level_graph = new ArrayList<>();
    transient public Map<String, Task> taskMap = new HashMap<>();

    public Workflow() {
    }

    public Workflow(String name, Long scheduled_at, ArrayList<Task> tasks) {
        this.name = name;
        this.scheduled_at = scheduled_at;
        this.tasks = tasks;
    }

    public Map<Task, ArrayList<Task>> getTask_graph() {
        return task_graph;
    }

    public void setTask_graph(Map<Task, ArrayList<Task>> task_graph) {
        this.task_graph = task_graph;
    }

    private void bfs(){
        int level = 0;
        Map<Task, Boolean> vis = new HashMap<>();
        for(Task task: tasks){
            vis.put(task, false);
        }
        for(Task task: tasks){
            if(task.getDependencies().isEmpty()){
                bfs_helper(task, 1, vis);
            }
        }
    }
    private void bfs_helper(Task initialNode, int lev, Map<Task, Boolean> vis){
        Queue<Task> taskQueue = new ArrayDeque<>();
        initialNode.level = 1;
        taskQueue.add(initialNode);
        while (!taskQueue.isEmpty()){
            Task currentTask = taskQueue.poll();
            if(vis.get(currentTask))
                continue;
            vis.put(currentTask, true);
            for(Task task: task_graph.get(currentTask)){
                task.level = currentTask.level + 1;
                taskQueue.add(task);
            }
        }
    }


    public void initializeGraph(){
        /*
         * Function to initialize the graph for task
         * */

        for(Task task : tasks){
            task_graph.put(task, new ArrayList<>());
            taskMap.put(task.getName(), task);
        }
        for(Task task: tasks){
            for(String dp : task.getDependencies()){
                addEdge(taskMap.get(dp), task);
            }
        }
        bfs();
        int maxLevel = 0;
        for(Task task: tasks){
            maxLevel = Math.max(maxLevel, task.level);
        }
        for(int i=0;i<maxLevel;i++){
            level_graph.add(i, new ArrayList<>());
        }
        for(Task task: tasks){
            level_graph.get(task.level - 1).add(task);
        }
    }

    public void addEdge(Task task1, Task task2){
        task_graph.get(task1).add(task2);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getScheduled_at() {
        return scheduled_at;
    }

    public void setScheduled_at(Long scheduled_at) {
        this.scheduled_at = scheduled_at;
    }

    public Long getCompleted_at() {
        return completed_at;
    }

    public void setCompleted_at(Long completed_at) {
        this.completed_at = completed_at;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }
}