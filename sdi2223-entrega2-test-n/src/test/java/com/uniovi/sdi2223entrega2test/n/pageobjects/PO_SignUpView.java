package com.uniovi.sdi2223entrega2test.n.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_SignUpView extends PO_NavView {
    static public void fillForm(WebDriver driver, String emailp,String namep,String surnamep,
                                String birthdatep,String passwordp, String passwordconfp) {
        WebElement dni = driver.findElement(By.name("email"));
        dni.click();
        dni.clear();
        dni.sendKeys(emailp);
        WebElement name = driver.findElement(By.name("name"));
        name.click();
        name.clear();
        name.sendKeys(namep);
        WebElement surnames = driver.findElement(By.name("surnames"));
        surnames.click();
        surnames.clear();
        surnames.sendKeys(surnamep);
        WebElement birthdate = driver.findElement(By.name("birthdate"));
        birthdate.click();
        birthdate.clear();
        birthdate.sendKeys(birthdatep);
        WebElement password = driver.findElement(By.name("password"));
        password.click();
        password.clear();
        password.sendKeys(passwordp);
        WebElement confirmPassword = driver.findElement(By.name("confirmPassword"));
        confirmPassword.click();
        confirmPassword.clear();
        confirmPassword.sendKeys(passwordconfp);
        //Pulsar el boton de Alta.
        By boton = By.className("btn");
        driver.findElement(boton).click();
    }

}
