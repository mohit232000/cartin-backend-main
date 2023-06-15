package io.cartin.backend.Stubs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class PasswordChangeRequest {
    private Long userId;
    private String oldPassword;
    private String newPassword;
}