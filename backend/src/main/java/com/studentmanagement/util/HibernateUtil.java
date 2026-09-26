package com.studentmanagement.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import io.github.cdimascio.dotenv.Dotenv;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {

            // Load .env file
            Dotenv dotenv = Dotenv.load();

            // Load Hibernate configuration
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");

            // Get database details from .env
            configuration.setProperty(
                    "hibernate.connection.url",
                    dotenv.get("DB_URL")
            );

            configuration.setProperty(
                    "hibernate.connection.username",
                    dotenv.get("DB_USERNAME")
            );

            configuration.setProperty(
                    "hibernate.connection.password",
                    dotenv.get("DB_PASSWORD")
            );

            // Create SessionFactory
            return configuration.buildSessionFactory();

        } catch (Throwable ex) {
            System.err.println("SessionFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}