package com.studentmanagement.controller;

import com.studentmanagement.dao.StudentDAO;
import com.studentmanagement.model.Student;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/students/*")
public class StudentServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            out.print(gson.toJson(studentDAO.getAllStudents()));
        } else {
            int id = Integer.parseInt(pathInfo.substring(1));
            Student student = studentDAO.getStudentById(id);
            if (student != null) {
                out.print(gson.toJson(student));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Student not found\"}");
            }
        }
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        Student student = gson.fromJson(req.getReader(), Student.class);

        try {
            studentDAO.saveStudent(student);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            out.print(gson.toJson(student));
        } catch (StudentDAO.DuplicateEmailException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Failed to save student\"}");
        }
        out.flush();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Student id required in URL\"}");
            out.flush();
            return;
        }

        int id = Integer.parseInt(pathInfo.substring(1));
        Student student = gson.fromJson(req.getReader(), Student.class);
        student.setId(id);

        try {
            boolean updated = studentDAO.updateStudent(student);
            if (updated) {
                out.print(gson.toJson(student));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Student not found\"}");
            }
        } catch (StudentDAO.DuplicateEmailException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
        out.flush();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Student id required in URL\"}");
        } else {
            int id = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = studentDAO.deleteStudent(id);
            if (deleted) {
                out.print("{\"message\":\"Student deleted\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Student not found\"}");
            }
        }
        out.flush();
    }
}
