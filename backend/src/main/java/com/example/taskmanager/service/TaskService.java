package com.example.taskmanager.service;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.exception.BadRequestException;
import com.example.taskmanager.exception.NotFoundException;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAllByOrderByIdAsc();
    }

    public Task getTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found"));
    }

    public Task createTask(Task task) {
        normalize(task);
        validateDueDateRule(task.getStatus(), task.getDueDate());
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task updated) {
        normalize(updated);
        validateDueDateRule(updated.getStatus(), updated.getDueDate());

        Task existing = getTask(id);
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setStatus(updated.getStatus());
        existing.setDueDate(updated.getDueDate());

        return taskRepository.save(existing);
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new NotFoundException("Not found");
        }
        taskRepository.deleteById(id);
    }

    private void normalize(Task task) {
        if (task.getTitle() != null) {
            task.setTitle(task.getTitle().trim());
        }

        if (task.getDescription() != null) {
            String description = task.getDescription().trim();
            task.setDescription(description.isEmpty() ? null : description);
        }

        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.TODO);
        }
    }

    private void validateDueDateRule(TaskStatus status, Instant dueDate) {
        if (dueDate != null && status != TaskStatus.DONE && dueDate.isBefore(Instant.now())) {
            throw new BadRequestException("Past due date/time is only allowed for completed tasks.");
        }
    }
}
