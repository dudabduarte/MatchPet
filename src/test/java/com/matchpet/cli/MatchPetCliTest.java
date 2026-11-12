
package com.matchpet.cli;

import com.matchpet.model.Localizacao;

import com.matchpet.model.Pet;
import com.matchpet.service.PetService;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MatchPetCliTest {

    private String executarCli(PetService service, String entradas) {

        ByteArrayInputStream input = new ByteArrayInputStream(
                entradas.getBytes(StandardCharsets.UTF_8));

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        MatchPetCli cli = new MatchPetCli(
                service,
                input,
                new PrintStream(output)
        );

        cli.run();

        return output.toString();
    }

    @Test
    void deveSairAoEscolherZero() {

        PetService service = mock(PetService.class);

        String resultado = executarCli(service, "0\n");

        assertTrue(resultado.contains("Até logo!"));

        verifyNoInteractions(service);
    }

    @Test
    void deveListarPets() {

        PetService service = mock(PetService.class);

        Pet pet = new Pet("123", "Luna", "Gato", 2);

        when(service.listar()).thenReturn(List.of(pet));

        String resultado = executarCli(service, "1\n0\n");

        assertTrue(resultado.contains("Luna"));
        assertTrue(resultado.contains("Total: 1 pet(s)."));

        verify(service).listar();
    }

    @Test
    void deveInformarQuandoNaoExistemPets() {

        PetService service = mock(PetService.class);

        when(service.listar()).thenReturn(List.of());

        String resultado = executarCli(service, "1\n0\n");

        assertTrue(resultado.contains("Nenhum pet cadastrado."));
    }

    @Test
    void deveCadastrarPetComLocalizacaoECaracteristicas() {

        PetService service = mock(PetService.class);

        Pet pet = new Pet("123", "Luna", "Cachorro", 3);

        when(service.cadastrar(
                "Luna",
                "Cachorro",
                3,
                "Maringá",
                "PR",
                List.of("Dócil", "Brincalhona")
        )).thenReturn(pet);

        String resultado = executarCli(
                service,
                "3\nLuna\nCachorro\n3\nMaringá\nPR\nDócil, Brincalhona\n0\n"
        );

        assertTrue(resultado.contains("Pet cadastrado com ID: 123"));

        verify(service).cadastrar(
                "Luna",
                "Cachorro",
                3,
                "Maringá",
                "PR",
                List.of("Dócil", "Brincalhona")
        );
    }

    @Test
    void deveSolicitarIdadeNovamenteSemPerderDados() {

        PetService service = mock(PetService.class);

        Pet pet = new Pet("123", "Luna", "Gato", 0);

        when(service.cadastrar(
                "Luna",
                "Gato",
                0,
                "Maringá",
                "PR",
                List.of("Mansa")
        )).thenReturn(pet);

        String resultado = executarCli(
                service,
                "3\nLuna\nGato\n-1\nabc\n0\nMaringá\nPR\nMansa\n0\n"
        );

        assertTrue(resultado.contains(
                "Informe uma idade inteira maior ou igual a zero."
        ));

        verify(service).cadastrar(
                "Luna",
                "Gato",
                0,
                "Maringá",
                "PR",
                List.of("Mansa")
        );
    }

    @Test
    void deveOrientarQuandoBancoEstiverIndisponivel() {

        PetService service = mock(PetService.class);

        when(service.listar()).thenThrow(
                new DataAccessResourceFailureException("Banco indisponível")
        );

        String resultado = executarCli(service, "1\n0\n");

        assertTrue(resultado.contains("Erro ao acessar o MongoDB:"));
    }


    @Test
    void deveBuscarPetPeloId() {
        PetService service = mock(PetService.class);

        Pet pet = new Pet("123", "Luna", "Gato", 2);

        when(service.buscar("123")).thenReturn(pet);

        String resultado = executarCli(service, "2\n123\n0\n");

        assertTrue(resultado.contains("Luna"));
        assertTrue(resultado.contains("Gato"));

        verify(service).buscar("123");
    }

    @Test
    void deveAtualizarPet() {
        PetService service = mock(PetService.class);

        Pet atual = new Pet("123", "Luna", "Gato", 2);
        Pet atualizado = new Pet("123", "Luna Nova", "Cachorro", 3);

        when(service.buscar("123")).thenReturn(atual);

        when(service.atualizar(
                "123",
                "Luna Nova",
                "Cachorro",
                3,
                "",
                "",
                List.of()
        )).thenReturn(atualizado);

        String resultado = executarCli(
                service,
                "4\n123\nLuna Nova\nCachorro\n3\n\n\n\n0\n"
        );

        assertTrue(resultado.contains("Pet atualizado: Luna Nova"));

        verify(service).atualizar(
                "123",
                "Luna Nova",
                "Cachorro",
                3,
                "",
                "",
                List.of()
        );
    }

    @Test
    void deveManterDadosAoAtualizarQuandoPressionarEnter() {
        PetService service = mock(PetService.class);

        Pet atual = new Pet("123", "Luna", "Gato", 2);

        when(service.buscar("123")).thenReturn(atual);

        when(service.atualizar(
                "123",
                "Luna",
                "Gato",
                2,
                "",
                "",
                List.of()
        )).thenReturn(atual);

        String resultado = executarCli(
                service,
                "4\n123\n\n\n\n\n\n\n0\n"
        );

        assertTrue(resultado.contains("Pet atualizado: Luna"));

        verify(service).atualizar(
                "123",
                "Luna",
                "Gato",
                2,
                "",
                "",
                List.of()
        );
    }

    @Test
    void deveExcluirPetQuandoConfirmar() {
        PetService service = mock(PetService.class);

        Pet pet = new Pet("123", "Luna", "Gato", 2);

        when(service.buscar("123")).thenReturn(pet);

        String resultado = executarCli(
                service,
                "5\n123\ns\n0\n"
        );

        assertTrue(resultado.contains("Pet excluído."));

        verify(service).excluir("123");
    }

    @Test
    void deveCancelarExclusaoQuandoNaoConfirmar() {
        PetService service = mock(PetService.class);

        Pet pet = new Pet("123", "Luna", "Gato", 2);

        when(service.buscar("123")).thenReturn(pet);

        String resultado = executarCli(
                service,
                "5\n123\nn\n0\n"
        );

        assertTrue(resultado.contains("Exclusão cancelada."));

        verify(service, never()).excluir("123");
    }

    @Test
    void deveInformarQuandoEscolherOpcaoInvalida() {
        PetService service = mock(PetService.class);

        String resultado = executarCli(service, "9\n0\n");

        assertTrue(resultado.contains("Opção inválida."));

        verifyNoInteractions(service);
    }

    @Test
    void deveInformarErroQuandoPetNaoForEncontrado() {
        PetService service = mock(PetService.class);

        when(service.buscar("999"))
                .thenThrow(new IllegalArgumentException("Pet não encontrado: 999"));

        String resultado = executarCli(service, "2\n999\n0\n");

        assertTrue(resultado.contains("Erro: Pet não encontrado: 999"));
    }

    @Test
    void deveEncerrarQuandoEntradaForFinalizada() {
        PetService service = mock(PetService.class);

        String resultado = executarCli(service, "");

        assertTrue(resultado.contains("Entrada encerrada."));
        assertTrue(resultado.contains("Até logo!"));

        verifyNoInteractions(service);
    }

    @Test
    void deveImprimirPetComLocalizacaoECaracteristicas() {
        PetService service = mock(PetService.class);

        Localizacao localizacao = new Localizacao("Maringá", "PR");

        Pet pet = new Pet(
                "123",
                "Luna",
                "Gato",
                2,
                localizacao,
                List.of("Dócil", "Brincalhona")
        );

        when(service.listar()).thenReturn(List.of(pet));

        String resultado = executarCli(service, "1\n0\n");

        assertTrue(resultado.contains("Cidade: Maringá"));
        assertTrue(resultado.contains("Estado: PR"));
        assertTrue(resultado.contains("Características: [Dócil, Brincalhona]"));
    }

    @Test
    void deveIgnorarCaracteristicasVaziasAoCadastrar() {
        PetService service = mock(PetService.class);

        Pet pet = new Pet("123", "Luna", "Gato", 2);

        when(service.cadastrar(
                "Luna",
                "Gato",
                2,
                "Maringá",
                "PR",
                List.of("Dócil", "Brincalhona")
        )).thenReturn(pet);

        String resultado = executarCli(
                service,
                "3\nLuna\nGato\n2\nMaringá\nPR\nDócil, , Brincalhona, \n0\n"
        );

        assertTrue(resultado.contains("Pet cadastrado com ID: 123"));

        verify(service).cadastrar(
                "Luna",
                "Gato",
                2,
                "Maringá",
                "PR",
                List.of("Dócil", "Brincalhona")
        );
    }

    @Test
    void deveExcluirPetQuandoConfirmarComLetraMaiuscula() {
        PetService service = mock(PetService.class);

        Pet pet = new Pet("123", "Luna", "Gato", 2);

        when(service.buscar("123")).thenReturn(pet);

        String resultado = executarCli(
                service,
                "5\n123\nS\n0\n"
        );

        assertTrue(resultado.contains("Pet excluído."));

        verify(service).excluir("123");
    }

}
