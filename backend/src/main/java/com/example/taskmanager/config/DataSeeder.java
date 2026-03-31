package com.example.taskmanager.config;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(TaskRepository taskRepository) {
        return args -> {
            if (taskRepository.count() == 0) {
                Instant now = Instant.now();

                Task task1 = new Task();
                task1.setTitle("Buy groceries");
                task1.setDescription("Milk, eggs, bread, fruit, and rice.");
                task1.setStatus(TaskStatus.TODO);
                task1.setDueDate(now.plus(1, ChronoUnit.DAYS));

                Task task2 = new Task();
                task2.setTitle("Do laundry");
                task2.setDescription("Wash clothes and fold them in the evening.");
                task2.setStatus(TaskStatus.IN_PROGRESS);
                task2.setDueDate(now.plus(8, ChronoUnit.HOURS));

                Task task3 = new Task();
                task3.setTitle("Clean the room");
                task3.setDescription("Vacuum the floor and organize the desk.");
                task3.setStatus(TaskStatus.TODO);
                task3.setDueDate(now.plus(2, ChronoUnit.DAYS));

                Task task4 = new Task();
                task4.setTitle("Pay electricity bill");
                task4.setDescription("Check the online account and make the payment.");
                task4.setStatus(TaskStatus.TODO);
                task4.setDueDate(now.plus(3, ChronoUnit.DAYS));

                Task task5 = new Task();
                task5.setTitle("Call mom");
                task5.setDescription("Ask how she is doing and check in.");
                task5.setStatus(TaskStatus.DONE);
                task5.setDueDate(now.minus(3, ChronoUnit.HOURS));

                taskRepository.saveAll(List.of(task1, task2, task3, task4, task5));
            }
        };
    }
}
