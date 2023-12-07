package com.example.accessingdatamysql;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    private String email;

    private String password;

    private List<Integer> listTests;

    public void createListTests() {
        listTests = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public AbstractMap.SimpleEntry<String, Integer> getPerson() {
        return new AbstractMap.SimpleEntry<>(name, id);
    }

    public String getName() {
        return name;
    }

    public void rename(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean successInput(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }

    public boolean getPassword(String attempt) {
        return password.equals(attempt);
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    public void addNewTest(int testId) {
        listTests.add(testId);
    }

    public void removeTest(int testId) {
        listTests.remove(testId);
    }

    public int getTestById(int testId) {
        for (var test : listTests) {
            if (test == testId) {
                return test;
            }
        }
        throw new IllegalArgumentException("The test does not exist");
    }

    public List<Integer> getListTests() {
        return listTests;
    }
}