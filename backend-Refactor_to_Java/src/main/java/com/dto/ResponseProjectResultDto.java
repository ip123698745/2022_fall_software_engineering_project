package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ResponseProjectResultDto {
    private long id;
    private String name;
    private String ownerId;
    private String ownerName;
    private int members;
}
