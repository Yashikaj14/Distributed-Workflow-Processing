package com.company;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class Main {

    static class byStartTime implements Comparator<Workflow>{
        @Override
        public int compare(Workflow o1, Workflow o2) {
            return (int) ((o1.getScheduled_at() - o2.getScheduled_at())%1000000007);
        }
    }

    static void process_workflow(Workflow[] workflows, int worker_count){
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

    public static void main(String[] args) {

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the Worker Count");
            int worker_count = scanner.nextInt();

            JsonElement json = parser.parse(new FileReader("./input.json"));
            Workflow[] workflows = gson.fromJson(json, Workflow[].class);
            for(Workflow workflow : workflows){
                workflow.initializeGraph();
            }


            process_workflow(workflows, worker_count);

            for(Workflow workflow: workflows){
                long maxTime = 0;
                for(Task task: workflow.getTasks()){
                    maxTime = Math.max(maxTime, task.getCompleted_at());
                }
                workflow.setCompleted_at(maxTime);
            }

            JsonElement elem = parser.parse(gson.toJson(workflows));
            Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
//            System.out.println(elem);
            BufferedWriter writer = new BufferedWriter(new FileWriter("./output.json"));
//            writer.write(elem.toString());
            gson2.toJson(elem, writer);
            writer.close();
//            System.out.println(js);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}