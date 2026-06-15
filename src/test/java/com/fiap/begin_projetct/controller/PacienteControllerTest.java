package com.fiap.begin_projetct.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.begin_projetct.dto.PacienteRequest;
import com.fiap.begin_projetct.model.Paciente;
import com.fiap.begin_projetct.repository.UsuarioRepository;
import com.fiap.begin_projetct.service.PacienteService;
import com.fiap.begin_projetct.infra.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.context.annotation.Import;
import com.fiap.begin_projetct.infra.security.SecurityConfigurations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PacienteController.class)
@Import(SecurityConfigurations.class)
class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PacienteService pacienteService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    private Paciente paciente;

    @BeforeEach
    void setUp() {
        paciente = new Paciente();
        paciente.setId(1L);
        paciente.setNome("Maria Silva");
        paciente.setCpf("987.654.321-11");
        paciente.setEmail("maria@example.com");
        paciente.setTelefone("(11) 98888-8888");
        paciente.setDataNascimento(LocalDate.of(1985, 5, 5));
        paciente.setConvenio("Care Plus Premium");
    }

    @Test
    void deveBloquearAcessoSemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/pacientes"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@careplus.com")
    void deveListarTodosOsPacientesAutenticado() throws Exception {
        when(pacienteService.listarTodos()).thenReturn(List.of(paciente));

        mockMvc.perform(get("/api/pacientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Maria Silva"))
                .andExpect(jsonPath("$[0].cpf").value("987.654.321-11"));
    }

    @Test
    @WithMockUser(username = "admin@careplus.com")
    void deveBuscarPacientePorIdExistente() throws Exception {
        when(pacienteService.buscarPorId(1L)).thenReturn(Optional.of(paciente));

        mockMvc.perform(get("/api/pacientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria Silva"));
    }

    @Test
    @WithMockUser(username = "admin@careplus.com")
    void deveRetornarNotFoundBuscarPacienteInexistente() throws Exception {
        when(pacienteService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pacientes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin@careplus.com")
    void deveCriarPacienteComSucesso() throws Exception {
        PacienteRequest request = new PacienteRequest();
        request.setNome("Maria Silva");
        request.setCpf("987.654.321-11");
        request.setEmail("maria@example.com");
        request.setTelefone("(11) 98888-8888");

        when(pacienteService.salvar(any(Paciente.class))).thenReturn(paciente);

        mockMvc.perform(post("/api/pacientes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Maria Silva"));
    }

    @Test
    @WithMockUser(username = "admin@careplus.com")
    void deveRetornarBadRequestCriarPacienteComCpfInvalido() throws Exception {
        PacienteRequest request = new PacienteRequest();
        request.setNome("Maria Silva");
        request.setCpf(""); // Inválido (NotBlank)
        request.setEmail("maria@example.com");
        request.setTelefone("(11) 98888-8888");

        mockMvc.perform(post("/api/pacientes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
