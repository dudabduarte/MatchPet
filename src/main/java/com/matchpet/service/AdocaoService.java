
package com.matchpet.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.matchpet.model.Adocao;
import com.matchpet.repository.AdocaoRepository;
import com.matchpet.repository.UsuarioRepository;
import com.matchpet.repository.PetRepository;

@Service
public class AdocaoService {

    private static final String PENDENTE = "PENDENTE";
    private static final String APROVADA = "APROVADA";
    private static final String REJEITADA = "REJEITADA";

    private final AdocaoRepository adocaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PetRepository petRepository;

    public AdocaoService(AdocaoRepository adocaoRepository,
                         UsuarioRepository usuarioRepository,
                         PetRepository petRepository) {
        this.adocaoRepository = adocaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.petRepository = petRepository;
    }

    public Adocao cadastrar(String usuarioId, String petId) {
        validarIdentificadores(usuarioId, petId);

        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Usuário não encontrado.");
        }

        if (!petRepository.existsById(petId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Pet não encontrado.");
        }

        if (adocaoRepository.existsByPetIdAndStatusIgnoreCase(petId, APROVADA)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este pet já possui uma adoção aprovada.");
        }

        boolean jaExisteSolicitacaoAtiva = adocaoRepository
                .findByUsuarioIdAndPetId(usuarioId, petId)
                .stream()
                .anyMatch(solicitacao ->
                        PENDENTE.equalsIgnoreCase(solicitacao.getStatus())
                                || APROVADA.equalsIgnoreCase(solicitacao.getStatus()));

        if (jaExisteSolicitacaoAtiva) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe uma solicitação pendente ou aprovada para este usuário e pet.");
        }

        DateTimeFormatter formatoData =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

        String data = LocalDateTime.now().format(formatoData);

        Adocao adocao = new Adocao(
                null, usuarioId, petId, data, PENDENTE);

        return adocaoRepository.save(adocao);
    }

    public List<Adocao> listar() {
        return adocaoRepository.findAllByOrderByDataDesc();
    }

    public Adocao buscar(String id) {
        if (id == null || id.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe o ID da solicitação.");
        }

        return adocaoRepository.findById(id.trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Solicitação de adoção não encontrada."));
    }

    public List<Adocao> listarPorUsuario(String usuarioId) {
        if (usuarioId == null || usuarioId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe o ID do usuário.");
        }

        return adocaoRepository.findByUsuarioIdOrderByDataDesc(
                usuarioId.trim());
    }

    public List<Adocao> listarPorPet(String petId) {
        if (petId == null || petId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe o ID do pet.");
        }

        return adocaoRepository.findByPetIdOrderByDataDesc(
                petId.trim());
    }

    public Adocao atualizarStatus(String id, String novoStatus) {
        Adocao adocao = buscar(id);
        String status = normalizarStatus(novoStatus);

        if (!PENDENTE.equalsIgnoreCase(adocao.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Somente solicitações pendentes podem ser atualizadas.");
        }

        if (APROVADA.equals(status)
                && adocaoRepository.existsByPetIdAndStatusIgnoreCaseAndIdNot(
                adocao.getPetId(), APROVADA, adocao.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este pet já possui outra solicitação aprovada.");
        }

        adocao.setStatus(status);
        return adocaoRepository.save(adocao);
    }

    private String normalizarStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Informe o novo status.");
        }

        String normalizado = status.trim().toUpperCase(Locale.ROOT);

        if (!APROVADA.equals(normalizado)
                && !REJEITADA.equals(normalizado)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status inválido. Utilize APROVADA ou REJEITADA.");
        }

        return normalizado;
    }

    private void validarIdentificadores(String usuarioId, String petId) {
        if (usuarioId == null || usuarioId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O ID do usuário é obrigatório.");
        }

        if (petId == null || petId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O ID do pet é obrigatório.");
        }
    }
}
