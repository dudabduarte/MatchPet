package com.matchpet.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.matchpet.model.Pet;
import com.matchpet.service.PetService;

@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "*")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    // LISTAR TODOS OS PETS
    @GetMapping
    public List<Pet> listar() {
        return petService.listar();
    }

    // BUSCAR PET PELO ID
    @GetMapping("/{id}")
    public Pet buscar(@PathVariable String id) {
        return petService.buscar(id);
    }

    // CADASTRAR PET
    @PostMapping
    public ResponseEntity<Pet> cadastrar(
            @RequestBody CadastroPetRequest dados) {

        Pet pet = petService.cadastrar(
                dados.nome(),
                dados.especie(),
                dados.idade(),
                dados.cidade(),
                dados.estado(),
                dados.caracteristicas()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(pet);
    }

    /// ATUALIZAR PET
    @PutMapping("/{id}")
    public Pet atualizar(
            @PathVariable String id,
            @RequestBody AtualizarPetRequest dados) {

        return petService.atualizar(
                id,
                dados.nome(),
                dados.especie(),
                dados.idade(),
                dados.cidade(),
                dados.estado(),
                dados.caracteristicas()
        );
    }

    // EXCLUIR PET
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {

        petService.excluir(id);

        return ResponseEntity.noContent().build();
    }

    // DADOS RECEBIDOS NO CADASTRO
    public record CadastroPetRequest(
            String nome,
            String especie,
            int idade,
            String cidade,
            String estado,
            List<String> caracteristicas
    ) {
    }

    // DADOS RECEBIDOS NA ATUALIZAÇÃO
    public record AtualizarPetRequest(
            String nome,
            String especie,
            int idade,
            String cidade,
            String estado,
            List<String> caracteristicas
    ) {}
}
