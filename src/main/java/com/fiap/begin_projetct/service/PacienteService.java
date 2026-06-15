package com.fiap.begin_projetct.service;

import com.fiap.begin_projetct.exception.PacienteAlreadyExistsException;
import com.fiap.begin_projetct.exception.PacienteNotFoundException;
import com.fiap.begin_projetct.model.Paciente;
import com.fiap.begin_projetct.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PacienteService {
    
    private final PacienteRepository pacienteRepository;
    
    @Transactional(readOnly = true)
    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Paciente> buscarPorId(Long id) {
        return pacienteRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Paciente> buscarPorCpf(String cpf) {
        return pacienteRepository.findByCpf(cpf);
    }
    
    @Transactional
    public Paciente salvar(Paciente paciente) {
        if (pacienteRepository.existsByCpf(paciente.getCpf())) {
            throw new PacienteAlreadyExistsException("Já existe um paciente com este CPF");
        }
        if (pacienteRepository.existsByEmail(paciente.getEmail())) {
            throw new PacienteAlreadyExistsException("Já existe um paciente com este e-mail");
        }
        return pacienteRepository.save(paciente);
    }
    
    @Transactional
    public Paciente atualizar(Long id, Paciente paciente) {
        return pacienteRepository.findById(id)
                .map(pacienteExistente -> {
                    // Check if CPF is being changed and if new CPF already exists
                    if (!pacienteExistente.getCpf().equals(paciente.getCpf()) && 
                        pacienteRepository.existsByCpf(paciente.getCpf())) {
                        throw new PacienteAlreadyExistsException("Já existe um paciente com este CPF");
                    }
                    // Check if email is being changed and if new email already exists
                    if (!pacienteExistente.getEmail().equals(paciente.getEmail()) && 
                        pacienteRepository.existsByEmail(paciente.getEmail())) {
                        throw new PacienteAlreadyExistsException("Já existe um paciente com este e-mail");
                    }
                    paciente.setId(id);
                    return pacienteRepository.save(paciente);
                })
                .orElseThrow(() -> new PacienteNotFoundException("Paciente não encontrado"));
    }
    
    @Transactional
    public void deletar(Long id) {
        if (!pacienteRepository.existsById(id)) {
            throw new PacienteNotFoundException("Paciente não encontrado");
        }
        pacienteRepository.deleteById(id);
    }
}
