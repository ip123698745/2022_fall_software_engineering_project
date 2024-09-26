package com.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Accessors(chain = true)
@MappedSuperclass
public abstract class Repo implements Serializable {
    // TODO 目前為套用至新的 schema
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REPO_ID_SEQUENCE")
    @Basic
    protected long id;

    protected String url;

    private String name;
    private String owner;   //第三方API的owner
    private Integer repoId;

    private String accountColonPw;
    private String projectKey;
    private String type; // Type 在 Jira 中會使用到(用於前端辨識 Jira 頁面) 不要刪了！
    private String apiToken;
}