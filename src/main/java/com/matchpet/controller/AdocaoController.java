package com.matchpet.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matchpet.model.Adocao;
import com.matchpet.service.AdocaoService;

@RestController
@RequestMapping("/api/adocoes")
@CrossOrigin(origins = "*")
public class AdocaoController {

    private final AdocaoService adocaoService;

    public AdocaoController(AdocaoService adocaoService) {
        this.adocaoService = adocaoService;
    }

    @PostMapping
    public ResponseEntity<Adocao> cadastrar(@RequestBody CriarAdocaoRequest dados) {
        Adocao adocao = adocaoService.cadastrar(dados.usuarioId(), dados.petId());
        return ResponseEntity.status(HttpStatus.CREATED).body(adocao);
    }

    @GetMapping
    public List<Adocao> listar() {
        return adocaoService.listar();
    }

    @GetMapping("/{id}")
    public Adocao buscar(@PathVariable String id) {
        return adocaoService.buscar(id);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Adocao> listarPorUsuario(@PathVariable String usuarioId) {
        return adocaoService.listarPorUsuario(usuarioId);
    }

    @GetMapping("/pet/{petId}")
    public List<Adocao> listarPorPet(@PathVariable String petId) {
        return adocaoService.listarPorPet(petId);
    }

    @PatchMapping("/{id}/status")
    public Adocao atualizarStatus(
            @PathVariable String id,
            @RequestBody AtualizarStatusRequest dados) {
        return adocaoService.atualizarStatus(id, dados.status());
    }

    public record CriarAdocaoRequest(String usuarioId, String petId) {
    }

    public record AtualizarStatusRequest(String status) {
    }
}
