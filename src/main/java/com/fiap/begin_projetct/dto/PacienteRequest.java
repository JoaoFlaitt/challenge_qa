package com.fiap.begin_projetct.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PacienteRequest {
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    private String nome;
    
    @NotBlank(message = "CPF é obrigatório")
    @Size(max = 14, message = "CPF deve ter no máximo 14 caracteres")
    private String cpf;
    
    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail deve ser válido")
    @Size(max = 255, message = "E-mail deve ter no máximo 255 caracteres")
    private String email;
    
    @NotBlank(message = "Telefone é obrigatório")
    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    private String telefone;
    
    private LocalDate dataNascimento;
    
    @Size(max = 100, message = "Convênio deve ter no máximo 100 caracteres")
    private String convenio;

    public com.fiap.begin_projetct.model.Paciente toEntity() {
        com.fiap.begin_projetct.model.Paciente p = new com.fiap.begin_projetct.model.Paciente();
        p.setNome(this.nome);
        p.setCpf(this.cpf);
        p.setEmail(this.email);
        p.setTelefone(this.telefone);
        p.setDataNascimento(this.dataNascimento);
        p.setConvenio(this.convenio);
        return p;
    }
}
