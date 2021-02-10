package pl.kurs.java.repository;

import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Repository
public class TaskRepository {
    private final ConcurrentHashMap<UUID, Future<String>> tasks = new ConcurrentHashMap<>();

    public void putTask(final UUID jobId, final Future<String> fTask) {
        tasks.put(jobId, fTask);
    }

    public boolean hasJob(final UUID jobId) {
        return tasks.containsKey(jobId);
    }

    public Optional<Future<String>> get(final UUID jobId) {
        return Optional.ofNullable(tasks.get(jobId));
    }
}
