package pl.kurs.java.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kurs.java.model.BankInfo;
import pl.kurs.java.repository.BankRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankService {
    private final BankRepository bankRepository;

    public void save(final BankInfo bankInfo) {
        bankRepository.saveAndFlush(bankInfo);
    }

    public List<BankInfo> findAll() {
        return bankRepository.findAll();
    }
}
