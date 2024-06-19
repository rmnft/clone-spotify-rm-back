package br.com.ibmec.cloud.Clonespotify.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class BandaRequest {

    public BandaRequest() {this.musicas = new ArrayList<>();}

    private UUID id;

    @NotBlank(message = "Campo nome é obrigatório")
    private String nome;

    @NotBlank(message = "Campo descrição e obrigatório")
    private String descricao;

    private String imagemBase64;

    private List<MusicasRequest> musicas;
}
