package com.fiap.begin_projetct.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioRequest {
    
    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail deve ser válido")
    @Size(max = 255, message = "E-mail deve ter no máximo 255 caracteres")
    private String email;
    
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, max = 32, message = "Senha deve ter entre 8 e 32 caracteres")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,32}$",
             message = "Senha deve conter: 1 letra maiúscula, 1 letra minúscula, 1 número e 1 caractere especial")
    private String senha;

    public com.fiap.begin_projetct.model.Usuario toEntity() {
        com.fiap.begin_projetct.model.Usuario u = new com.fiap.begin_projetct.model.Usuario();
        u.setEmail(this.email);
        u.setSenha(this.senha);
        return u;
    }
}
