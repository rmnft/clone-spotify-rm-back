package br.com.ibmec.cloud.Clonespotify.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PlaylistRequest {

    @NotBlank(message = "Nome da lista é obrigatório")
    private String nome;

}
