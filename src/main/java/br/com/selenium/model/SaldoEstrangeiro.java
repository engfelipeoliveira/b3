package br.com.selenium.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder(toBuilder = true)
@Data
@Table(name="saldo_estrangeiro")
@NoArgsConstructor
@AllArgsConstructor
public class SaldoEstrangeiro implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String tipo;
	
	private Integer codigo;
	
	private BigDecimal compra;
	
	private BigDecimal venda;
	
	private LocalDate data;
	
}
