package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestProjectDto {
    public long projectId;
    // 這邊命名須與前端request body的變數命名相同，不然無法對應到dto
    public String ProjectName;
}
