package br.com.ibmec.cloud.Clonespotify.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class PlanoRequest {

    private UUID id;

    @NotBlank(message = "campo nome é obrigatório ")
    private String nome;

    private double preco;

}
