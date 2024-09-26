package com.dao;

import com.bean.Jira;
import com.bean.Repo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JiraRepository extends JpaRepository<Jira, Long> {

}