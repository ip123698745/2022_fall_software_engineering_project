package com.dao;

import com.bean.GitLab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GitLabRepository extends JpaRepository<GitLab, Long> {
}