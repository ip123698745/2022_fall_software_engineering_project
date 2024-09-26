package com.dao;

import com.bean.GitHub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GitHubRepository extends JpaRepository<GitHub, Long> {
}