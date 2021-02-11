package br.com.selenium.service;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By.ByClassName;
import org.openqa.selenium.By.ByName;
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
	
	private Optional<SaldoEstrangeiro> parseRowToEntity(WebElement row, List<WebElement> cols)  {
		String diaHora = StringUtils.trimToEmpty(row.getAttribute("data-event-datetime"));
		if(StringUtils.isNotBlank(diaHora)) {
			String dia = StringUtils.trimToEmpty(diaHora.substring(0, 10));
			String hora = StringUtils.trimToEmpty(diaHora.substring(11, 16));
			String pais = StringUtils.trimToEmpty(cols.get(1).getText());
			String forca = StringUtils.replace(StringUtils.trimToEmpty(cols.get(2).getAttribute("data-img_key")), "bull", "");
			String evento = StringUtils.trimToEmpty(cols.get(3).getText());
			String isDiscurso = cols.get(3).findElements(ByClassName.className("audioIconNew")).isEmpty() ? "False" : "True";
			String atual = StringUtils.trimToEmpty(cols.get(4).getText());
			String projecao = StringUtils.trimToEmpty(cols.get(5).getText());
			String previo = StringUtils.trimToEmpty(cols.get(6).getText());
			
			return Optional.of(SaldoEstrangeiro.builder()
					.dia(dia)
					.hora(hora)
					.pais(pais)
					.forca(forca)
					.isDiscurso(isDiscurso)
					.evento(evento)
					.atual(atual)
					.projecao(projecao)
					.previo(previo)
					.build());			
		}else {
			return Optional.empty();
		}

	}

	@Override
	public void execute() throws Exception {
		WebDriver driver = this.setupSelenium();
		
		while(true) {
			WebElement table = driver.findElement(By.id("economicCalendarData"));
			WebElement body = table.findElement(By.tagName("tbody"));
			List<WebElement> rows = body.findElements(By.tagName("tr"));
			List<ChecadorNoticia> checadorNoticias = new ArrayList<ChecadorNoticia>();
			
			rows.stream().forEach(row -> {
				List<WebElement> cols = row.findElements(By.tagName("td"));
				
				if(parseRowToEntity(row, cols).isPresent()) {
					ChecadorNoticia newChecadorNoticia = parseRowToEntity(row, cols).get();
					ChecadorNoticia oldChecadorNoticia = this.checadorNoticiaService.getByDiaHoraPaisEvento(newChecadorNoticia.getDia(), newChecadorNoticia.getHora(), newChecadorNoticia.getPais(), newChecadorNoticia.getEvento());
					
					if(oldChecadorNoticia != null) {
						newChecadorNoticia.setId(oldChecadorNoticia.getId());
					}
					
					checadorNoticias.add(newChecadorNoticia);
				}
				
			});
			LOG.info("Salvando noticias no BD");
			this.checadorNoticiaService.save(checadorNoticias);
			LOG.info(checadorNoticias.toString());
			Thread.sleep(WAIT);
		}		
		
	}
	
	

}
