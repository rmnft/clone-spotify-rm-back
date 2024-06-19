package br.com.ibmec.cloud.Clonespotify.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class Musicas {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String nome;

    @Column
    private float duracao;

    @ManyToOne()
    @JsonIgnore
    private Banda banda;

    @ManyToOne()
    @JsonIgnore
    private Playlist playlist;
}
