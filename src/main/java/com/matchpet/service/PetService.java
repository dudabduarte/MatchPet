package com.matchpet.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.matchpet.model.Pet;
import com.matchpet.repository.PetRepository;

@Service
public class PetService {

    private final PetRepository repository;

    public PetService(PetRepository repository) {
        this.repository = repository;
    }

    public List<Pet> listar() {
        return repository.findAllByOrderByNomeAsc();
    }

    public Pet buscar(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Informe o ID do pet.");
        }
        return repository.findById(id.trim())
                .orElseThrow(() -> new IllegalArgumentException("Pet não encontrado: " + id));
    }

    public Pet cadastrar(String nome, String especie, int idade) {
        validar(nome, especie, idade);
        return repository.save(new Pet(null, nome.trim(), especie.trim(), idade));
    }

    public Pet atualizar(String id, String nome, String especie, int idade) {
        validar(nome, especie, idade);
        Pet pet = buscar(id);
        pet.setNome(nome.trim());
        pet.setEspecie(especie.trim());
        pet.setIdade(idade);
        return repository.save(pet);
    }

    public void excluir(String id) {
        Pet pet = buscar(id);
        repository.delete(pet);
    }

    private void validar(String nome, String especie, int idade) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }
        if (especie == null || especie.isBlank()) {
            throw new IllegalArgumentException("A espécie é obrigatória.");
        }
        if (idade < 0) {
            throw new IllegalArgumentException("A idade não pode ser negativa.");
        }
    }
}
