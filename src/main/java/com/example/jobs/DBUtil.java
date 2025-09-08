public static Connection getConnection() throws Exception {
    Class.forName("org.h2.Driver");
    Connection conn = DriverManager.getConnection(
        "jdbc:h2:mem:jobsdb;DB_CLOSE_DELAY=-1", "sa", ""
    );

    // ✅ Ensure table exists
    try (Statement st = conn.createStatement()) {
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
    }

    return conn;
}
