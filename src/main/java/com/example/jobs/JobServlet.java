package com.example.jobs;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/jobs")   // ✅ This makes your servlet accessible at /api/jobs
public class JobServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final JobDAO dao = new JobDAO();
    private final Gson gson = new Gson();

    private void cors(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        cors(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        cors(resp);
        System.out.println("➡️ Received POST /api/jobs");
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        try (BufferedReader br = req.getReader()) {
            Job j = gson.fromJson(br, Job.class);

            if (isBlank(j.title) || isBlank(j.companyName) || isBlank(j.location)
                    || isBlank(j.jobType) || isBlank(j.description)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(error("Missing required fields"));
                return;
            }

            dao.add(j);

            JsonObject ok = new JsonObject();
            ok.addProperty("message", "Job created");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(ok.toString());
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(error(e.getMessage()));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        cors(resp);
        resp.setContentType("application/json;charset=UTF-8");

        String title = req.getParameter("title");
        String location = req.getParameter("location");
        String jobType = req.getParameter("jobType");
        Integer smin = parseIntOrNull(req.getParameter("salaryMin"));
        Integer smax = parseIntOrNull(req.getParameter("salaryMax"));

        try {
            List<Job> jobs = dao.listFiltered(title, location, jobType, smin, smax);
            resp.getWriter().write(gson.toJson(jobs));
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(error(e.getMessage()));
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private Integer parseIntOrNull(String v) {
        if (v == null) return null;
        v = v.trim();
        if (v.isEmpty()) return null;
        try {
            return Integer.valueOf(v);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String error(String msg) {
        JsonObject o = new JsonObject();
        o.addProperty("error", (msg == null || msg.trim().isEmpty()) ? "Server error" : msg);
        return o.toString();
    }
}
