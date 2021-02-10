package pl.kurs.java.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.kurs.java.model.BankInfo;
import pl.kurs.java.model.response.JobIdResponse;
import pl.kurs.java.service.BankService;
import pl.kurs.java.service.TaskService;
import pl.kurs.java.task.BankProcessAsyncTask;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bank")
public class BankController {

    private final ExecutorService executorService;
    private final BankService bankService;
    private final TaskService taskService;

    @PostMapping("/import")
    public ResponseEntity<JobIdResponse> getResult(final MultipartFile file) throws IOException {
        final UUID jobId = UUID.randomUUID();
        final Future<String> fTask = executorService.submit(new BankProcessAsyncTask(jobId, bankService, file.getInputStream()));
        taskService.putTask(jobId, fTask);
        return new ResponseEntity<>(new JobIdResponse(jobId), HttpStatus.OK);
    }

    @GetMapping("/job/running/{jobId}")
    public ResponseEntity<Boolean> isRunning(@PathVariable("jobId") final UUID jobId) {
        return taskService.get(jobId)
                .map(future -> ResponseEntity.ok(!future.isDone()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/job/status/{jobId}")
    public ResponseEntity<String> status(@PathVariable("jobId") final UUID jobId) {
        return taskService.get(jobId)
                .map(this::checkStatus)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<BankInfo>> readData() {
        return new ResponseEntity<>(bankService.findAll(), HttpStatus.OK);
    }

    @SneakyThrows
    private ResponseEntity<String> checkStatus(Future<String> future) {
        if (!future.isDone()) {
            return ResponseEntity.badRequest().body("Job is still running");
        } else {
            return ResponseEntity.ok(future.get());
        }
    }
}
