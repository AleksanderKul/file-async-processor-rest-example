package pl.kurs.java.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kurs.java.repository.TaskRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public void putTask(final UUID jobId, final Future<String> fTask) {
        taskRepository.putTask(jobId, fTask);
    }

    public Optional<Future<String>> get(final UUID jobId) {
        return taskRepository.get(jobId);
    }
}
