package com.example.taskmanager.controller;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://127.0.0.1:5173"
})
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping({"", "/"})
    public List<Task> getAll() {
        return taskService.getAllTasks();
    }

    @GetMapping({"/{id}", "/{id}/"})
    public Task getById(@PathVariable Long id) {
        return taskService.getTask(id);
    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public Task create(@Valid @RequestBody Task task) {
        return taskService.createTask(task);
    }

    @PutMapping({"/{id}", "/{id}/"})
    public Task update(@PathVariable Long id, @Valid @RequestBody Task task) {
        return taskService.updateTask(id, task);
    }

    @DeleteMapping({"/{id}", "/{id}/"})
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
