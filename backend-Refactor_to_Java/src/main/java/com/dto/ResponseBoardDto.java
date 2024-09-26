package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ResponseBoardDto {
    private boolean success;
    private List<JiraBoardDetailDto> data;
}