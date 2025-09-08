package com.example.jobs;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.Connection;
import java.sql.Statement;

@WebListener
public class DBInitializer implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement()) {

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS jobs (
                    id IDENTITY PRIMARY KEY,
                    title VARCHAR(255),
                    company_name VARCHAR(255),
                    location VARCHAR(255),
                    job_type VARCHAR(100),
                    salary_min INT,
                    salary_max INT,
                    description CLOB,
                    requirements CLOB,
                    responsibilities CLOB,
                    application_deadline DATE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            System.out.println("✅ H2 jobs table ready");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
