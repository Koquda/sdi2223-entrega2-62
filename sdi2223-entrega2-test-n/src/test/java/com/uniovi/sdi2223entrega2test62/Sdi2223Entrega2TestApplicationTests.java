package com.uniovi.sdi2223entrega2test62;

import com.uniovi.sdi2223entrega2test62.mongo.MongoDB;
import com.uniovi.sdi2223entrega2test62.pageobjects.PO_HomeView;
import com.uniovi.sdi2223entrega2test62.pageobjects.PO_LoginView;
import com.uniovi.sdi2223entrega2test62.pageobjects.PO_SignUpView;
import com.uniovi.sdi2223entrega2test62.pageobjects.PO_View;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Sdi2223Entrega2TestApplicationTests {
    static String PathFirefox = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
    //static String Geckodriver = "C:\\\\Users\\\\UO282874\\\\OneDrive - Universidad de Oviedo\\\\3º\\\\SDI\\\\Lab\\\\Sesion4\\\\PL-SDI-Sesión5-material\\\\PL-SDI-Sesio╠ün5-material\\\\geckodriver-v0.30.0-win64.exe";
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


    /* Ejemplos de pruebas de llamada a una API-REST */
    /* ---- Probamos a obtener lista de canciones sin token ---- */
    @Test
    @Order(11)
    public void PR11() {
        final String RestAssuredURL = "http://localhost:8081/api/v1.0/songs";
        Response response = RestAssured.get(RestAssuredURL);
        Assertions.assertEquals(403, response.getStatusCode());
    }

    @Test
    @Order(38)
    public void PR38() {
        final String RestAssuredURL = "http://localhost:8081/api/v1.0/users/login";
        //2. Preparamos el parámetro en formato JSON
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", "prueba1@prueba1.com");
        requestParams.put("password", "prueba1");
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());
        //3. Hacemos la petición
        Response response = request.post(RestAssuredURL);
        //4. Comprobamos que el servicio ha tenido exito
        Assertions.assertEquals(200, response.getStatusCode());
    }
}
