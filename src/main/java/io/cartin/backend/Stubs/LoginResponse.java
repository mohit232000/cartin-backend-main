package io.cartin.backend.Stubs;

import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Getter
@Setter
public class LoginResponse {
    private Long userId;
    private String role;
}
