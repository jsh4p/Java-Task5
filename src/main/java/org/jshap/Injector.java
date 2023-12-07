package org.jshap;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * Класс, который бы осуществлял внедрение зависимостей в любой объект, содержащий поля,
 * помеченные нашей аннотацией
 * @author jshap
 */
public class Injector {
    /**
     * Метод, осуществляющий внедрение зависимостей
     * @param obj экземпляр класса
     * @return ссылка на obj
     */
    public static Object inject(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        Properties properties = new Properties();

        try (InputStream in = Injector.class.getClassLoader().getResourceAsStream("injector_example.properties")) {
            if (in == null) {
                throw new NullPointerException();
            }

            properties.load(new InputStreamReader(in));
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Failed to read properties file ", e);
        }

        for (Field field : fields) {
            if (field.isAnnotationPresent(AutoInjectable.class)) {
                String propertyKey = field.getType().getName();
                String implementationClassName = properties.getProperty(propertyKey);

                if (implementationClassName == null) {
                    throw new RuntimeException("There is no implementation for " + propertyKey);
                }

                try {
                    Class<?> implementationClass = Class.forName(implementationClassName);
                    Object implementationClassInstance = implementationClass.getDeclaredConstructor().newInstance();
                    field.setAccessible(true);
                    field.set(obj, implementationClassInstance);
                    field.setAccessible(false);
                } catch (Exception e) {
                    throw new RuntimeException("Injection failed with " + field.getName(), e);
                }
            }
        }

        return obj;
    }
}
