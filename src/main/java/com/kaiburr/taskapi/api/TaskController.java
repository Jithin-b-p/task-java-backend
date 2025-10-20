package com.kaiburr.taskapi.api;

import com.kaiburr.taskapi.service.TaskService;
import com.kaiburr.taskapi.domain.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService service;

    @GetMapping
    public List<Task> getAll() {
        return service.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getById(@PathVariable String id) {
        return service.getTaskById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Task>> search(@RequestParam String name) {
        List<Task> results = service.searchByName(name);
        return results.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(results);
    }

    @PutMapping
    public Task create(@RequestBody Task task) {
        return service.saveTask(task);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.deleteTask(id);
    }

    @PutMapping("/{id}/execute")
    public Task execute(@PathVariable String id) throws Exception {
        return service.executeTaskInPod(id);
    }
}
