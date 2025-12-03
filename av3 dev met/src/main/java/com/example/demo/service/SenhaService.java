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

        // salva para gerar ID
        novaSenha = repository.save(novaSenha);

        String prefixo = (tipo == TipoSenha.PREFERENCIAL) ? "P" : "N";
        novaSenha.setNumero(prefixo + novaSenha.getId());

        return repository.save(novaSenha);
    }

    @Transactional
    public Senha chamarProximo() {

        // Última senha chamada (opcional, pode ser null)
        Senha ultimaChamada = repository.findTopByStatusOrderByIdDesc(StatusSenha.CHAMADO);
        TipoSenha ultimoTipo = (ultimaChamada != null) ? ultimaChamada.getTipo() : null;

        // Filas
        List<Senha> preferenciais = repository
                .findByTipoAndStatusOrderByDataHoraCriacaoAsc(TipoSenha.PREFERENCIAL, StatusSenha.AGUARDANDO);

        List<Senha> normais = repository
                .findByTipoAndStatusOrderByDataHoraCriacaoAsc(TipoSenha.NORMAL, StatusSenha.AGUARDANDO);

        // Se as duas estão vazias → não há o que chamar
        if (preferenciais.isEmpty() && normais.isEmpty()) {
            return null;
        }

        // Lógica de intercalação

        // Se o último foi NORMAL, então tenta chamar PREFERENCIAL
        if (ultimoTipo == TipoSenha.NORMAL && !preferenciais.isEmpty()) {
            return atualizarParaChamado(preferenciais.get(0));
        }

        // Se o último foi PREFERENCIAL, então tenta chamar NORMAL
        if (ultimoTipo == TipoSenha.PREFERENCIAL && !normais.isEmpty()) {
            return atualizarParaChamado(normais.get(0));
        }

        // Primeira chamada do sistema (nenhum chamado ainda)
        if (ultimoTipo == null) {
            if (!normais.isEmpty()) {
                return atualizarParaChamado(normais.get(0));
            } else {
                return atualizarParaChamado(preferenciais.get(0));
            }
        }

        // Se deveria chamar preferencial mas acabou → chama normal
        if (!normais.isEmpty()) {
            return atualizarParaChamado(normais.get(0));
        }

        // Se deveria chamar normal mas acabou → chama preferencial
        return atualizarParaChamado(preferenciais.get(0));
    }

    private Senha atualizarParaChamado(Senha senha) {
        senha.setStatus(StatusSenha.CHAMADO);
        return repository.save(senha);
    }

    public List<Senha> listarFila() {
        return repository.findByStatusNot(StatusSenha.FINALIZADO);
    }

    public Senha obterSenhaAtual() {
        return repository.findTopByStatusOrderByIdDesc(StatusSenha.CHAMADO);
    }
}
