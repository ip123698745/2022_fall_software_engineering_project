package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class RequestJiraLoginDto {
    public String DomainURL;
    public String APIToken;
    public String Account;
    public long BoardId;
    public long ProjectId;
}