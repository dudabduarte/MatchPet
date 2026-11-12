
package com.matchpet.model;

public class Localizacao {

    private String cidade;
    private String estado;

    public Localizacao() {
    }

    public Localizacao(String cidade, String estado) {
        this.cidade = cidade;
        this.estado = estado;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
