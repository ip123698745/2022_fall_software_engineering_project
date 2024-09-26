package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ResponseUserInfoDto {
    public String id;
    public String Name;
    public String avatarUrl;
}
