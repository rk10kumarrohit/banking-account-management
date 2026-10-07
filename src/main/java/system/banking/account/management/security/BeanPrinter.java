package system.banking.account.management.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


public class BeanPrinter implements CommandLineRunner {

    private final ApplicationContext applicationContext;

    public BeanPrinter(ApplicationContext applicationContext) {

        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) {

        String[] beanNames = applicationContext.getBeanDefinitionNames();

        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);

            System.out.println(
                    "Bean: " + beanName +
                            " | Class: " + bean.getClass().getName()
            );
        }
    }
}
