package com.example.demo.controller;

import com.example.demo.entity.Senha;
import com.example.demo.entity.enums.TipoSenha;
import com.example.demo.service.SenhaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/senhas")
@CrossOrigin(origins = "*")
public class SenhaController {

    @Autowired
    private SenhaService service;

    // Rota para o Totem: POST /senhas/criar/NORMAL
    @PostMapping("/criar/{tipo}")
    public Senha criarSenha(@PathVariable TipoSenha tipo) {
        return service.gerarSenha(tipo);
    }

    // Rota para o Médico: POST /senhas/chamar
    @PostMapping("/chamar")
    public Senha chamarProxima() {
        return service.chamarProximo();
    }

    // Rota para a TV: GET /senhas
    @GetMapping
    public List<Senha> listarFila() {
        return service.listarFila();
    }

    // Rota para a TV pegar só a última senha: GET /senhas/atual
    @GetMapping("/atual")
    public Senha pegarSenhaAtual() {
        return service.obterSenhaAtual();
    }
}