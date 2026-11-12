
package com.matchpet.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.matchpet.model.Adocao;

public interface AdocaoRepository extends MongoRepository<Adocao, String> {

    List<Adocao> findAllByOrderByDataDesc();

    List<Adocao> findByUsuarioIdOrderByDataDesc(String usuarioId);

    List<Adocao> findByPetIdOrderByDataDesc(String petId);

    List<Adocao> findByUsuarioIdAndPetId(String usuarioId, String petId);

    boolean existsByPetIdAndStatusIgnoreCase(String petId, String status);

    boolean existsByPetIdAndStatusIgnoreCaseAndIdNot(
            String petId, String status, String id);
}
