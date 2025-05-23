/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.monge.sevenexpress.repositories;

import com.monge.sevenexpress.entities.Task;
import com.monge.sevenexpress.entities.Task.TaskReason;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author DeliveryExpress
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.taskReason = :taskReason ORDER BY t.executionDate DESC, t.id DESC")
    List<Task> findTasksByReason(@Param("taskReason") TaskReason taskReason);

    default Optional<Task> findMostRecentTaskByReason(TaskReason taskReason) {
        List<Task> tasks = findTasksByReason(taskReason);
        return tasks.isEmpty() ? Optional.empty() : Optional.of(tasks.get(0));
    }

}
