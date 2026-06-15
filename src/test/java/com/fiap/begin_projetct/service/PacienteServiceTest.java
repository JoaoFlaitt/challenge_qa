package com.fiap.begin_projetct.service;

import com.fiap.begin_projetct.exception.PacienteAlreadyExistsException;
import com.fiap.begin_projetct.exception.PacienteNotFoundException;
import com.fiap.begin_projetct.model.Paciente;
import com.fiap.begin_projetct.repository.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private PacienteService pacienteService;

    private Paciente paciente;

    @BeforeEach
    void setUp() {
        paciente = new Paciente();
        paciente.setId(1L);
        paciente.setNome("João da Silva");
        paciente.setCpf("123.456.789-00");
        paciente.setEmail("joao@example.com");
        paciente.setTelefone("(11) 99999-9999");
        paciente.setDataNascimento(LocalDate.of(1990, 1, 1));
        paciente.setConvenio("Care Plus Premium");
    }

    @Test
    void deveListarTodosOsPacientes() {
        when(pacienteRepository.findAll()).thenReturn(List.of(paciente));

        List<Paciente> result = pacienteService.listarTodos();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("João da Silva", result.get(0).getNome());
        verify(pacienteRepository, times(1)).findAll();
    }

    @Test
    void deveBuscarPacientePorIdComSucesso() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));

        Optional<Paciente> result = pacienteService.buscarPorId(1L);

        assertTrue(result.isPresent());
        assertEquals("João da Silva", result.get().getNome());
        verify(pacienteRepository, times(1)).findById(1L);
    }

    @Test
    void deveBuscarPacientePorCpfComSucesso() {
        when(pacienteRepository.findByCpf("123.456.789-00")).thenReturn(Optional.of(paciente));

        Optional<Paciente> result = pacienteService.buscarPorCpf("123.456.789-00");

        assertTrue(result.isPresent());
        assertEquals("João da Silva", result.get().getNome());
        verify(pacienteRepository, times(1)).findByCpf("123.456.789-00");
    }

    @Test
    void deveSalvarPacienteComSucesso() {
        when(pacienteRepository.existsByCpf(paciente.getCpf())).thenReturn(false);
        when(pacienteRepository.existsByEmail(paciente.getEmail())).thenReturn(false);
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(paciente);

        Paciente result = pacienteService.salvar(paciente);

        assertNotNull(result);
        assertEquals("João da Silva", result.getNome());
        verify(pacienteRepository, times(1)).save(paciente);
    }

    @Test
    void deveLancarExcecaoAoSalvarCpfDuplicado() {
        when(pacienteRepository.existsByCpf(paciente.getCpf())).thenReturn(true);

        assertThrows(PacienteAlreadyExistsException.class, () -> pacienteService.salvar(paciente));
        verify(pacienteRepository, never()).save(any(Paciente.class));
    }

    @Test
    void deveLancarExcecaoAoSalvarEmailDuplicado() {
        when(pacienteRepository.existsByCpf(paciente.getCpf())).thenReturn(false);
        when(pacienteRepository.existsByEmail(paciente.getEmail())).thenReturn(true);

        assertThrows(PacienteAlreadyExistsException.class, () -> pacienteService.salvar(paciente));
        verify(pacienteRepository, never()).save(any(Paciente.class));
    }

    @Test
    void deveAtualizarPacienteComSucesso() {
        Paciente pacienteAtualizado = new Paciente();
        pacienteAtualizado.setNome("João Silva Junior");
        pacienteAtualizado.setCpf("123.456.789-00");
        pacienteAtualizado.setEmail("joao.junior@example.com");

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(pacienteRepository.existsByEmail(pacienteAtualizado.getEmail())).thenReturn(false);
        when(pacienteRepository.save(any(Paciente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Paciente result = pacienteService.atualizar(1L, pacienteAtualizado);

        assertNotNull(result);
        assertEquals("João Silva Junior", result.getNome());
        assertEquals("joao.junior@example.com", result.getEmail());
        verify(pacienteRepository, times(1)).save(any(Paciente.class));
    }

    @Test
    void deveDeletarPacienteComSucesso() {
        when(pacienteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(pacienteRepository).deleteById(1L);

        assertDoesNotThrow(() -> pacienteService.deletar(1L));

        verify(pacienteRepository, times(1)).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarPacienteInexistente() {
        when(pacienteRepository.existsById(1L)).thenReturn(false);

        assertThrows(PacienteNotFoundException.class, () -> pacienteService.deletar(1L));
        verify(pacienteRepository, never()).deleteById(anyLong());
    }
}
