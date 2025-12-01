package com.example.demo.entity;

import com.example.demo.entity.enums.StatusSenha;

import com.example.demo.entity.enums.TipoSenha;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data // O Lombok cria getters e setters aqui
@Entity
public class Senha {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero; // Ex: "N-10", "P-12"

    @Enumerated(EnumType.STRING)
    private TipoSenha tipo;

    @Enumerated(EnumType.STRING)
    private StatusSenha status;

    private LocalDateTime dataHoraCriacao;
}
