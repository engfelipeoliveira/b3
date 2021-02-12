package br.com.selenium.service;

import static org.slf4j.LoggerFactory.getLogger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.selenium.model.SaldoEstrangeiro;

@Service
public class MainServiceImpl implements MainService {

	private static final Logger LOG = getLogger(MainServiceImpl.class);
	
	private SaldoEstrangeiroService saldoEstrangeiroService;
	
	public MainServiceImpl(SaldoEstrangeiroService saldoEstrangeiroService) {
		super();
		this.saldoEstrangeiroService = saldoEstrangeiroService;
	}

	@Value("${url.b3}")
	private String URL_B3;
	
	@Value("${wait.seconds}")
	private Long WAIT;
	
	private WebDriver setupSelenium() throws Exception{
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		WebDriver driver = new ChromeDriver(options);
		driver.get(URL_B3);
		
		return driver;
	}
	
	private Optional<SaldoEstrangeiro> parseRowToEntity(WebElement row, List<WebElement> cols, LocalDate date)  {
		String tipo = StringUtils.trimToEmpty(cols.get(0).getText());
		Integer codigo = 0;
		BigDecimal compra = new BigDecimal(StringUtils.trimToEmpty(cols.get(1).getText()));
		BigDecimal venda = new BigDecimal(StringUtils.trimToEmpty(cols.get(3).getText()));
		
		if("Pessoa Jurídica Financeira".equalsIgnoreCase(tipo)){
			codigo = 1;
		}else if("Bancos".equalsIgnoreCase(tipo)){
			codigo = 2;
		}else if("Investidores Não Residentes".equalsIgnoreCase(tipo)){
			codigo = 3;
		}
		
		if(codigo > 0) {
			return Optional.of(SaldoEstrangeiro.builder()
					.data(date)
					.codigo(codigo)
					.compra(compra)
					.venda(venda)
					.tipo(tipo)
					.build());	
		}else {
			return Optional.empty();
		}
					
	}

	@Override
	public void execute() throws Exception {
		WebDriver driver = this.setupSelenium();
		
		LocalDate yesterday = LocalDate.now().minusDays(1);
		Thread.sleep(2000);
		driver.findElement(By.id("dData1")).sendKeys(yesterday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		Thread.sleep(2000);
		driver.findElement(By.name("frmBD")).findElement(By.tagName("button")).click();
		Thread.sleep(2000);
		
		WebElement table = driver.findElements(By.className("responsive")).stream().filter(
				tables -> "MERCADO FUTURO DE DÓLAR".equalsIgnoreCase(tables.findElement(By.tagName("caption")).getText())).findFirst().get();
		
		
		WebElement body = table.findElement(By.tagName("tbody"));
		List<WebElement> rows = body.findElements(By.tagName("tr"));
		List<SaldoEstrangeiro> saldoEstrangeiros = new ArrayList<SaldoEstrangeiro>();
		
		rows.stream().forEach(row -> {
			List<WebElement> cols = row.findElements(By.tagName("td"));
			
			if(parseRowToEntity(row, cols, yesterday).isPresent()) {
				SaldoEstrangeiro newSaldoEstrangeiro = parseRowToEntity(row, cols, yesterday).get();
				SaldoEstrangeiro oldSaldoEstrangeiro = this.saldoEstrangeiroService.getByCodigoAndData(newSaldoEstrangeiro.getCodigo(), newSaldoEstrangeiro.getData());
				
				if(oldSaldoEstrangeiro != null) {
					newSaldoEstrangeiro.setId(oldSaldoEstrangeiro.getId());
				}
				
				saldoEstrangeiros.add(newSaldoEstrangeiro);
			}
			
		});
		LOG.info("Salvando saldo estrangeiro no BD");
		this.saldoEstrangeiroService.save(saldoEstrangeiros);
		LOG.info(saldoEstrangeiros.toString());
		
		driver.close();
		driver.quit();
		
	}
	
	

}
