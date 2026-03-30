package web.model.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class LoginRequest {
    @Email(message = "Email should be valid")
    private final String email;
    private final String password;

}
