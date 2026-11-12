
package com.matchpet.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "pets")
public class Pet {

    @Id
    private String id;

    private String nome;
    private String especie;
    private int idade;

    private Localizacao localizacao;
    private List<String> caracteristicas;

    public Pet() {
    }

    public Pet(String id, String nome, String especie, int idade) {
        this.id = id;
        this.nome = nome;
        this.especie = especie;
        this.idade = idade;
    }

    public Pet(String id, String nome, String especie, int idade,
               Localizacao localizacao, List<String> caracteristicas) {
        this.id = id;
        this.nome = nome;
        this.especie = especie;
        this.idade = idade;
        this.localizacao = localizacao;
        this.caracteristicas = caracteristicas;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    public List<String> getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(List<String> caracteristicas) {
        this.caracteristicas = caracteristicas;
    }
}
