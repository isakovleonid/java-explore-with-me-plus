package ru.practicum.users.dto.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {
    @NotEmpty
    @Length(min = 6, max = 254)
    @Email
    String email;
    @NotEmpty
    @Length(min = 2, max = 250)
    String name;
}
