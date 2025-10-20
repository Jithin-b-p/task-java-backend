package com.kaiburr.taskapi.service;

import com.kaiburr.taskapi.domain.*;
import com.kaiburr.taskapi.infrastructure.TaskRepository;


import io.kubernetes.client.openapi.*;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;

import com.kaiburr.taskapi.util.CommandValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Task executeTaskInPod(String id) throws Exception {
        Task task = repo.findById(id).orElseThrow();
        TaskExecution exec = new TaskExecution();
        exec.setStartTime(new Date());

        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);
        CoreV1Api api = new CoreV1Api();

        String podName = "task-" + UUID.randomUUID().toString().substring(0, 5);
        String namespace = "default";

        V1Pod pod = new V1Pod()
                .metadata(new V1ObjectMeta().name(podName))
                .spec(new V1PodSpec()
                        .restartPolicy("Never")
                        .containers(List.of(new V1Container()
                                .name("busybox")
                                .image("busybox")
                                .command(List.of("sh", "-c", task.getCommand()))
                        ))
                );

        api.createNamespacedPod(namespace, pod);

        while (true) {
            V1Pod current = api.readNamespacedPod(podName, namespace).execute();
            String phase = current.getStatus().getPhase();
            if ("Succeeded".equals(phase) || "Failed".equals(phase)) break;
            Thread.sleep(1000);
        }

        String logs = String.valueOf(api.readNamespacedPodLog(podName, namespace));
        api.deleteNamespacedPod(podName, namespace);

        exec.setEndTime(new Date());
        exec.setOutput(logs);
        task.getTaskExecutions().add(exec);
        return repo.save(task);
    }
}
