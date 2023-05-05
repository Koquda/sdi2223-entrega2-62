package com.uniovi.sdi2223entrega2test62.pageobjects;

import com.uniovi.sdi2223entrega2test62.util.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class PO_PrivateView extends PO_NavView {
    static public void fillFormAddOffer(WebDriver driver, String titlep, String detailsp,String pricep
            ,boolean highlight) {


        WebElement title = driver.findElement(By.name("title"));
        title.clear();
        title.sendKeys(titlep);
        WebElement details = driver.findElement(By.name("details"));
        details.click();
        details.clear();
        details.sendKeys(detailsp);

        WebElement price = driver.findElement(By.name("price"));
        price.click();
        price.clear();
        price.sendKeys(pricep);

        if(highlight){
            WebElement highlightElement = driver.findElement(By.name("highlight"));
            highlightElement.click();
        }


        By boton = By.className("btn");
        driver.findElement(boton).click();
    }
}
