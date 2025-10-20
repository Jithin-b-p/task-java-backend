package com.kaiburr.taskapi.service;

import com.kaiburr.taskapi.domain.*;
import com.kaiburr.taskapi.infrastructure.TaskRepository;


import io.kubernetes.client.openapi.*;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;

import com.kaiburr.taskapi.util.CommandValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class TaskService {

    @Autowired
    private TaskRepository repo;

    public List<Task> getAllTasks() {
        return repo.findAll();
    }

    public Optional<Task> getTaskById(String id) {
        return repo.findById(id);
    }

    public List<Task> searchByName(String name) {
        return repo.findByNameContainingIgnoreCase(name);
    }

    public Task saveTask(Task task) {
        CommandValidator.validate(task.getCommand());
        return repo.save(task);
    }

    public void deleteTask(String id) {
        repo.deleteById(id);
    }

    public Task executeTaskLocally(String id) {
        Task task = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with ID: " + id));

        try {
            CommandValidator.validate(task.getCommand());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid command: " + e.getMessage());
        }

        TaskExecution execution = new TaskExecution();
        execution.setStartTime(new Date());

        try {
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", task.getCommand());
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            execution.setEndTime(new Date());
            execution.setOutput("Exit Code: " + exitCode + "\n" + output.toString());

        } catch (IOException | InterruptedException e) {
            execution.setEndTime(new Date());
            execution.setOutput("Error: " + e.getMessage());
        }

        task.getTaskExecutions().add(execution);
        return repo.save(task);
    }
}
