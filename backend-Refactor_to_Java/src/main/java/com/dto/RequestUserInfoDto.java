package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class RequestUserInfoDto {
    //Should be public type owing to map to @RequestBody
    public String id;
    public String Name;
    public String avatarUrl;
}
