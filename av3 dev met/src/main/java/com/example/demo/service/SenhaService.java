package com.example.demo.service;

import com.example.demo.entity.enums.TipoSenha;
import com.example.demo.entity.enums.StatusSenha;
import com.example.demo.entity.Senha;
import com.example.demo.repository.SenhaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SenhaService {

    @Autowired
    private SenhaRepository repository;

    // 1. O paciente clica no Totem
    public Senha gerarSenha(TipoSenha tipo) {
        Senha novaSenha = new Senha();
        novaSenha.setTipo(tipo);
        novaSenha.setStatus(StatusSenha.AGUARDANDO);
        novaSenha.setDataHoraCriacao(LocalDateTime.now());

        // Salvamos primeiro para gerar o ID
        novaSenha = repository.save(novaSenha);

        // Geramos o número visual (Ex: "N" + ID 10 = "N10")
        String prefixo = (tipo == TipoSenha.PREFERENCIAL) ? "P" : "N";
        novaSenha.setNumero(prefixo + novaSenha.getId());

        return repository.save(novaSenha);
    }

    // 2. O Médico chama o próximo (Regra de Ouro)
    public Senha chamarProximo() {
        // Tenta achar PREFERENCIAIS aguardando
        List<Senha> preferenciais = repository.findByTipoAndStatusOrderByDataHoraCriacaoAsc(
                TipoSenha.PREFERENCIAL, StatusSenha.AGUARDANDO);

        if (!preferenciais.isEmpty()) {
            return atualizarParaChamado(preferenciais.get(0));
        }

        // Se não tem preferencial, busca NORMAIS
        List<Senha> normais = repository.findByTipoAndStatusOrderByDataHoraCriacaoAsc(
                TipoSenha.NORMAL, StatusSenha.AGUARDANDO);

        if (!normais.isEmpty()) {
            return atualizarParaChamado(normais.get(0));
        }

        return null; // Fila vazia
    }

    // Método auxiliar para não repetir código
    private Senha atualizarParaChamado(Senha senha) {
        senha.setStatus(StatusSenha.CHAMADO);
        return repository.save(senha);
    }

    // 3. Para mostrar na TV
    public List<Senha> listarFila() {
        return repository.findByStatusNot(StatusSenha.FINALIZADO);
    }

    // 4. Pega a senha atual para piscar na tela
    public Senha obterSenhaAtual() {
        return repository.findFirstByStatusOrderByIdDesc(StatusSenha.CHAMADO);
    }
}