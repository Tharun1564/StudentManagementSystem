package com.studentmanagement.dao;

import com.studentmanagement.model.Student;
import com.studentmanagement.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;

public class StudentDAO {

    public static class DuplicateEmailException extends RuntimeException {
        public DuplicateEmailException(String message) {
            super(message);
        }
    }

    public void saveStudent(Student student) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(student);
            transaction.commit();
        } catch (ConstraintViolationException e) {
            if (transaction != null && transaction.isActive()) transaction.rollback();
            throw new DuplicateEmailException("A student with this email already exists");
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) transaction.rollback();
            e.printStackTrace();
            throw e;
        }
    }

    public Student getStudentById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Student.class, id);
        }
    }

    public List<Student> getAllStudents() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Student", Student.class).list();
        }
    }

    public boolean updateStudent(Student student) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Student existing = session.get(Student.class, student.getId());
            if (existing == null) {
                transaction.rollback();
                return false;
            }

            existing.setName(student.getName());
            existing.setEmail(student.getEmail());
            existing.setCourse(student.getCourse());
            existing.setAge(student.getAge());

            transaction.commit();
            return true;
        } catch (ConstraintViolationException e) {
            if (transaction != null && transaction.isActive()) transaction.rollback();
            throw new DuplicateEmailException("A student with this email already exists");
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) transaction.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStudent(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Student student = session.get(Student.class, id);
            if (student == null) {
                transaction.rollback();
                return false;
            }
            session.remove(student);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) transaction.rollback();
            e.printStackTrace();
            return false;
        }
    }
}
