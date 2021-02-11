package br.com.selenium.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.selenium.model.SaldoEstrangeiro;
import br.com.selenium.repository.SaldoEstrangeiroRepository;

@Service
public class SaldoEstrangeiroServiceImpl implements SaldoEstrangeiroService {

	private SaldoEstrangeiroRepository saldoEstrangeiroRepository;
	
	public SaldoEstrangeiroServiceImpl(SaldoEstrangeiroRepository saldoEstrangeiroRepository) {
		super();
		this.saldoEstrangeiroRepository = saldoEstrangeiroRepository;
	}

	@Override
	public void save(List<SaldoEstrangeiro> saldoEstrangeiros) throws Exception{
		this.saldoEstrangeiroRepository.saveAll(saldoEstrangeiros);
	}

	@Override
	public SaldoEstrangeiro getByCodigoAndData(Integer codigo, LocalDate data) {
		return this.saldoEstrangeiroRepository.findFirstByCodigoAndData(codigo, data);
	}

}
