package com.example.tasktracker.repository;

import com.example.tasktracker.entity.TaskActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskActivityRepo extends JpaRepository<TaskActivity, Long> {
    List<TaskActivity> findByTaskIdOrderByPerformedAtDesc(Long taskId);
}