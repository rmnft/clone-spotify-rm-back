package br.com.ibmec.cloud.Clonespotify.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Entity
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)

    @Column
    private UUID id;

    @Column
    private String nome;

    @ManyToOne
    @JsonIgnore
    private Usuario usuario;

    @OneToMany
    private List<Musicas> musicas;

}
