package com.example.ruletrack.service;

import com.example.ruletrack.dto.ReglamentoRequestDTO;
import com.example.ruletrack.dto.ReglamentoResponseDTO;
import com.example.ruletrack.entity.Reglamento;
import com.example.ruletrack.entity.Usuario;
import com.example.ruletrack.exception.ResourceNotFoundException;
import com.example.ruletrack.repository.ReglamentoRepository;
import com.example.ruletrack.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReglamentoService {

    private final ReglamentoRepository reglamentoRepository;
    private final UsuarioRepository usuarioRepository;

    public List<ReglamentoResponseDTO> findAll() {
        return reglamentoRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public ReglamentoResponseDTO findById(Long id) {
        return toDTO(getReglamentoOrThrow(id));
    }

    @Transactional
    public ReglamentoResponseDTO create(ReglamentoRequestDTO request) {
        Usuario currentUser = getCurrentUser();
        Reglamento reglamento = Reglamento.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .creadoPor(currentUser)
                .build();
        return toDTO(reglamentoRepository.save(reglamento));
    }

    @Transactional
    public ReglamentoResponseDTO update(Long id, ReglamentoRequestDTO request) {
        Reglamento reglamento = getReglamentoOrThrow(id);
        reglamento.setTitulo(request.getTitulo());
        reglamento.setDescripcion(request.getDescripcion());
        return toDTO(reglamentoRepository.save(reglamento));
    }

    @Transactional
    public void delete(Long id) {
        if (!reglamentoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reglamento", id);
        }
        reglamentoRepository.deleteById(id);
    }

    private Reglamento getReglamentoOrThrow(Long id) {
        return reglamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reglamento", id));
    }

    private Usuario getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
    }

    private ReglamentoResponseDTO toDTO(Reglamento r) {
        return ReglamentoResponseDTO.builder()
                .id(r.getId())
                .titulo(r.getTitulo())
                .descripcion(r.getDescripcion())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .creadoPorUsername(r.getCreadoPor().getUsername())
                .totalVersiones(r.getVersiones().size())
                .build();
    }
}
