package br.com.ibmec.cloud.Clonespotify.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class Usuario {

    public Usuario() {this.listaDesejo = new ArrayList<>(); this.assinatura = new ArrayList<>();}

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    @NotBlank (message = "campo nome é obrigatório ")
    private String nome;


    @Column
    @NotBlank (message = "campo email é obrigatório ")
    @Email (message = "campo não está no formato correto")
    private String email;

    @Column
    @NotBlank (message = "campo senha é obrigatório ")
    private String senha;

    @OneToMany
    @JoinColumn (name = "usuario_id", referencedColumnName = "id")
    private List<Playlist> listaDesejo;

    @OneToMany
    @JoinColumn (name = "usuario_id", referencedColumnName = "id")
    private List<Assinatura> assinatura;


}
