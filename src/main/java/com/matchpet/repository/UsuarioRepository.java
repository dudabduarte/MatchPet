
package com.matchpet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.matchpet.model.Usuario;

public interface UsuarioRepository
        extends MongoRepository<Usuario, String> {
}
