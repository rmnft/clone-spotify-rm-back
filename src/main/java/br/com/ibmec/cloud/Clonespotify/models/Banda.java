package br.com.ibmec.cloud.Clonespotify.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class Banda {

    public Banda() {this.musicas = new ArrayList<>();}

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    @NotBlank(message = "campo nome é obrigatório ")
    private String nome;

    @Column
    @NotBlank (message = "campo descrição é obrigatório ")
    private String descricao;

    @Column
    private String imagem;;

    @OneToMany
    @JoinColumn(name = "banda_id", referencedColumnName = "id")
    private List<Musicas> musicas;

}
