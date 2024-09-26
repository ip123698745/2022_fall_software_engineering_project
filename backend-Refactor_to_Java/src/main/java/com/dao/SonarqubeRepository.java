package com.dao;

import com.bean.Sonarqube;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SonarqubeRepository extends JpaRepository<Sonarqube, Long> {

}