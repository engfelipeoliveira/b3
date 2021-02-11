package br.com.selenium.service;

import java.time.LocalDate;
import java.util.List;

import br.com.selenium.model.SaldoEstrangeiro;

public interface SaldoEstrangeiroService {

	void save(List<SaldoEstrangeiro> saldoEstrangeiros) throws Exception;
	SaldoEstrangeiro getByCodigoAndData(Integer codigo, LocalDate data);

}
