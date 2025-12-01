package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.enums.TipoSenha;
import com.example.demo.entity.enums.StatusSenha;
import com.example.demo.entity.Senha;

import java.util.List;

public interface SenhaRepository extends JpaRepository<Senha, Long> {

    List<Senha> findByTipoAndStatusOrderByDataHoraCriacaoAsc(TipoSenha tipo, StatusSenha status);

    List<Senha> findByStatusNot(StatusSenha status);

    // Busca apenas UM registro (o último que foi chamado)
    Senha findFirstByStatusOrderByIdDesc(StatusSenha status);
}
