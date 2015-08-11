package com.homework;

import org.springframework.boot.SpringApplication;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("com/homework/beans.xml");
		
		// The following not needed because autowire parameter used on controller beans: 
		//LoanController.loanDao = (LoanDao)applicationContext.getBean("loanDao");
		//PersonController.personDao = (PersonDao)applicationContext.getBean("personDao");
		
        SpringApplication.run(new Object[]{LoanController.class, PersonController.class}, args);
        
        applicationContext.close();
        
    }
}
