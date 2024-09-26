package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ResponseCodebaseDto {
    // 每週第一天
    private String date;
    // 當週程式碼增加的行數
    private int numberOfRowsAdded;
    // 當週程式碼刪減的行數
    private int numberOfRowsDeleted;
    // 截至目前，程式碼行數的總量，numberOfRows += numberOfRowsAdded + (-numberOfRowsDeleted)
    private int numberOfRows;
}
