package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.enums.TipoSenha;
import com.example.demo.entity.enums.StatusSenha;
import com.example.demo.entity.Senha;

import java.util.List;

public interface SenhaRepository extends JpaRepository<Senha, Long> {

    // Fila: Ordena por quem chegou primeiro (FIFO)
    List<Senha> findByTipoAndStatusOrderByDataHoraCriacaoAsc(TipoSenha tipo, StatusSenha status);

    // Painel: Traz tudo menos o que já foi finalizado
    List<Senha> findByStatusNot(StatusSenha status);

    // Lógica: Traz o último que foi chamado (baseado na hora do clique, não no ID)
    Senha findTopByStatusOrderByDataHoraChamadaDesc(StatusSenha status);
}