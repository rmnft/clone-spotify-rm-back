package br.com.ibmec.cloud.Clonespotify.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class Assinatura {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JsonIgnore
    private Usuario usuario;

    @ManyToOne
    private Plano plano;

    @Column
    private boolean ativo;
}
