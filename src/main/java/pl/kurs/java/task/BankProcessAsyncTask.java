package pl.kurs.java.task;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.kurs.java.model.BankInfo;
import pl.kurs.java.service.BankService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Getter
@Slf4j
public class BankProcessAsyncTask implements Callable<String> {

    private final UUID jobId;
    private final BankService bankService;
    private final InputStream fileInputStream;

    @Override
    public String call() throws Exception {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(fileInputStream))) {
            String inputLine;
            in.readLine();
            while ((inputLine = in.readLine()) != null) {
                String[] lines = inputLine.split(";");
                Thread.sleep(2000);
                bankService.save(BankInfo.builder()
                        .bankIdentifier(lines[1])
                        .name(lines[0])
                        .build());
            }
        } catch (Exception e) {
            log.error("Error while importing banks", e);
            return "ERROR";
        }
        return "OK";
    }
}
