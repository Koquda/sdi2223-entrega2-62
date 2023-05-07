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

import static java.lang.Integer.parseInt;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Sdi2223Entrega2TestApplicationTests {
    static String PathFirefox = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
    static String Geckodriver = "C:\\\\Users\\\\UO282874\\\\OneDrive - Universidad de Oviedo\\\\3º\\\\SDI\\\\Lab\\\\Sesion4\\\\PL-SDI-Sesión5-material\\\\PL-SDI-Sesio╠ün5-material\\\\geckodriver-v0.30.0-win64.exe";
//     static String Geckodriver = "C:\\Users\\dani\\Downloads\\geckodriver-v0.30.0-win64\\geckodriver.exe";
//  static String Geckodriver = "C:\\Users\\sergi\\OneDrive\\Escritorio\\3º 2CUATRIMESTRE\\SDI\\Sesion 6\\PL-SDI-Sesión5-material\\geckodriver-v0.30.0-win64.exe";

//Común a Windows y a MACOSX
    static WebDriver driver = getDriver(PathFirefox, Geckodriver);
    static WebDriver driverClient = getDriverClient(PathFirefox, Geckodriver);
    static String URL = "http://localhost:8080";
    static String URLCLIENT = "http://localhost:8080/apiclient/client.html";
    static MongoDB mongoDB = new MongoDB();

    public static WebDriver getDriver(String PathFirefox, String Geckodriver) {
        System.setProperty("webdriver.firefox.bin", PathFirefox);
        System.setProperty("webdriver.gecko.driver", Geckodriver);
        driver = new FirefoxDriver();
        return driver;
    }
    public static WebDriver getDriverClient(String PathFirefox, String Geckodriver) {
        System.setProperty("webdriver.firefox.bin", PathFirefox);
        System.setProperty("webdriver.gecko.driver", Geckodriver);
        driverClient = new FirefoxDriver();
        return driverClient;
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
        driverClient.quit();
    }

    /**
     * [Prueba1] Registro de Usuario con datos válidos.
     */
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

    /**
     * [Prueba2] Registro de Usuario con datos inválidos (email, nombre, apellidos y fecha de nacimiento
     * vacíos).
     */
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

    /**
     * [Prueba3] Registro de Usuario con datos inválidos (repetición de contraseña inválida).
     */
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

    /**
     * [Prueba4] Registro de Usuario con datos inválidos (email existente).
     */
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

    /**
     * [Prueba5] Inicio de sesión con datos válidos (administrador).
     */
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

    /**
     * [Prueba6] Inicio de sesión con datos válidos (usuario estándar).
     */
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

    /**
     * [Prueba7] Inicio de sesión con datos inválidos (usuario estándar, email existente, pero contraseña
     * incorrecta).
     */
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

    /**
     * [Prueba8] Inicio de sesión con datos inválidos (campo email o contraseña vacíos).
     */
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

    /**
     * [Prueba9] Hacer click en la opción de salir de sesión y comprobar que se redirige a la página de inicio
     * de sesión (Login).
     */
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

    /**
     * [Prueba10] Comprobar que el botón cerrar sesión no está visible si el usuario no está autenticado.
     */
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

    /**
     * [Prueba11] Mostrar el listado de usuarios. Comprobar que se muestran todos los que existen en el
     * sistema, contabilizando al menos el número de usuarios.
     */
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

        int size = 0;
        // Obtener la lista de usuarios de la primera pagina
        List<WebElement> markList = SeleniumUtils.waitLoadElementsBy(driver, "free", "/html/body/div[1]/form/div[1]/table/tbody/tr",
                PO_View.getTimeout());
        // Suma el size a la variable size
        size += markList.size();
        for(int i = 2; i < 5;i++){
            // Clicar en la siguiente pagina
            PO_View.checkElementBy(driver,"free", "/html/body/div/div/ul/li["+i+"]/a").get(0).click();
            // Obtener la lista de usuarios de esa pagina
            markList = SeleniumUtils.waitLoadElementsBy(driver, "free", "/html/body/div[1]/form/div[1]/table/tbody/tr",
                    PO_View.getTimeout());
            // Suma el size a la variable size
            size += markList.size();
        }
        // Comprobar que la suma total es 16
        assertEquals(16, size);
    }

    /**
     * [Prueba12] Ir a la lista de usuarios, borrar el primer usuario de la lista, comprobar que la lista se actualiza
     * y dicho usuario desaparece.
     */
    @Test
    @Order(12)
    public void PR12() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario administrador).
        PO_LoginView.fillLoginForm(driver,"admin@email.com","admin");

        //Comprobamos que entramos a la vista:“listado de usuarios”
        String checkText = "List of Users";
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.get(0).getText());

        // Clicar en la casilla del primer usuario (el primer usuario que sale es el admin asi que selecciono el siguiente)
        PO_View.checkElementBy(driver,"free", "/html/body/div/form/div[1]/table/tbody/tr[2]/td[1]/input").get(0).click();
        // Clicar en borrar
        PO_View.checkElementBy(driver,"free", "//*[@id=\"deleteButton\"]").get(0).click();

        //Comprobamos que se borra
        checkText = "user01@email.com";
        SeleniumUtils.textIsNotPresentOnPage(driver,checkText);
    }
    /**
     * [Prueba13] Ir a la lista de usuarios, borrar el último usuario de la lista, comprobar que la lista se actualiza
     * y dicho usuario desaparece.
     */
    @Test
    @Order(13)
    public void PR13() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario administrador).
        PO_LoginView.fillLoginForm(driver,"admin@email.com","admin");

        //Comprobamos que entramos a la vista:“listado de usuarios”
        String checkText = "List of Users";
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.get(0).getText());

        //Me muevo hasta la última pagina
        for(int i = 2; i < 5;i++){
            // Clicar en la siguiente pagina
            PO_View.checkElementBy(driver,"free", "/html/body/div/div/ul/li["+i+"]/a").get(0).click();
        }
        // Clicar en la casilla del ultimo usuario
        PO_View.checkElementBy(driver,"free", "/html/body/div/form/div[1]/table/tbody/tr[1]/td[1]/input").get(0).click();
        // Clicar en borrar
        PO_View.checkElementBy(driver,"free", "//*[@id=\"deleteButton\"]").get(0).click();

        //Comprobamos que se borra
        checkText = "user15@email.com";
        SeleniumUtils.textIsNotPresentOnPage(driver,checkText);
    }

    /**
     * [Prueba14] Ir a la lista de usuarios, borrar 3 usuarios, comprobar que la lista se actualiza y dichos
     * usuarios desaparecen.
     */
    @Test
    @Order(14)
    public void PR14() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario administrador).
        PO_LoginView.fillLoginForm(driver,"admin@email.com","admin");

        //Comprobamos que entramos a la vista:“listado de usuarios”
        String checkText = "List of Users";
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.get(0).getText());

        //Seleccion los 3 usuarios
        for(int i = 2; i < 5;i++){
            // Clicar en la casilla del usuario
            PO_View.checkElementBy(driver,"free", "/html/body/div/form/div[1]/table/tbody/tr["+ i +"]/td[1]/input").get(0).click();
        }
        // Clicar en borrar
        PO_View.checkElementBy(driver,"free", "//*[@id=\"deleteButton\"]").get(0).click();

        //Comprobamos que se borra
        checkText = "user01@email.com";
        SeleniumUtils.textIsNotPresentOnPage(driver,checkText);
        checkText = "user02@email.com";
        SeleniumUtils.textIsNotPresentOnPage(driver,checkText);
        checkText = "user03@email.com";
        SeleniumUtils.textIsNotPresentOnPage(driver,checkText);
    }

    /**
     * [Prueba15] Intentar borrar el usuario que se encuentra en sesión y comprobar que no ha sido borrado
     * (porque no es un usuario administrador o bien, porque, no se puede borrar a sí mismo, si está
     * autenticado).
     */
    @Test
    @Order(15)
    public void PR15() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario administrador).
        PO_LoginView.fillLoginForm(driver,"admin@email.com","admin");

        //Comprobamos que entramos a la vista:“listado de usuarios”
        String checkText = "List of Users";
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.get(0).getText());


        // Clicar en la casilla del usuario
        PO_View.checkElementBy(driver,"free", "/html/body/div/form/div[1]/table/tbody/tr["+ 1 +"]/td[1]/input").get(0).click();

        // Clicar en borrar
        PO_View.checkElementBy(driver,"free", "//*[@id=\"deleteButton\"]").get(0).click();

        //Comprobamos que no  se borra
        checkText = "admin@email.com";
        SeleniumUtils.textIsPresentOnPage(driver,checkText);
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
        Assertions.assertEquals(5, offers.size());
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
     * Prueba22] Ir a la lista de ofertas, borrar la última oferta de la lista, comprobar que la lista se actualiza
     * y que la oferta desaparece.
     */
    @Test
    @Order(22)
    public void PR22() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario estandar).
        PO_LoginView.fillLoginForm(driver,"user01@email.com","123456");

        //Vamos a la tienda
        PO_HomeView.clickOption(driver, "/shop", "free", "/html/body/nav/div/div[2]/ul[1]/li[1]/a");
        //Comprobamos que entramos a la vista:“shop”
        String checkText = "Shop";
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.get(0).getText());

        // Compramos una oferta, oferta10User2
        PO_View.checkElementBy(driver,"free","/html/body/div/table/tbody/tr[3]/td[5]/a").get(0).click();

        //Nos salimos de sesion
        PO_HomeView.clickOption(driver, "/users/logout", "free", "/html/body/nav/div/div[2]/ul[2]/li[2]/a");

        //Rellenamos el formulario de login con datos válidos (usuario estandar).
        PO_LoginView.fillLoginForm(driver,"user02@email.com","123456");

        // Borramos la oferta
        PO_View.checkElementBy(driver,"free","/html/body/div/table/tbody/tr[3]/td[5]/a").get(0).click();

        //Comprobamos que no se borra
        checkText = "offer10User2";
        SeleniumUtils.textIsPresentOnPage(driver,checkText);
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
     * [Prueba26] Sobre una búsqueda determinada (a elección de desarrollador), comprar una oferta que
     * deja un saldo positivo en el contador del comprobador. Y comprobar que el contador se actualiza
     * correctamente en la vista del comprador.
     */
    @Test
    @Order(26)
    public void PR26() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario estandar).
        PO_LoginView.fillLoginForm(driver,"user01@email.com","123456");

        //Vamos a la tienda
        PO_HomeView.clickOption(driver, "/shop", "free", "/html/body/nav/div/div[2]/ul[1]/li[1]/a");
        //Comprobamos que entramos a la vista:“shop”
        String checkText = "Shop";
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.get(0).getText());

        //Rellenamos el input a buscar con una oferta existente pero escrita en mayusculas
        WebElement search = driver.findElement(By.name("search"));
        search.click();
        search.clear();
        search.sendKeys("OFFER10USER2");

        // Clickamos en el buscador
        PO_View.checkElementBy(driver,"free","/html/body/div/div[1]/div/form/div/span/button").get(0).click();

        // Comprobamos que esta la oferta
        checkText = "offer10User2";
        SeleniumUtils.textIsPresentOnPage(driver,checkText);
        int price  = parseInt(driver.findElement(By.xpath("/html/body/div/table/tbody/tr/td[4]")).getText());

        //Comprobamos que el wallet esta en 100
        WebElement wallet  = driver.findElement(By.xpath("/html/body/nav/div/div[2]/ul[2]/li[1]/a/span"));
        Assertions.assertEquals("Wallet: 100",wallet.getText());

        // Compramos la oferta, oferta10User2
        PO_View.checkElementBy(driver,"free","/html/body/div/table/tbody/tr/td[5]/a").get(0).click();

        //Comprobamos que se ha reducido el wallet
        wallet  = driver.findElement(By.xpath("/html/body/nav/div/div[2]/ul[2]/li[1]/a/span"));

        Assertions.assertEquals("Wallet: " +(100 - price) , wallet.getText());

    }

    /**
     * [Prueba27] Sobre una búsqueda determinada (a elección de desarrollador), comprar una oferta que
     * deja un saldo 0 en el contador del comprobador. Y comprobar que el contador se actualiza
     * correctamente en la vista del comprador.
     */
    @Test
    @Order(27)
    public void PR27() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario estandar).
        PO_LoginView.fillLoginForm(driver,"user01@email.com","123456");

        //Le damos a añadir oferta
        List<WebElement> result = PO_View.checkElementBy(driver,"free","/html/body/div/div[2]/a");
        result.get(0).click();

        PO_PrivateView.fillFormAddOffer(driver,"A1","test26","100",false);

        //Comprobamos que se añade la oferta
        String checkText = "A1";
        List<WebElement> offerTitle = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, offerTitle.get(0).getText());

        //Nos salimos de sesion
        PO_HomeView.clickOption(driver, "/users/logout", "free", "/html/body/nav/div/div[2]/ul[2]/li[2]/a");

        //Rellenamos el formulario de login con datos válidos (usuario estandar).
        PO_LoginView.fillLoginForm(driver,"user02@email.com","123456");

        //Vamos a la tienda
        PO_HomeView.clickOption(driver, "/shop", "free", "/html/body/nav/div/div[2]/ul[1]/li[1]/a");

        //Comprobamos que entramos a la vista:“shop”
        checkText = "Shop";
        result = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.get(0).getText());

        //Rellenamos el input a buscar con una oferta existente pero escrita en mayusculas
        WebElement search = driver.findElement(By.name("search"));
        search.click();
        search.clear();
        search.sendKeys("A1");

        // Clickamos en el buscador
        PO_View.checkElementBy(driver,"free","/html/body/div/div[1]/div/form/div/span/button").get(0).click();

        // Comprobamos que esta la oferta
        checkText = "A1";
        SeleniumUtils.textIsPresentOnPage(driver,checkText);
        int price  = parseInt(driver.findElement(By.xpath("/html/body/div/table/tbody/tr/td[4]")).getText());

        //Comprobamos que el wallet esta en 100
        WebElement wallet  = driver.findElement(By.xpath("/html/body/nav/div/div[2]/ul[2]/li[1]/a/span"));
        Assertions.assertEquals("Wallet: 100",wallet.getText());

        // Compramos la oferta, A1
        PO_View.checkElementBy(driver,"free","/html/body/div/table/tbody/tr/td[5]/a").get(0).click();

        //Comprobamos que se ha reducido el wallet
        wallet  = driver.findElement(By.xpath("/html/body/nav/div/div[2]/ul[2]/li[1]/a/span"));

        Assertions.assertEquals("Wallet: " +(100 - price) , wallet.getText());
    }

    /**
     * [Prueba28] Sobre una búsqueda determinada (a elección de desarrollador), intentar comprar una oferta
     * que esté por encima de saldo disponible del comprador. Y comprobar que se muestra el mensaje
     * de saldo no suficiente.
     */
    @Test
    @Order(28)
    public void PR28() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario estandar).
        PO_LoginView.fillLoginForm(driver,"user01@email.com","123456");

        //Le damos a añadir oferta
        List<WebElement> result = PO_View.checkElementBy(driver,"free","/html/body/div/div[2]/a");
        result.get(0).click();

        PO_PrivateView.fillFormAddOffer(driver,"A1","test26","110",false);

        //Comprobamos que se añade la oferta
        String checkText = "A1";
        List<WebElement> offerTitle = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, offerTitle.get(0).getText());

        //Nos salimos de sesion
        PO_HomeView.clickOption(driver, "/users/logout", "free", "/html/body/nav/div/div[2]/ul[2]/li[2]/a");

        //Rellenamos el formulario de login con datos válidos (usuario estandar).
        PO_LoginView.fillLoginForm(driver,"user02@email.com","123456");

        //Vamos a la tienda
        PO_HomeView.clickOption(driver, "/shop", "free", "/html/body/nav/div/div[2]/ul[1]/li[1]/a");

        //Comprobamos que entramos a la vista:“shop”
        checkText = "Shop";
        result = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.get(0).getText());

        //Rellenamos el input a buscar con una oferta existente pero escrita en mayusculas
        WebElement search = driver.findElement(By.name("search"));
        search.click();
        search.clear();
        search.sendKeys("A1");

        // Clickamos en el buscador
        PO_View.checkElementBy(driver,"free","/html/body/div/div[1]/div/form/div/span/button").get(0).click();

        // Comprobamos que esta la oferta
        checkText = "A1";
        SeleniumUtils.textIsPresentOnPage(driver,checkText);
        int price  = parseInt(driver.findElement(By.xpath("/html/body/div/table/tbody/tr/td[4]")).getText());

        //Comprobamos que el wallet esta en 100
        WebElement wallet  = driver.findElement(By.xpath("/html/body/nav/div/div[2]/ul[2]/li[1]/a/span"));
        Assertions.assertEquals("Wallet: 100",wallet.getText());

        // Compramos la oferta, A1
        PO_View.checkElementBy(driver,"free","/html/body/div/table/tbody/tr/td[5]/a").get(0).click();

        //Comprobamos que no se ha reducido el wallet
        wallet  = driver.findElement(By.xpath("/html/body/nav/div/div[2]/ul[2]/li[1]/a/span"));
        Assertions.assertEquals("Wallet: 100", wallet.getText());

        //Comprobamos que se notifican los errores
        checkText = "You don't have enough money";
        result = PO_View.checkElementBy(driver, "class", "alert alert-danger ");
        Assertions.assertEquals(checkText, result.get(0).getText());
    }

    /**
     * [Prueba29] Ir a la opción de ofertas compradas del usuario y mostrar la lista. Comprobar que aparecen
     * las ofertas que deben aparecer.
     */
    @Test
    @Order(29)
    public void PR29() {
        //Vamos al formulario de login
        PO_HomeView.clickOption(driver, "/users/login", "free", "//*[@id=\"myNavbar\"]/ul[2]/li[1]/a");
        //Rellenamos el formulario de login con datos válidos (usuario estandar).
        PO_LoginView.fillLoginForm(driver, "user01@email.com", "123456");

        //Vamos a la tienda
        PO_HomeView.clickOption(driver, "/shop", "free", "/html/body/nav/div/div[2]/ul[1]/li[1]/a");

        //Rellenamos el input a buscar con una oferta existente pero escrita en mayusculas
        WebElement search = driver.findElement(By.name("search"));
        search.click();
        search.clear();
        search.sendKeys("OFFER10USER2");

        // Clickamos en el buscador
        PO_View.checkElementBy(driver,"free","/html/body/div/div[1]/div/form/div/span/button").get(0).click();

        // Compramos la oferta, oferta10User2
        PO_View.checkElementBy(driver,"free","/html/body/div/table/tbody/tr/td[5]/a").get(0).click();

        //Vamos a las compras
        PO_HomeView.clickOption(driver, "/purchases", "free", "/html/body/nav/div/div[2]/ul[1]/li[3]/a");
        //Comprobamos que entramos a la vista:“purchases”
        String checkText = "My Purchases";
        List<WebElement> result = PO_View.checkElementBy(driver, "free","/html/body/div/h2");
        Assertions.assertEquals(checkText, result.get(0).getText());

        // Comprobamos que la primera página tiene una oferta
        List<WebElement> offers = PO_View.checkElementBy(driver, "free", "/html/body/div/div/table/tbody/tr");
        Assertions.assertEquals(1, offers.size());

        //Vamos a la tienda
        PO_HomeView.clickOption(driver, "/shop", "free", "/html/body/nav/div/div[2]/ul[1]/li[1]/a");

        //Rellenamos el input a buscar con una oferta existente pero escrita en mayusculas
        search = driver.findElement(By.name("search"));
        search.click();
        search.clear();
        search.sendKeys("OFFER1USER2");

        // Clickamos en el buscador
        PO_View.checkElementBy(driver,"free","/html/body/div/div[1]/div/form/div/span/button").get(0).click();

        // Compramos la oferta, oferta1User2
        PO_View.checkElementBy(driver,"free","/html/body/div/table/tbody/tr/td[5]/a").get(0).click();
        //Vamos a las compras
        PO_HomeView.clickOption(driver, "/purchases", "free", "/html/body/nav/div/div[2]/ul[1]/li[3]/a");
        //Comprobamos que entramos a la vista:“purchases”
        checkText = "My Purchases";
        result = PO_View.checkElementBy(driver, "free","/html/body/div/h2");
        Assertions.assertEquals(checkText, result.get(0).getText());

        // Comprobamos que la primera página tiene una oferta
        offers = PO_View.checkElementBy(driver, "free", "/html/body/div/div/table/tbody/tr");
        Assertions.assertEquals(2, offers.size());
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
        // TODO mirar si esta bien
        //Accedemos a la url
        driver.get("http://localhost:8080/apiclient/client.html");

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
        List<WebElement> peticion = PO_View.checkElementBy(driver,"free","//*[@id=\"tableLogs\"]/tbody/tr");
        Assertions.assertEquals(1,peticion.size());

        SeleniumUtils.textIsPresentOnPage(driver, "/log/list GET {}");

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
        //11. Hacemos la petición y comprobamos que devuelve 200 y size == 1
        response = request.get(RestGetMessageAssuredURL);
        Assertions.assertEquals(200, response.getStatusCode());
        body = response.body();
        List<String> messages = body.path("messages");
        Assertions.assertEquals(1, messages.size());
    }

    // [Prueba43] Enviar un primer mensaje una oferta propia y comprobar que no se inicia la conversación.
    // En este caso de prueba, el propietario de la oferta tendrá que identificarse (S1), enviar un mensaje
    // para una oferta propia (S3) y comprobar que el mensaje no se almacena (S4)
    @Test
    @Order(43)
    public void PR43() {
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
        final String RestOffersAssuredURL = "http://localhost:8080/api/myOffers";
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
        //9. Hacemos la petición y comprobamos que devuelve 403
        response = request.post(RestMessageAssuredURL);
        Assertions.assertEquals(403, response.getStatusCode());

        //10. Preparamos la peticion de conversaciones para obtener el mensaje
        final String RestGetMessageAssuredURL = "http://localhost:8080/api/offers/" + offerId + "/conversation";
        request = RestAssured.given();
        request.header("token", token);
        //11. Hacemos la petición y comprobamos que devuelve 200 y size == 1
        response = request.get(RestGetMessageAssuredURL);
        Assertions.assertEquals(200, response.getStatusCode());
        body = response.body();
        List<String> messages = body.path("messages");
        //12. Comprobamos que no existe el mensaje
        Assertions.assertEquals(0, messages.size());
    }

    // [Prueba44] Obtener los mensajes de una conversación. Esta prueba consistirá en comprobar que el
    // servicio retorna el número correcto de mensajes para una conversación. El ID de la conversación
    // deberá conocerse a priori. Por lo tanto, se tendrá primero que invocar al servicio de identificación
    // (S1), y solicitar el listado de mensajes de una conversación de id conocido a continuación (S4),
    // comprobando que se retornan los mensajes adecuados.
    @Test
    @Order(44)
    public void PR44() {
        final String RestLoginAssuredURL = "http://localhost:8080/api/users/login";
        //2. Preparamos el parámetro en formato JSON
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        String user = "user01@email.com";
        requestParams.put("email", user);
        requestParams.put("password", "123456");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        //3. Hacemos la petición y comprobamos que devuelve 200
        Response response = request.post(RestLoginAssuredURL);
        Assertions.assertEquals(200, response.getStatusCode());
        //4. Obtenemos el token de autorización
        ResponseBody body = response.body();
        String token = body.path("token");

        // Insertamos los mensajes y obtenemos el id de la oferta de la conversacion
        String offerID = mongoDB.insertMessages(user);

        //5. Preparamos la peticion de conversaciones para obtener el mensaje
        final String RestGetMessageAssuredURL = "http://localhost:8080/api/offers/" + offerID + "/conversation";
        request = RestAssured.given();
        request.header("token", token);
        //6. Hacemos la petición y comprobamos que devuelve 200 y size == 1
        response = request.get(RestGetMessageAssuredURL);
        Assertions.assertEquals(200, response.getStatusCode());
        body = response.body();
        List<String> messages = body.path("messages");
        //7. Comprobamos que no existe el mensaje
        Assertions.assertEquals(2, messages.size());
    }

    // [Prueba45] Obtener la lista de conversaciones de un usuario. Esta prueba consistirá en comprobar que
    // el servicio retorna el número correcto de conversaciones para dicho usuario. Por lo tanto, se tendrá
    // primero que invocar al servicio de identificación (S1), y solicitar el listado de conversaciones a
    // continuación (S5) comprobando que se retornan las conversaciones adecuadas
    @Test
    @Order(45)
    public void PR45() {
        final String RestLoginAssuredURL = "http://localhost:8080/api/users/login";
        //2. Preparamos el parámetro en formato JSON
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        String user = "user01@email.com";
        requestParams.put("email", user);
        requestParams.put("password", "123456");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        //3. Hacemos la petición y comprobamos que devuelve 200
        Response response = request.post(RestLoginAssuredURL);
        Assertions.assertEquals(200, response.getStatusCode());
        //4. Obtenemos el token de autorización
        ResponseBody body = response.body();
        String token = body.path("token");

        // Insertamos los mensajes y obtenemos el id de la oferta de la conversacion
        String offerID = mongoDB.insertMessages(user);

        //5. Preparamos la peticion de conversaciones para obtener el mensaje
        final String RestGetMessageAssuredURL = "http://localhost:8080/api/offers/conversations";
        request = RestAssured.given();
        request.header("token", token);
        //6. Hacemos la petición y comprobamos que devuelve 200 y size == 1
        response = request.get(RestGetMessageAssuredURL);
        Assertions.assertEquals(200, response.getStatusCode());
        body = response.body();
        List<String> conversations = body.path("conversations");
        //7. Comprobamos que exite la conversacion
        Assertions.assertEquals(1, conversations.size());
    }

    // [Prueba46] Eliminar una conversación de ID conocido. Esta prueba consistirá en comprobar que se
    // elimina correctamente una conversación concreta. Por lo tanto, se tendrá primero que invocar al
    // servicio de identificación (S1), eliminar la conversación ID (S6) y solicitar el listado de
    // conversaciones a continuación (S5), comprobando que se retornan las conversaciones adecuadas.
    @Test
    @Order(46)
    public void PR46() {
        final String RestLoginAssuredURL = "http://localhost:8080/api/users/login";
        //2. Preparamos el parámetro en formato JSON
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        String user = "user01@email.com";
        requestParams.put("email", user);
        requestParams.put("password", "123456");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        //3. Hacemos la petición y comprobamos que devuelve 200
        Response response = request.post(RestLoginAssuredURL);
        Assertions.assertEquals(200, response.getStatusCode());
        //4. Obtenemos el token de autorización
        ResponseBody body = response.body();
        String token = body.path("token");

        // Insertamos los mensajes y obtenemos el id de la oferta de la conversacion
        String offerID = mongoDB.insertMessages(user);
        String conversationId = mongoDB.getFirstConversation();

        //5. Preparamos la peticion de conversaciones para obtener el mensaje
        final String RestConversationsAssuredURL = "http://localhost:8080/api/offers/conversation/delete/" + conversationId;
        request = RestAssured.given();
        request.header("token", token);
        //6. Hacemos la petición y comprobamos que devuelve 200 y size == 1
        response = request.delete(RestConversationsAssuredURL);
        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals("{\"message\":\"Conversation removed\"}", response.body().asString());

        //7. Preparamos la peticion de conversaciones para obtener el mensaje
        final String RestGetMessageAssuredURL = "http://localhost:8080/api/offers/conversations";
        request = RestAssured.given();
        request.header("token", token);
        //8. Hacemos la petición y comprobamos que devuelve 200 y size == 1
        response = request.get(RestGetMessageAssuredURL);
        Assertions.assertEquals(200, response.getStatusCode());
        body = response.body();
        List<String> conversations = body.path("conversations");
        //9. Comprobamos que no existe el mensaje
        Assertions.assertEquals(0, conversations.size());
    }

    // [Prueba47] Marcar como leído un mensaje de ID conocido. Esta prueba consistirá en comprobar que
    // el mensaje marcado de ID conocido queda marcado correctamente a true como leído. Por lo
    // tanto, se tendrá primero que invocar al servicio de identificación (S1), solicitar el servicio de
    // marcado (S7), comprobando que el mensaje marcado ha quedado marcado a true como leído (S4).
    @Test
    @Order(47)
    public void PR47() {
        final String RestLoginAssuredURL = "http://localhost:8080/api/users/login";
        //2. Preparamos el parámetro en formato JSON
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        String user = "user01@email.com";
        requestParams.put("email", user);
        requestParams.put("password", "123456");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        //3. Hacemos la petición y comprobamos que devuelve 200
        Response response = request.post(RestLoginAssuredURL);
        Assertions.assertEquals(200, response.getStatusCode());
        //4. Obtenemos el token de autorización
        ResponseBody body = response.body();
        String token = body.path("token");

        // Insertamos los mensajes y obtenemos el id de la oferta de la conversacion
        mongoDB.insertMessages(user);
        String conversationId = mongoDB.getFirstConversation();

        //5. Preparamos la peticion de conversaciones para obtener el mensaje
        final String RestReadAssuredURL = "http://localhost:8080/api/offers/" + conversationId + "/markRead";
        request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("token", token);
        //6. Hacemos la petición y comprobamos que devuelve 200 y size == 1
        response = request.post(RestReadAssuredURL);
        Assertions.assertEquals(200, response.getStatusCode());

        body = response.body();
        String offerID = body.path("offerId");

        //7. Preparamos la peticion de conversaciones para obtener el mensaje
        final String RestGetMessageAssuredURL = "http://localhost:8080/api/offers/" + offerID + "/conversation";
        request = RestAssured.given();
        request.header("token", token);
        //6. Hacemos la petición y comprobamos que devuelve 200 y size == 1
        response = request.get(RestGetMessageAssuredURL);
        Assertions.assertEquals(200, response.getStatusCode());
        body = response.body();
        HashMap messages = body.path("messages[0]");
        //7. Comprobamos que no existe el mensaje
        Assertions.assertEquals(true, messages.get("read"));
    }

    // ----------------------------------------------------------------------------------------------------
    // TESTING CLIENT
    // ----------------------------------------------------------------------------------------------------

    /**
     * [Prueba48] Inicio de sesión con datos válidos
     */
    @Test
    @Order(48)
    public void PR48() {
      //  driverClient.navigate().to(URLCLIENT);
        driver.get(URLCLIENT);
//        mongoDB.resetMongo();

       //Rellenamos el formulario de login de un usuario
        WebElement email = driver.findElement(By.name("email"));
        email.click();
        email.clear();
        email.sendKeys("user01@email.com");
        WebElement password = driver.findElement(By.name("password"));
        password.click();
        password.clear();
        password.sendKeys("123456");

        //Le damos click al boton de login
        PO_View.checkElementBy(driver,"free","/html/body/div/div/div[3]/div/button").get(0).click();

        //Comprobamos que entramos a la vista: “shop”
        String checkText = "Shop";
        List<WebElement> result = PO_View.checkElementBy(driver, "text", checkText);
        Assertions.assertEquals(checkText, result.get(0).getText());

        driverClient.manage().deleteAllCookies();
    }

    /**
     * [Prueba49] Inicio de sesión con datos inválidos (email existente, pero contraseña incorrecta).
     */
    @Test
    @Order(49)
    public void PR49() {
        driverClient.navigate().to(URLCLIENT);
        mongoDB.resetMongo();

        //Rellenamos el formulario de login de un usuario pero con contraseña falsa
        WebElement email = driverClient.findElement(By.name("email"));
        email.click();
        email.clear();
        email.sendKeys("user01@email.com");
        WebElement password = driverClient.findElement(By.name("password"));
        password.click();
        password.clear();
        password.sendKeys("1123127348124");

        //Le damos click al boton de login
        PO_View.checkElementBy(driverClient,"free","/html/body/div/div/div[3]/div/button").get(0).click();

        //Comprobamos que se notifican los errores
        String checkText = "Usuario no encontrado";
        List<WebElement> result = PO_View.checkElementBy(driverClient, "class", "alert alert-danger");
        Assertions.assertEquals(checkText, result.get(0).getText());

        driverClient.manage().deleteAllCookies();
    }

    /**
     * [Prueba50] Inicio de sesión con datos inválidos (campo email o contraseña vacíos).
     */
    @Test
    @Order(50)
    public void PR50() {
        driverClient.navigate().to(URLCLIENT);
        mongoDB.resetMongo();

        //Rellenamos el formulario de login de un usuario pero con contraseña falsa
        WebElement email = driverClient.findElement(By.name("email"));
        email.click();
        email.clear();
        email.sendKeys("");
        WebElement password = driverClient.findElement(By.name("password"));
        password.click();
        password.clear();
        password.sendKeys("");

        //Le damos click al boton de login
        PO_View.checkElementBy(driverClient,"free","/html/body/div/div/div[3]/div/button").get(0).click();

        //Comprobamos que se notifican los errores
        String checkText = "Usuario no encontrado";
        List<WebElement> result = PO_View.checkElementBy(driverClient, "class", "alert alert-danger");
        Assertions.assertEquals(checkText, result.get(0).getText());

        driverClient.manage().deleteAllCookies();
    }

    /**
     * [Prueba51] Mostrar el listado de ofertas disponibles y comprobar que se muestran todas las que existen,
     * menos las del usuario identificado.
     */
    @Test
    @Order(51)
    public void PR51() {
        driverClient.navigate().to(URLCLIENT);
        mongoDB.resetMongo();

        //Rellenamos el formulario de login de un usuario pero con contraseña falsa
        WebElement email = driverClient.findElement(By.name("email"));
        email.click();
        email.clear();
        email.sendKeys("user01@email.com");
        WebElement password = driverClient.findElement(By.name("password"));
        password.click();
        password.clear();
        password.sendKeys("123456");

        //Le damos click al boton de login
        PO_View.checkElementBy(driverClient,"free","/html/body/div/div/div[3]/div/button").get(0).click();

        // Comprobamos que la primera página este llena (5 ofertas)
        List<WebElement> offers = PO_View.checkElementBy(driverClient, "free", "//*[@id=\"offersTableBody\"]/tr");
        Assertions.assertEquals(140, offers.size());

        driverClient.manage().deleteAllCookies();
    }

    /**
     * [Prueba52] Sobre listado de ofertas disponibles (a elección de desarrollador), enviar un mensaje a una
     * oferta concreta. Se abriría dicha conversación por primera vez. Comprobar que el mensaje aparece
     * en el listado de mensajes.
     */
    @Test
    @Order(52)
    public void PR52() {
        driverClient.navigate().to(URLCLIENT);
        mongoDB.resetMongo();

        //Rellenamos el formulario de login de un usuario pero con contraseña falsa
        WebElement email = driverClient.findElement(By.name("email"));
        email.click();
        email.clear();
        email.sendKeys("user01@email.com");
        WebElement password = driverClient.findElement(By.name("password"));
        password.click();
        password.clear();
        password.sendKeys("123456");

        //Le damos click al boton de login
        PO_View.checkElementBy(driverClient,"free","/html/body/div/div/div[3]/div/button").get(0).click();

        //Le damos click al boton de coversacion de la primera oferta
        PO_View.checkElementBy(driverClient,"free","/html/body/div/div/table/tbody/tr[1]/td[6]/a").get(0).click();

        //Rellenamos un mensaje
        WebElement mensaje = driverClient.findElement(By.name("message"));
        mensaje.click();
        mensaje.clear();
        mensaje.sendKeys("Hola");

        //Le damos click al boton de send
        PO_View.checkElementBy(driverClient,"free","//*[@id=\"button-send-message\"]").get(0).click();

        SeleniumUtils.waitSeconds(driverClient, 5);
        //Vemos que ya hay un mensaje
        List<WebElement> mensajes = PO_View.checkElementBy(driverClient, "free","//*[@id=\"messagesTableBody\"]/tr");
        Assertions.assertEquals(1, mensajes.size());

        //Rellenamos un mensaje
        mensaje = driverClient.findElement(By.name("message"));
        mensaje.click();
        mensaje.clear();
        mensaje.sendKeys("Hola 1");


        //Le damos click al boton de send
        PO_View.checkElementBy(driverClient,"free","//*[@id=\"button-send-message\"]").get(0).click();
        SeleniumUtils.waitSeconds(driverClient, 5);

        //Vemos que ya hay dos mensaje
        mensajes = PO_View.checkElementBy(driverClient, "free","//*[@id=\"messagesTableBody\"]/tr");
        Assertions.assertEquals(2, mensajes.size());
    }
}