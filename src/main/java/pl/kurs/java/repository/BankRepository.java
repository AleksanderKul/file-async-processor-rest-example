package pl.kurs.java.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kurs.java.model.BankInfo;

public interface BankRepository extends JpaRepository<BankInfo, Long> {
}
