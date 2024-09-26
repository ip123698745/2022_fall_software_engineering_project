package com.dao;


import com.bean.Project;
import com.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository  extends JpaRepository<Project, Long> {

    //todo 寫錯了
    @Query("SELECT u FROM User u WHERE u.account = :account")
    User getOwnerByAccount(@Param("account") String account);

    @Query("SELECT p FROM Project p")
    List<Project> findAllProject();

}
