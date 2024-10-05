package com.sportZplay.sportZplay.DTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequestDTO {

    /* The UserId*/
    @NonNull
    private String userId;

    /* The password*/
    @NonNull
    private String password;
}
