package com.matchpet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.matchpet.model.Pet;

import java.util.List;

public interface PetRepository extends MongoRepository<Pet, String> {

    List<Pet> findAllByOrderByNomeAsc();
}