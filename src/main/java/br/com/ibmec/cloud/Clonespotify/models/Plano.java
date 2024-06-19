package br.com.ibmec.cloud.Clonespotify.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class Plano {

    public Plano() {};
    public Plano(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    @NotBlank(message = "campo nome é obrigatório ")
    private String nome;

    @Column
    private double preco;



}
