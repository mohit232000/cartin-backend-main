package io.cartin.backend.Stubs;


import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Getter
@Setter
public class SignupResponse {
    private Long userId;
}
