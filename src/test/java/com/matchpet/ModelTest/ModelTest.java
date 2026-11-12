
package com.matchpet.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    void deveCriarPetVazio() {
        Pet pet = new Pet();

        assertNull(pet.getId());
        assertNull(pet.getNome());
        assertNull(pet.getEspecie());
        assertEquals(0, pet.getIdade());
        assertNull(pet.getLocalizacao());
        assertNull(pet.getCaracteristicas());
    }

    @Test
    void deveCriarPetCompleto() {
        Localizacao localizacao = new Localizacao("Maringá", "PR");
        List<String> caracteristicas = List.of("Brincalhão", "Dócil");

        Pet pet = new Pet("1", "Luna", "Gato", 2,
                localizacao, caracteristicas);

        assertEquals("1", pet.getId());
        assertEquals("Luna", pet.getNome());
        assertEquals("Gato", pet.getEspecie());
        assertEquals(2, pet.getIdade());
        assertEquals(localizacao, pet.getLocalizacao());
        assertEquals(caracteristicas, pet.getCaracteristicas());
    }

    @Test
    void deveAlterarDadosDoPet() {
        Pet pet = new Pet();

        Localizacao localizacao = new Localizacao("Curitiba", "PR");
        List<String> caracteristicas = List.of("Calmo");

        pet.setNome("Thor");
        pet.setEspecie("Cachorro");
        pet.setIdade(3);
        pet.setLocalizacao(localizacao);
        pet.setCaracteristicas(caracteristicas);

        assertEquals("Thor", pet.getNome());
        assertEquals("Cachorro", pet.getEspecie());
        assertEquals(3, pet.getIdade());
        assertEquals(localizacao, pet.getLocalizacao());
        assertEquals(caracteristicas, pet.getCaracteristicas());
    }

    @Test
    void deveCriarUsuarioVazio() {
        Usuario usuario = new Usuario();

        assertNull(usuario.getId());
        assertNull(usuario.getNome());
        assertNull(usuario.getEmail());
        assertNull(usuario.getTelefone());
    }

    @Test
    void deveCriarUsuarioCompleto() {
        Usuario usuario = new Usuario(
                "1", "Maria", "maria@email.com", "44999999999");

        assertEquals("1", usuario.getId());
        assertEquals("Maria", usuario.getNome());
        assertEquals("maria@email.com", usuario.getEmail());
        assertEquals("44999999999", usuario.getTelefone());
    }

    @Test
    void deveAlterarDadosDoUsuario() {
        Usuario usuario = new Usuario();

        usuario.setNome("João");
        usuario.setEmail("joao@email.com");
        usuario.setTelefone("44988888888");

        assertEquals("João", usuario.getNome());
        assertEquals("joao@email.com", usuario.getEmail());
        assertEquals("44988888888", usuario.getTelefone());
    }

    @Test
    void deveCriarLocalizacaoVazia() {
        Localizacao localizacao = new Localizacao();

        assertNull(localizacao.getCidade());
        assertNull(localizacao.getEstado());
    }

    @Test
    void deveCriarLocalizacaoCompleta() {
        Localizacao localizacao = new Localizacao("Maringá", "PR");

        assertEquals("Maringá", localizacao.getCidade());
        assertEquals("PR", localizacao.getEstado());
    }

    @Test
    void deveAlterarLocalizacao() {
        Localizacao localizacao = new Localizacao();

        localizacao.setCidade("Londrina");
        localizacao.setEstado("PR");

        assertEquals("Londrina", localizacao.getCidade());
        assertEquals("PR", localizacao.getEstado());
    }

    @Test
    void deveCriarAdocaoVazia() {
        Adocao adocao = new Adocao();

        assertNull(adocao.getId());
        assertNull(adocao.getUsuarioId());
        assertNull(adocao.getPetId());
        assertNull(adocao.getData());
        assertNull(adocao.getStatus());
    }

    @Test
    void deveCriarAdocaoCompleta() {
        Adocao adocao = new Adocao(
                "1", "10", "20", "10/11/2026", "Pendente");

        assertEquals("1", adocao.getId());
        assertEquals("10", adocao.getUsuarioId());
        assertEquals("20", adocao.getPetId());
        assertEquals("10/11/2026", adocao.getData());
        assertEquals("Pendente", adocao.getStatus());
    }

    @Test
    void deveAlterarDadosDaAdocao() {
        Adocao adocao = new Adocao();

        adocao.setUsuarioId("15");
        adocao.setPetId("25");
        adocao.setData("11/11/2026");
        adocao.setStatus("Aprovada");

        assertEquals("15", adocao.getUsuarioId());
        assertEquals("25", adocao.getPetId());
        assertEquals("11/11/2026", adocao.getData());
        assertEquals("Aprovada", adocao.getStatus());
    }
}
