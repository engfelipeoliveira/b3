package br.com.selenium.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.selenium.model.SaldoEstrangeiro;

@Repository
public interface SaldoEstrangeiroRepository extends JpaRepository<SaldoEstrangeiro, Long> {

	SaldoEstrangeiro findFirstByCodigoAndData(Integer codigo, LocalDate data);
	
}
