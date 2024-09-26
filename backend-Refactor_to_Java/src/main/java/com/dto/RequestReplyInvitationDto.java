package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class RequestReplyInvitationDto {
    public long InvitationId;
    public boolean IsAgreed;
}
