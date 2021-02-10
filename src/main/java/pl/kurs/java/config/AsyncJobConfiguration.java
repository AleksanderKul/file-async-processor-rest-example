package pl.kurs.java.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AsyncJobConfiguration {

    @Value("${bank.app.threads}")
    private int threadsCount;

    @Bean
    public ExecutorService executors(){
        return Executors.newFixedThreadPool(threadsCount);
    }
}
