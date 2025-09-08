package com.example.jobs;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobDAO {

    public void add(Job j) throws Exception {
        String sql = "INSERT INTO jobs (title, company_name, location, job_type, salary_min, salary_max, description, requirements, responsibilities, application_deadline) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, j.title);
            ps.setString(2, j.companyName);
            ps.setString(3, j.location);
            ps.setString(4, j.jobType);

            if (j.salaryMin == null) ps.setNull(5, Types.INTEGER);
            else ps.setInt(5, j.salaryMin);

            if (j.salaryMax == null) ps.setNull(6, Types.INTEGER);
            else ps.setInt(6, j.salaryMax);

            ps.setString(7, j.description);
            ps.setString(8, j.requirements);
            ps.setString(9, j.responsibilities);

            if (j.applicationDeadline == null || j.applicationDeadline.trim().isEmpty()) {
                ps.setNull(10, Types.DATE);
            } else {
                ps.setDate(10, Date.valueOf(j.applicationDeadline));
            }

            int rows = ps.executeUpdate();
            System.out.println("Rows inserted: " + rows);

        }
    }

    public List<Job> listFiltered(String title, String location, String jobType,
                                  Integer salaryMin, Integer salaryMax) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT * FROM jobs WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (title != null && !title.trim().isEmpty()) {
            sql.append(" AND title LIKE ?");
            params.add("%" + title.trim() + "%");
        }
        if (location != null && !location.trim().isEmpty()) {
            sql.append(" AND location LIKE ?");
            params.add("%" + location.trim() + "%");
        }
        if (jobType != null && !jobType.trim().isEmpty()) {
            sql.append(" AND job_type = ?");
            params.add(jobType.trim());
        }
        if (salaryMin != null) {
            sql.append(" AND (salary_max IS NULL OR salary_max >= ?)");
            params.add(salaryMin);
        }
        if (salaryMax != null) {
            sql.append(" AND (salary_min IS NULL OR salary_min <= ?)");
            params.add(salaryMax);
        }

        sql.append(" ORDER BY created_at DESC");

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<Job> out = new ArrayList<>();
                while (rs.next()) {
                    Job j = new Job();
                    j.id = rs.getInt("id");
                    j.title = rs.getString("title");
                    j.companyName = rs.getString("company_name");
                    j.location = rs.getString("location");
                    j.jobType = rs.getString("job_type");

                    int smin = rs.getInt("salary_min");
                    j.salaryMin = rs.wasNull() ? null : smin;

                    int smax = rs.getInt("salary_max");
                    j.salaryMax = rs.wasNull() ? null : smax;

                    j.description = rs.getString("description");
                    j.requirements = rs.getString("requirements");
                    j.responsibilities = rs.getString("responsibilities");

                    Date d = rs.getDate("application_deadline");
                    j.applicationDeadline = (d == null) ? null : d.toString();

                    out.add(j);
                }
                return out;
            }
        }
    }
}
