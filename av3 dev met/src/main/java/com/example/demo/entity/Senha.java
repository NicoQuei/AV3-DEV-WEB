package com.example.demo.entity;

import com.example.demo.entity.enums.StatusSenha;
import com.example.demo.entity.enums.TipoSenha;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
public class Senha {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;

    @Enumerated(EnumType.STRING)
    private TipoSenha tipo;

    public void setTipo(TipoSenha tipo) {
        this.tipo = tipo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public StatusSenha getStatus() {
        return status;
    }

    public LocalDateTime getDataHoraCriacao() {
        return dataHoraCriacao;
    }

    public LocalDateTime getDataHoraChamada() {
        return dataHoraChamada;
    }

    public TipoSenha getTipo() {
        return tipo;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setDataHoraCriacao(LocalDateTime dataHoraCriacao) {
        this.dataHoraCriacao = dataHoraCriacao;
    }

    public void setDataHoraChamada(LocalDateTime dataHoraChamada) {
        this.dataHoraChamada = dataHoraChamada;
    }

    public void setStatus(StatusSenha status) {
        this.status = status;
    }

    @Enumerated(EnumType.STRING)
    private StatusSenha status;

    private LocalDateTime dataHoraCriacao;

    private LocalDateTime dataHoraChamada;
}