
package com.matchpet.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "adocoes")
public class Adocao {

    @Id
    private String id;

    private String usuarioId;
    private String petId;
    private String data;
    private String status;

    public Adocao() {
    }

    public Adocao(String id, String usuarioId, String petId,
                  String data, String status) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.petId = petId;
        this.data = data;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
