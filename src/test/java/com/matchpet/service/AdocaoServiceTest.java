package com.matchpet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.matchpet.model.Adocao;
import com.matchpet.repository.AdocaoRepository;
import com.matchpet.repository.PetRepository;
import com.matchpet.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class AdocaoServiceTest {

    @Mock
    private AdocaoRepository adocaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private AdocaoService adocaoService;

    @BeforeEach
    void configurar() {
        lenient().when(usuarioRepository.existsById("usuario-1")).thenReturn(true);
        lenient().when(petRepository.existsById("pet-1")).thenReturn(true);
        lenient().when(adocaoRepository.existsByPetIdAndStatusIgnoreCase(
                "pet-1", "APROVADA")).thenReturn(false);
        lenient().when(adocaoRepository.findByUsuarioIdAndPetId("usuario-1", "pet-1"))
                .thenReturn(java.util.List.of());
    }

    @Test
    void deveCadastrarSolicitacaoPendente() {
        when(adocaoRepository.save(any(Adocao.class))).thenAnswer(invocacao -> {
            Adocao adocao = invocacao.getArgument(0);
            adocao.setData("2026-11-11T10:00:00");
            return adocao;
        });

        Adocao resultado = adocaoService.cadastrar("usuario-1", "pet-1");

        assertEquals("usuario-1", resultado.getUsuarioId());
        assertEquals("pet-1", resultado.getPetId());
        assertEquals("PENDENTE", resultado.getStatus());
        assertNotNull(resultado.getData());
        verify(adocaoRepository).save(any(Adocao.class));
    }

    @Test
    void naoDeveCadastrarQuandoUsuarioNaoExiste() {
        when(usuarioRepository.existsById("usuario-inexistente")).thenReturn(false);

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> adocaoService.cadastrar("usuario-inexistente", "pet-1"));

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
        verify(adocaoRepository, never()).save(any(Adocao.class));
    }

    @Test
    void naoDeveCadastrarQuandoPetNaoExiste() {
        when(petRepository.existsById("pet-inexistente")).thenReturn(false);

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> adocaoService.cadastrar("usuario-1", "pet-inexistente"));

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
        verify(adocaoRepository, never()).save(any(Adocao.class));
    }

    @Test
    void naoDevePermitirSolicitacaoDuplicada() {
        when(adocaoRepository.findByUsuarioIdAndPetId("usuario-1", "pet-1"))
                .thenReturn(java.util.List.of(new Adocao("adocao-existente", "usuario-1",
                        "pet-1", "2026-11-11T10:00:00", "Pendente")));

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> adocaoService.cadastrar("usuario-1", "pet-1"));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
        verify(adocaoRepository, never()).save(any(Adocao.class));
    }

    @Test
    void deveAtualizarStatusDeSolicitacaoPendente() {
        Adocao existente = new Adocao("adocao-1", "usuario-1", "pet-1",
                "2026-11-11T10:00:00", "PENDENTE");
        when(adocaoRepository.findById("adocao-1")).thenReturn(Optional.of(existente));
        when(adocaoRepository.existsByPetIdAndStatusIgnoreCaseAndIdNot(
                "pet-1", "APROVADA", "adocao-1")).thenReturn(false);
        when(adocaoRepository.save(existente)).thenReturn(existente);

        Adocao resultado = adocaoService.atualizarStatus("adocao-1", "aprovada");

        assertEquals("APROVADA", resultado.getStatus());
        verify(adocaoRepository).save(existente);
    }

    @Test
    void naoDeveAprovarQuandoPetJaPossuiOutraAdocaoAprovada() {
        Adocao existente = new Adocao("adocao-1", "usuario-1", "pet-1",
                "2026-11-11T10:00:00.000", "PENDENTE");
        when(adocaoRepository.findById("adocao-1")).thenReturn(Optional.of(existente));
        when(adocaoRepository.existsByPetIdAndStatusIgnoreCaseAndIdNot(
                "pet-1", "APROVADA", "adocao-1")).thenReturn(true);

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> adocaoService.atualizarStatus("adocao-1", "APROVADA"));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
        verify(adocaoRepository, never()).save(existente);
    }

    @Test
    void naoDeveAtualizarSolicitacaoQueJaFoiFinalizada() {
        Adocao existente = new Adocao("adocao-1", "usuario-1", "pet-1",
                "2026-11-11T10:00:00", "REJEITADA");
        when(adocaoRepository.findById("adocao-1")).thenReturn(Optional.of(existente));

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> adocaoService.atualizarStatus("adocao-1", "APROVADA"));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
        verify(adocaoRepository, never()).save(existente);
    }
}
