package calculadora;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RaizTest {
    private static String URL = "https://www.calculadoraonline.com.br/basica";
    private static WebDriver driver;
    private static NodeList listaProcedimentos;
    private static Document documento;

    @BeforeAll
    public static void setup() {
        driver = new FirefoxDriver();
        driver.get(URL);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        try {
            File inputFile = new File("src/test/resources/casos_raiz.xml");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            documento = documentBuilder.parse(inputFile);
            documento.getDocumentElement().normalize();

            listaProcedimentos = documento.getElementsByTagName("procedimento");
        } catch (Exception e) {
            System.out.println("Erro na leitura do documento");
        }

        Actions actions = new Actions(driver);
        WebElement listaArredondamento = driver.findElement(By.cssSelector(".uk-navbar2-nav > li:nth-child(2)"));

        // hover no elemento arredondamento
        actions.moveToElement(listaArredondamento).perform();
        WebElement arredondamentoTresCasas = listaArredondamento.findElement(By.cssSelector("div ul li:nth-child(4)"));
        actions.click(arredondamentoTresCasas).perform();
    }

    @AfterAll
    private static void tearDown() {
        driver.close();
    }

    @Test
    public void raizTeste() {
        for (int procedimentoIndex = 0; procedimentoIndex < listaProcedimentos.getLength(); procedimentoIndex++) {
            Element procedimento = (Element) listaProcedimentos.item(procedimentoIndex);
            NodeList casos = procedimento.getElementsByTagName("caso");

            for (int casoIndex = 0; casoIndex < casos.getLength(); casoIndex++) {
                Element caso = (Element) casos.item(casoIndex);
                WebElement painel = driver.findElement(By.id("TIExp"));

                String resultado = caso.getElementsByTagName("resultado").item(0).getTextContent();
                String numero = caso.getElementsByTagName("numero").item(0).getTextContent();

                WebElement raizBotao = driver.findElement(By.id("b21"));
                painel.sendKeys(numero);
                raizBotao.click();

                assertEquals(resultado, painel.getAttribute("value"), caso.getAttribute("descricao"));

                this.limparResultado();
            }
        }
    }

    private void limparResultado() {
        String limparValue = "C";
        String xpathExpression = "//table[@class='comandos']//td[text()='" + limparValue + "']";
        WebElement botaoLimpar = driver.findElement(By.xpath(xpathExpression));
        botaoLimpar.click();
    }
}
