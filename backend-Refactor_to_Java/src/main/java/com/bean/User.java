package com.bean;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "Users")
public class User implements Serializable {
    @Id
    @Column(name = "account")
    private String account;

    private String name;
    private String avatarUrl;
    private String authority;
    private String password;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    @NonNull
    private List<Project> ownedProjects = new ArrayList<>();

    //    建立 project & user 之間的關聯
    @ManyToMany(mappedBy = "users", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    @NonNull
    private List<Project> projects = new ArrayList<>();

    @Override
    public String toString() {
        return "User{" +
                "account='" + account + '\'' +
                ", name='" + name + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", authority='" + authority + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
