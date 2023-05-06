package com.uniovi.sdi2223entrega2test62;


import com.mongodb.util.JSON;
import com.uniovi.sdi2223entrega2test62.mongo.MongoDB;
import com.uniovi.sdi2223entrega2test62.pageobjects.*;
import com.uniovi.sdi2223entrega2test62.util.SeleniumUtils;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Sdi2223Entrega2TestApplicationTests {
    static String PathFirefox = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
 //   static String Geckodriver = "C:\\\\Users\\\\UO282874\\\\OneDrive - Universidad de Oviedo\\\\3º\\\\SDI\\\\Lab\\\\Sesion4\\\\PL-SDI-Sesión5-material\\\\PL-SDI-Sesio╠ün5-material\\\\geckodriver-v0.30.0-win64.exe";
  static String Geckodriver = "C:\\Users\\dani\\Downloads\\geckodriver-v0.30.0-win64\\geckodriver.exe";

//Común a Windows y a MACOSX
    static WebDriver driver = getDriver(PathFirefox, Geckodriver);
    static String URL = "http://localhost:8080";
    static MongoDB mongoDB = new MongoDB();

    public static WebDriver getDriver(String PathFirefox, String Geckodriver) {
        System.setProperty("webdriver.firefox.bin", PathFirefox);
        System.setProperty("webdriver.gecko.driver", Geckodriver);
        driver = new FirefoxDriver();
        return driver;
    }

    @BeforeEach
    public void setUp() {
        driver.navigate().to(URL);
        mongoDB.resetMongo();
    }

    //Después de cada prueba se borran las cookies del navegador
    @AfterEach
    public void tearDown() {
        driver.manage().deleteAllCookies();
    }

    //Antes de la primera prueba
    @BeforeAll
    static public void begin() {
    }

    //Al finalizar la última prueba
    @AfterAll
    static public void end() {
//Cerramos el navegador al finalizar las pruebas
        driver.quit();
    }

    @Test
    @Order(1)
    void PR01() {
        //Vamos al formulario de registro
        PO_HomeView.clickOption(driver, "/users/signup", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario.
        PO_SignUpView.fillForm(driver, "test@email.com", "Luis", "Perez", "2001-01-01", "123456","123456");
        //Comprobamos que entramos en la sección privada y nos nuestra el texto a buscar
        String checkText = "Offers";
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.get(0).getText());
    }

    @Test
    @Order(2)
    public void PR02() {
        //Vamos al formulario de registro
        PO_HomeView.clickOption(driver, "/users/signup", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario.
        PO_SignUpView.fillForm(driver, "", "", "", "", "123456","123456");

        //Comprobamos que se notifican los errores
        String checkText = "Email is required, Name is required, Surnames are required, Invalid date";
        List<WebElement> result = PO_View.checkElementBy(driver, "class", "alert alert-danger ");

        Assertions.assertEquals(checkText, result.get(0).getText());
    }

    @Test
    @Order(3)
    public void PR03() {
        //Vamos al formulario de registro
        PO_HomeView.clickOption(driver, "/users/signup", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario.
        PO_SignUpView.fillForm(driver, "test3@email.com", "test3Name", "test3Surname", "2001-01-01", "123","12");

        //Comprobamos que se notifican los errores
        String checkText = "Passwords do not match";
        List<WebElement> result = PO_View.checkElementBy(driver, "class", "alert alert-danger ");

        Assertions.assertEquals(checkText, result.get(0).getText());
    }

    @Test
    @Order(4)
    public void PR04() {
        //Vamos al formulario de registro
        PO_HomeView.clickOption(driver, "/users/signup", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario.
        PO_SignUpView.fillForm(driver, "user01@email.com", "user01Name", "user01Surname", "2001-01-01", "123","123");

        //Comprobamos que se notifican los errores
        String checkText = "User already exists";
        List<WebElement> result = PO_View.checkElementBy(driver, "class", "alert alert-danger ");

        Assertions.assertEquals(checkText, result.get(0).getText());
    }

    @Test
    @Order(5)
    public void PR05() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login de un usuario administrador
        PO_LoginView.fillLoginForm(driver,"admin@email.com","admin");

        //Comprobamos que entramos a la vista: “listado de todos los usuarios de la aplicación”
        String checkText = "List of Users";
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.get(0).getText());
    }

    @Test
    @Order(6)
    public void PR06() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario estándar).
        PO_LoginView.fillLoginForm(driver,"user01@email.com","123456");

        //Comprobamos que entramos a la vista:“listado de ofertas propias”
        String checkText = "Offers";
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.get(0).getText());
    }

    @Test
    @Order(7)
    public void PR07() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos inválidos
        // (usuario estándar, email existente, pero contraseña incorrecta).
        PO_LoginView.fillLoginForm(driver,"user01@email.com","12");

        //Comprobamos que se notifican los errores
        String checkText = "Email o password incorrecto";
        List<WebElement> result = PO_View.checkElementBy(driver, "class", "alert alert-danger ");

        Assertions.assertEquals(checkText, result.get(0).getText());
    }

    @Test
    @Order(8)
    public void PR08() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos inválidos (campo email o contraseña vacíos).
        PO_LoginView.fillLoginForm(driver,"user01@email.com","");

        //Comprobamos que se notifican los errores
        String checkText = "Email o password incorrecto";
        List<WebElement> result = PO_View.checkElementBy(driver, "class", "alert alert-danger ");

        Assertions.assertEquals(checkText, result.get(0).getText());
    }

    @Test
    @Order(9)
    public void PR09() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario estándar).
        PO_LoginView.fillLoginForm(driver,"user01@email.com","123456");

        //Pulsamos la opcion de logout
        PO_HomeView.clickOption(driver, "/users/logout", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[2]/a");

        //Comprobamos que vamos a la vista de login
        String checkText = "Log In";
        List<WebElement> result = PO_View.checkElementBy(driver, "free", "/html/body/div/h2");
        Assertions.assertEquals(checkText, result.get(0).getText());

    }

    @Test
    @Order(10)
    public void PR10() {
        //Comprobamos que el boton logout no esta visible si no estas en sesion
        List<WebElement> result = new ArrayList<WebElement>();
        try{
             result = PO_View.checkElementBy(driver, "text", "Logout");
        }catch(TimeoutException e){

        }

        Assertions.assertEquals(0, result.size());
    }

// TODO no funciona
    @Test
    @Order(11)
    public void PR11() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario administrador).
        PO_LoginView.fillLoginForm(driver,"admin@email.com","admin");

        //Comprobamos que entramos a la vista:“listado de usuarios”
        String checkText = "List of Users";
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.get(0).getText());

        // Obtener la lista de usuarios
        List<WebElement> markList = SeleniumUtils.waitLoadElementsBy(driver, "free", "/html/body/div[1]/form/div[1]/table/tbody/tr",
                PO_View.getTimeout());
        // Comprobar length
        assertEquals(16, markList.size());

    }

    /**
     * [Prueba16] Ir al formulario de alta de oferta, rellenarla con datos válidos y pulsar el botón Submit.
     * Comprobar que la oferta sale en el listado de ofertas de dicho usuario.
     */
    @Test
    @Order(16)
    public void PR16() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario estandar).
        PO_LoginView.fillLoginForm(driver,"user01@email.com","123456");

        List<WebElement> result = PO_View.checkElementBy(driver,"free","/html/body/div/div[2]/a");
        result.get(0).click();

        PO_PrivateView.fillFormAddOffer(driver,"Titulo test16","test16","5.50",false);

        //Comprobamos que se añade la oferta
        String checkText = "Titulo test16";
        List<WebElement> offerTitle = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, offerTitle.get(0).getText());

    }

    /**
     * [Prueba17] Ir al formulario de alta de oferta, rellenarla con datos inválidos (campo título vacío y precio
     * en negativo) y pulsar el botón Submit. Comprobar que se muestra el mensaje de campo inválido.
     */
    @Test
    @Order(17)
    public void PR17() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario estandar).
        PO_LoginView.fillLoginForm(driver,"user01@email.com","123456");

        List<WebElement> result = PO_View.checkElementBy(driver,"free","/html/body/div/div[2]/a");
        result.get(0).click();

        PO_PrivateView.fillFormAddOffer(driver,"","test17","-5.50",false);

        //Comprobamos que se notifican los errores
        String checkText = "Title cant be empty, Price must be a positive number";
        List<WebElement> errorsResult = PO_View.checkElementBy(driver, "class", "alert alert-danger ");

        Assertions.assertEquals(checkText, errorsResult.get(0).getText());

    }

    /**
     * [Prueba18] Mostrar el listado de ofertas para dicho usuario y comprobar que se muestran todas las que
     * existen para este usuario.
     */
    @Test
    @Order(18)
    public void PR18() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario estandar).
        PO_LoginView.fillLoginForm(driver,"user01@email.com","123456");


        // Comprobamos que la primera página este llena (5 ofertas)
        List<WebElement> offers = PO_View.checkElementBy(driver, "free", "/html/body/div/table[1]/tbody/tr");
        Assertions.assertEquals(5, offers.size());

        // Pasamos a la pagina 2
        PO_View.checkElementBy(driver,"free","//*[@id=\"pi-2\"]/a").get(0).click();

        // Comprobamos que segunda página este llena (5 ofertas)
        List<WebElement> offers2 = PO_View.checkElementBy(driver, "free", "/html/body/div/table[1]/tbody/tr");
        Assertions.assertEquals(5, offers2.size());

    }

    /**
     * [Prueba19] Ir a la lista de ofertas, borrar la primera oferta de la lista, comprobar que la lista se actualiza
     *  y que la oferta desaparece.
     */
    @Test
    @Order(19)
    public void PR19() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario estandar).
        PO_LoginView.fillLoginForm(driver,"user01@email.com","123456");

        // Borramos la primera oferta
        PO_View.checkElementBy(driver,"free","/html/body/div/table[1]/tbody/tr[1]/td[5]/a").get(0).click();

        //Comprobamos que se borra
        String checkText = "offer10User1Details";
        SeleniumUtils.textIsNotPresentOnPage(driver,checkText);


    }

    /**
     * Prueba20] Ir a la lista de ofertas, borrar la última oferta de la lista, comprobar que la lista se actualiza
     * y que la oferta desaparece.
     */
    @Test
    @Order(20)
    public void PR20() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario estandar).
        PO_LoginView.fillLoginForm(driver,"user01@email.com","123456");

        // Borramos la ultima oferta
        PO_View.checkElementBy(driver,"free","/html/body/div/table[1]/tbody/tr[5]/td[5]/a").get(0).click();

        //Comprobamos que se borra
        String checkText = "offer4User1Details";
        SeleniumUtils.textIsNotPresentOnPage(driver,checkText);


    }


    /**
     * [Prueba23] Hacer una búsqueda con el campo vacío y comprobar que se muestra la página que
     * corresponde con el listado de las ofertas existentes en el sistema
     */
    @Test
    @Order(23)
    public void PR23() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario estandar).
        PO_LoginView.fillLoginForm(driver,"user01@email.com","123456");

        // Clickamos en el buscador sin introducir ningun texto
        PO_View.checkElementBy(driver,"free","//*[@id=\"custom-search-input \"]/form/div/span/button").get(0).click();

        // Comprobamos que siguen habiendo 5 ofertas en la pagina
        List<WebElement> offers = PO_View.checkElementBy(driver, "free", "/html/body/div/table[1]/tbody/tr");
        Assertions.assertEquals(5, offers.size());

        //Comprobamos que siguen siendo las mismas
        String checkText = "offer10User1Details";
        SeleniumUtils.textIsPresentOnPage(driver,checkText);


    }

    /**
     * [Prueba24] Hacer una búsqueda escribiendo en el campo un texto que no exista y comprobar que se
     * muestra la página que corresponde, con la lista de ofertas vacía.
     */
    @Test
    @Order(24)
    public void PR24() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario estandar).
        PO_LoginView.fillLoginForm(driver,"user01@email.com","123456");

        //Rellenamos el input a buscar con una oferta inexistente
        WebElement search = driver.findElement(By.name("search"));
        search.click();
        search.clear();
        search.sendKeys("oferta365");

        // Clickamos en el buscador
        PO_View.checkElementBy(driver,"free","//*[@id=\"custom-search-input \"]/form/div/span/button").get(0).click();

        // Comprobamos que la tabla esta vacia
        List<WebElement> rows = driver.findElements(By.xpath("//table[@class='table table-hover']/tbody/tr"));
        Assertions.assertTrue(rows.isEmpty());

    }


    /**
     * [Prueba25] Hacer una búsqueda escribiendo en el campo un texto en minúscula o mayúscula y
     * comprobar que se muestra la página que corresponde, con la lista de ofertas que contengan dicho
     * texto, independientemente que el título esté almacenado en minúsculas o mayúscula.
     */
    @Test
    @Order(25)
    public void PR25() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario estandar).
        PO_LoginView.fillLoginForm(driver,"user01@email.com","123456");

        //Rellenamos el input a buscar con una oferta existente pero escrita en mayusculas
        WebElement search = driver.findElement(By.name("search"));
        search.click();
        search.clear();
        search.sendKeys("OFFER10USER10");

        // Clickamos en el buscador
        PO_View.checkElementBy(driver,"free","//*[@id=\"custom-search-input \"]/form/div/span/button").get(0).click();

        // Comprobamos que esta la oferta
        String checkText = "offer10User10";
        SeleniumUtils.textIsPresentOnPage(driver,checkText);

    }

    /**
    * [Prueba30] Al crear una oferta, marcar dicha oferta como destacada y a continuación comprobar: i)
    * que aparece en el listado de ofertas destacadas para los usuarios y que el saldo del usuario se
    * actualiza adecuadamente en la vista del ofertante (comprobar saldo antes y después, que deberá
    * diferir en 20€).
     */
    @Test
    @Order(30)
    public void PR30() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario estandar).
        PO_LoginView.fillLoginForm(driver,"user01@email.com","123456");

        //Accedemos al formulario de añadir ofertas
        List<WebElement> result = PO_View.checkElementBy(driver,"free","/html/body/div/div[2]/a");
        result.get(0).click();

        // Creamos la oferta
        PO_PrivateView.fillFormAddOffer(driver,"Titulo test30","test30","6.50",true);

        // Comprobamos que se añade a la tabla de highlited offers
        List<WebElement> offers = PO_View.checkElementBy(driver, "free", "/html/body/div/table[2]/tbody/tr");
        Assertions.assertEquals(1, offers.size());

        //Comprobamos que se ha reducido el wallet
        WebElement wallet  = driver.findElement(By.xpath("/html/body/nav/div/div[2]/ul[2]/li[1]/a/span"));
        Assertions.assertEquals("Wallet: 80",wallet.getText());

    }

    /**
     * [Prueba31] Sobre el listado de ofertas de un usuario con más de 20 euros de saldo, pinchar en el enlace
     * Destacada y a continuación comprobar: i) que aparece en el listado de ofertas destacadas para los
     * usuarios y que el saldo del usuario se actualiza adecuadamente en la vista del ofertante (comprobar
     * saldo antes y después, que deberá diferir en 20€ ).
     */
    @Test
    @Order(31)
    public void PR31() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario estandar).
        PO_LoginView.fillLoginForm(driver,"user01@email.com","123456");

        // Marcamos la primera oferta como highlighted
        PO_View.checkElementBy(driver,"free","/html/body/div/table/tbody/tr[1]/td[6]/a").get(0).click();

        // Comprobamos que se añade a la tabla de highlited offers
        List<WebElement> offers = PO_View.checkElementBy(driver, "free", "/html/body/div/table[2]/tbody/tr");
        Assertions.assertEquals(1, offers.size());

        //Comprobamos que se ha reducido el wallet
        WebElement wallet  = driver.findElement(By.xpath("/html/body/nav/div/div[2]/ul[2]/li[1]/a/span"));
        Assertions.assertEquals("Wallet: 80",wallet.getText());

    }

    /**
     * [Prueba32] Sobre el listado de ofertas de un usuario con menos de 20 euros de saldo, pinchar en el
     * enlace Destacada y a continuación comprobar que se muestra el mensaje de saldo no suficiente.
     */
    @Test
    @Order(32)
    public void PR32() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario estandar).
        PO_LoginView.fillLoginForm(driver,"user15@email.com","123456");

        // Marcamos la primera oferta como highlighted
        PO_View.checkElementBy(driver,"free","/html/body/div/table/tbody/tr[1]/td[6]/a").get(0).click();

        // Comprobamos que salta el mensaje de error
        String checkText = "Error when highlighting offer, not enough money on wallet.";
        SeleniumUtils.textIsPresentOnPage(driver,checkText);

    }

    /**
     * [Prueba33] Intentar acceder sin estar autenticado a la opción de listado de usuarios. Se deberá volver
     * al formulario de login.
     */
    @Test
    @Order(33)
    public void PR33() {
        //Accedemos a la url
        driver.get("http://localhost:8080/users/list");

        //Comprobamos que se nos redirije a la pagina del login
        String checkText = "Log In";
        SeleniumUtils.textIsPresentOnPage(driver,checkText);
    }

    /**
     * [Prueba34] Intentar acceder sin estar autenticado a la opción de listado de conversaciones
     * [REQUISITO OBLIGATORIO S5]. Se deberá volver al formulario de login.
     */
    @Test
    @Order(34)
    public void PR34() {
        //Accedemos a la url
        driver.get("http://localhost:8080/api/offers/conversations");

        //Comprobamos que se nos redirije a la pagina del login
        String checkText = "Log In";
        SeleniumUtils.textIsPresentOnPage(driver,checkText);
    }

    /**
     * [Prueba35] Estando autenticado como usuario estándar intentar acceder a una opción disponible solo
     * para usuarios administradores (Añadir menú de auditoria (visualizar logs)). Se deberá indicar un
     * mensaje de acción prohibida.
     */
    @Test
    @Order(35)
    public void PR35() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario estandar).
        PO_LoginView.fillLoginForm(driver,"user01@email.com","123456");

        //Accedemos a la url
        driver.get("http://localhost:8080/log/list");

        //Comprobamos que se nos muestra el error
        String checkText = "You need role admin to enter that URL";
        SeleniumUtils.textIsPresentOnPage(driver,checkText);
    }


    /**
     * [Prueba36] Estando autenticado como usuario administrador visualizar todos los logs generados en
     * una serie de interacciones. Esta prueba deberá generar al menos dos interacciones de cada tipo y
     * comprobar que el listado incluye los logs correspondientes.
     */
    @Test
    @Order(36)
    public void PR36() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario administrador).
        PO_LoginView.fillLoginForm(driver,"admin@email.com","admin");

        //Accedemos a la url
        driver.get("http://localhost:8080/log/list");

        //Comprobamos que estan las peticiones
        String pet1 = "/log/list GET {}";
        String pet2 = "/users/login GET {}";

        SeleniumUtils.textIsPresentOnPage(driver,pet1);
        SeleniumUtils.textIsPresentOnPage(driver,pet2);
    }

    /**
     * [Prueba37] Estando autenticado como usuario administrador, ir a visualización de logs, pulsar el
     * botón/enlace borrar logs y comprobar que se eliminan los logs de la base de datos.
     */
    @Test
    @Order(37)
    public void PR37() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario administrador).
        PO_LoginView.fillLoginForm(driver,"admin@email.com","admin");

        //Accedemos a la url
        driver.get("http://localhost:8080/log/list");

       //Pulsamos el boton de eliminar todos los logs
        PO_View.checkElementBy(driver,"free","/html/body/div/form/button").get(0).click();

        //Comprobamos que solo esta presente el log relativo a el borrado de estos
        SeleniumUtils.textIsPresentOnPage(driver, "/log/list GET {}");

        List<WebElement> peticion = PO_View.checkElementBy(driver,"free","//*[@id=\"tableLogs\"]/tbody/tr");
        Assertions.assertEquals(1,peticion.size());
    }


    // ----------------------------------------------------------------------------------------------------
    // TESTING REST-API
    // ----------------------------------------------------------------------------------------------------


    // [Prueba38] Inicio de sesión con datos válidos
    @Test
    @Order(38)
    public void PR38() {
        final String RestAssuredURL = "http://localhost:8080/api/users/login";
        //2. Preparamos el parámetro en formato JSON
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", "user01@email.com");
        requestParams.put("password", "123456");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        //3. Hacemos la petición
        Response response = request.post(RestAssuredURL);
        //4. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(200, response.getStatusCode());
    }

    // [Prueba39] Inicio de sesión con datos inválidos (email existente, pero contraseña incorrecta)
    @Test
    @Order(39)
    public void PR39() {
        final String RestAssuredURL = "http://localhost:8080/api/users/login";
        //2. Preparamos el parámetro en formato JSON
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", "user01@email.com");
        requestParams.put("password", "incorrectPassword");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        //3. Hacemos la petición
        Response response = request.post(RestAssuredURL);
        //4. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(401, response.getStatusCode());
    }

    // [Prueba40] Inicio de sesión con datos inválidos (campo email o contraseña vacíos)
    @Test
    @Order(40)
    public void PR40() {
        final String RestAssuredURL = "http://localhost:8080/api/users/login";
        //2. Preparamos el parámetro en formato JSON
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", "user01@email.com");
        requestParams.put("password", "");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        //3. Hacemos la petición
        Response response = request.post(RestAssuredURL);
        //4. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(401, response.getStatusCode());
    }

    // [Prueba41] Mostrar el listado de ofertas para dicho usuario y comprobar que se muestran todas las que
    // existen para este usuario. Esta prueba implica invocar a dos servicios: S1 y S2.
    @Test
    @Order(41)
    public void PR41() {
        final String RestLoginAssuredURL = "http://localhost:8080/api/users/login";
        //2. Preparamos el parámetro en formato JSON
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", "user01@email.com");
        requestParams.put("password", "123456");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        //3. Hacemos la petición y comprobamos que devuelve 200
        Response response = request.post(RestLoginAssuredURL);
        Assertions.assertEquals(200, response.getStatusCode());
        //4. Obtenemos el token de autorización
        ResponseBody body = response.body();
        String token = body.path("token");

        //5. Preparamos la peticion de ofertas
        final String RestOffersAssuredURL = "http://localhost:8080/api/offers";
        request = RestAssured.given();
        request.header("token", token);
        //6. Hacemos la petición y comprobamos que devuelve 200
        response = request.get(RestOffersAssuredURL);
        Assertions.assertEquals(200, response.getStatusCode());
        // 7. Obtenemos las ofertas
        body = response.body();
        List<String> offers = body.path("offers");
        Assertions.assertEquals(140, offers.size());
    }

    // [Prueba42] Enviar un mensaje a una oferta. Esta prueba consistirá en comprobar que el servicio
    // almacena correctamente el mensaje para dicha oferta. Por lo tanto, el usuario tendrá que
    // identificarse (S1), enviar un mensaje para una oferta de id conocido (S3) y comprobar que el
    // mensaje ha quedado bien registrado (S4).
    @Test
    @Order(42)
    public void PR42() {
        final String RestLoginAssuredURL = "http://localhost:8080/api/users/login";
        //2. Preparamos el parámetro en formato JSON
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", "user01@email.com");
        requestParams.put("password", "123456");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        //3. Hacemos la petición y comprobamos que devuelve 200
        Response response = request.post(RestLoginAssuredURL);
        Assertions.assertEquals(200, response.getStatusCode());
        //4. Obtenemos el token de autorización
        ResponseBody body = response.body();
        String token = body.path("token");


        //5. Preparamos la peticion de ofertas
        final String RestOffersAssuredURL = "http://localhost:8080/api/offers";
        request = RestAssured.given();
        request.header("token", token);
        //6. Hacemos la petición y comprobamos que devuelve 200
        response = request.get(RestOffersAssuredURL);
        Assertions.assertEquals(200, response.getStatusCode());
        // 7. Obtenemos las ofertas
        body = response.body();
        HashMap offer = body.path("offers[1]");
        String offerId = offer.get("_id").toString();

        //8. Preparamos la peticion de mensajes
        final String RestMessageAssuredURL = "http://localhost:8080/api/offers/" + offerId + "/messages";
        System.out.println(RestMessageAssuredURL);
        request = RestAssured.given();
        requestParams = new JSONObject();
        requestParams.put("message", "Esto es un mensaje nuevo");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        request.header("token", token);
        //9. Hacemos la petición y comprobamos que devuelve 201
        response = request.post(RestMessageAssuredURL);
        Assertions.assertEquals(201, response.getStatusCode());

        //10. Preparamos la peticion de conversaciones para obtener el mensaje
        final String RestGetMessageAssuredURL = "http://localhost:8080/api/offers/" + offerId + "/conversation";
        System.out.println(RestMessageAssuredURL);
        request = RestAssured.given();
        request.header("token", token);
        //6. Hacemos la petición y comprobamos que devuelve 200 y size == 1
        response = request.get(RestGetMessageAssuredURL);
        Assertions.assertEquals(200, response.getStatusCode());
        body = response.body();
        List<String> messages = body.path("messages");
        Assertions.assertEquals(1, messages.size());
    }
}


