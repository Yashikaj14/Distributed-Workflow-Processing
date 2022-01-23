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

            WorkflowScheduler schduler = new SimpleScheduler();
            schduler.processWorkflow(workflows, worker_count);

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