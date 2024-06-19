package br.com.ibmec.cloud.Clonespotify.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Campo email é obrigatório")
    private String email;

    @NotBlank(message = "Campo senha é obrigatório")
    private String senha;

}
