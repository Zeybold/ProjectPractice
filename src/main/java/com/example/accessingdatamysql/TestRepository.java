package com.example.accessingdatamysql;

import org.springframework.data.repository.CrudRepository;

public interface TestRepository  extends CrudRepository<Test, Integer> {

}