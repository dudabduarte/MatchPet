package com.matchpet.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.matchpet.model.Pet;

public interface PetRepository extends MongoRepository<Pet, String> {

    List<Pet> findAllByOrderByNomeAsc();
}
