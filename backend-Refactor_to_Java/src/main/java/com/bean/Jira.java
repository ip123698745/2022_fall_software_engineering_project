package com.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "Jiras")
public class Jira extends Repo {

    @Column(name = "api_token")
    private String apiToken;

    @Column(name = "board_id")
    private long boardId;

    @Column(name = "board_name")
    private String name;

    private String account;
}
