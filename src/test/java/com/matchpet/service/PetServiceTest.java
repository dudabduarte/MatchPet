package com.matchpet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.matchpet.model.Pet;
import com.matchpet.repository.PetRepository;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository repository;

    private PetService service;

    @BeforeEach
    void configurar() {
        service = new PetService(repository);
    }

    @Test
    void deveListarPets() {
        Pet luna = new Pet("1", "Luna", "Cachorro", 3);
        when(repository.findAllByOrderByNomeAsc()).thenReturn(List.of(luna));

        assertEquals(List.of(luna), service.listar());
    }

    @Test
    void deveBuscarPet() {
        Pet luna = new Pet("1", "Luna", "Cachorro", 3);
        when(repository.findById("1")).thenReturn(Optional.of(luna));

        assertEquals(luna, service.buscar("1"));
    }

    @Test
    void deveInformarQuandoPetNaoExiste() {
        when(repository.findById("999")).thenReturn(Optional.empty());

        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class, () -> service.buscar("999"));

        assertEquals("Pet não encontrado: 999", erro.getMessage());
    }

    @Test
    void deveCadastrarPetComDadosSimples() {
        when(repository.save(org.mockito.ArgumentMatchers.any(Pet.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        service.cadastrar(" Luna ", " Cachorro ", 3);

        ArgumentCaptor<Pet> captor = ArgumentCaptor.forClass(Pet.class);
        verify(repository).save(captor.capture());
        assertEquals("Luna", captor.getValue().getNome());
        assertEquals("Cachorro", captor.getValue().getEspecie());
        assertEquals(3, captor.getValue().getIdade());
    }

    @Test
    void deveAtualizarPet() {
        Pet pet = new Pet("1", "Luna", "Cachorro", 3);
        when(repository.findById("1")).thenReturn(Optional.of(pet));
        when(repository.save(pet)).thenReturn(pet);

        Pet atualizado = service.atualizar("1", "Mia", "Gato", 4);

        assertEquals("Mia", atualizado.getNome());
        assertEquals("Gato", atualizado.getEspecie());
        assertEquals(4, atualizado.getIdade());
    }

    @Test
    void deveExcluirPet() {
        Pet pet = new Pet("1", "Luna", "Cachorro", 3);
        when(repository.findById("1")).thenReturn(Optional.of(pet));

        service.excluir("1");

        verify(repository).delete(pet);
    }

    @Test
    void deveValidarDadosObrigatorios() {
        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(" ", "Gato", 2));
        assertThrows(IllegalArgumentException.class, () -> service.cadastrar("Luna", " ", 2));
        assertThrows(IllegalArgumentException.class, () -> service.cadastrar("Luna", "Gato", -1));
    }
}
