package com.example.demo.controller;

import com.example.demo.entity.Senha;
import com.example.demo.entity.enums.TipoSenha;
import com.example.demo.service.SenhaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/senhas")
@CrossOrigin(origins = "*")
public class SenhaController {

    private final SenhaService service;

    public SenhaController(SenhaService service) {
        this.service = service;
    }

    @PostMapping("/criar/{tipo}")
    public ResponseEntity<Senha> criarSenha(@PathVariable TipoSenha tipo) {
        return ResponseEntity.ok(service.gerarSenha(tipo));
    }

    @PostMapping("/chamar")
    public ResponseEntity<Senha> chamarProxima() {
        return ResponseEntity.ok(service.chamarProximo());
    }

    @GetMapping
    public ResponseEntity<List<Senha>> listarFila() {
        return ResponseEntity.ok(service.listarFila());
    }

    @GetMapping("/atual")
    public ResponseEntity<Senha> pegarSenhaAtual() {
        return ResponseEntity.ok(service.obterSenhaAtual());
    }
}
