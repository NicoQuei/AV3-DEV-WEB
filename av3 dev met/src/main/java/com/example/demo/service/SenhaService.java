package com.example.demo.service;

import com.example.demo.entity.enums.TipoSenha;
import com.example.demo.entity.enums.StatusSenha;
import com.example.demo.entity.Senha;
import com.example.demo.repository.SenhaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SenhaService {

    private final SenhaRepository repository;

    public SenhaService(SenhaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Senha gerarSenha(TipoSenha tipo) {
        Senha novaSenha = new Senha();
        novaSenha.setTipo(tipo);
        novaSenha.setStatus(StatusSenha.AGUARDANDO);
        novaSenha.setDataHoraCriacao(LocalDateTime.now());

        // Salva para gerar o ID
        novaSenha = repository.save(novaSenha);

        // Monta o número (ex: N1, P2)
        String prefixo = (tipo == TipoSenha.PREFERENCIAL) ? "P" : "N";
        novaSenha.setNumero(prefixo + novaSenha.getId());

        return repository.save(novaSenha);
    }

    @Transactional
    public Senha chamarProximo() {
        // 1. Descobrir quem foi o último REALMENTE chamado (pela hora)
        Senha ultimaChamada = repository.findTopByStatusOrderByDataHoraChamadaDesc(StatusSenha.CHAMADO);
        TipoSenha ultimoTipo = (ultimaChamada != null) ? ultimaChamada.getTipo() : null;

        // 2. Buscar as filas
        List<Senha> preferenciais = repository
                .findByTipoAndStatusOrderByDataHoraCriacaoAsc(TipoSenha.PREFERENCIAL, StatusSenha.AGUARDANDO);

        List<Senha> normais = repository
                .findByTipoAndStatusOrderByDataHoraCriacaoAsc(TipoSenha.NORMAL, StatusSenha.AGUARDANDO);

        // Se ninguém está esperando
        if (preferenciais.isEmpty() && normais.isEmpty()) {
            return null;
        }

        // --- Lógica de Intercalação ---

        // Se o último foi NORMAL, vez do PREFERENCIAL
        if (ultimoTipo == TipoSenha.NORMAL && !preferenciais.isEmpty()) {
            return atualizarParaChamado(preferenciais.get(0));
        }

        // Se o último foi PREFERENCIAL, vez do NORMAL
        if (ultimoTipo == TipoSenha.PREFERENCIAL && !normais.isEmpty()) {
            return atualizarParaChamado(normais.get(0));
        }

        // Primeira chamada do dia (ultimoTipo é null)
        if (ultimoTipo == null) {
            // Prioridade para Normal se preferencial estiver vazio, senão Preferencial
            if (preferenciais.isEmpty()) {
                return atualizarParaChamado(normais.get(0));
            } else {
                return atualizarParaChamado(preferenciais.get(0));
            }
        }

        // Casos de falha (ex: era vez do preferencial, mas acabou a fila dele)
        if (!normais.isEmpty()) {
            return atualizarParaChamado(normais.get(0));
        }

        return atualizarParaChamado(preferenciais.get(0));
    }

    private Senha atualizarParaChamado(Senha senha) {
        senha.setStatus(StatusSenha.CHAMADO);
        senha.setDataHoraChamada(LocalDateTime.now()); // Importante para a ordem
        return repository.save(senha);
    }

    public List<Senha> listarFila() {
        return repository.findByStatusNot(StatusSenha.FINALIZADO);
    }

    public List<Senha> listarTodas() {
        return repository.findAll();
    }

    public Senha obterSenhaAtual() {
        // Usa a mesma lógica de data para ser consistente
        return repository.findTopByStatusOrderByDataHoraChamadaDesc(StatusSenha.CHAMADO);
    }

    @Transactional
    public void finalizarAtendimento(Long id) {
        Senha senha = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Senha não encontrada"));

        senha.setStatus(StatusSenha.FINALIZADO);
        repository.save(senha);
    }
}