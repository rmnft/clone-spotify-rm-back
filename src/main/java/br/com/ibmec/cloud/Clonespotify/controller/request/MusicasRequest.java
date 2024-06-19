package br.com.ibmec.cloud.Clonespotify.controller.request;

import lombok.Data;

import java.util.UUID;

@Data
public class MusicasRequest {
    private UUID id;
    private String nome;
    private float duracao;
}
