package br.com.ibmec.cloud.Clonespotify.controller;

import br.com.ibmec.cloud.Clonespotify.controller.request.PlanoRequest;
import br.com.ibmec.cloud.Clonespotify.models.*;
import br.com.ibmec.cloud.Clonespotify.repositor.PlanoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/plano")
public class PlanoController {

    @Autowired
    private PlanoRepository repository;

    @PostMapping
    public ResponseEntity<Plano> criar(@Valid @RequestBody PlanoRequest request) {

        Plano plano = new Plano();
        plano.setNome(request.getNome());
        plano.setPreco(request.getPreco());

        this.repository.save(plano);

        return new ResponseEntity<>(plano, HttpStatus.CREATED);

    }

    @GetMapping("{id}")
    public ResponseEntity<Plano> obter(@PathVariable("id") UUID id) {
        return this.repository.findById(id).map(item -> {
            return new ResponseEntity<>(item, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<Plano>> obterTodosPlanos(){
        List<Plano> planos = this.repository.findAll();
        return ResponseEntity.ok(planos);
    }

    @PutMapping("{id}")
    public ResponseEntity<Plano> atualizarPlano(@PathVariable("id") UUID id,@Valid @RequestBody PlanoRequest request) {
        Optional<Plano> optPlano = this.repository.findById(id);

        if (optPlano.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Plano plano = optPlano.get();

        plano.setNome(request.getNome());
        plano.setPreco(request.getPreco());

        this.repository.save(plano);

        return new ResponseEntity<>(plano, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Plano> excluirPlano(@PathVariable("id") UUID id) {
        Optional<Plano> optPlano = this.repository.findById(id);

        if (optPlano.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Exclui o plano
        this.repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}
