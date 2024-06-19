package br.com.ibmec.cloud.Clonespotify.controller;

import br.com.ibmec.cloud.Clonespotify.controller.request.BandaRequest;
import br.com.ibmec.cloud.Clonespotify.controller.request.MusicasRequest;
import br.com.ibmec.cloud.Clonespotify.models.Banda;
import br.com.ibmec.cloud.Clonespotify.models.Musicas;
import br.com.ibmec.cloud.Clonespotify.repositor.BandaRepository;
import br.com.ibmec.cloud.Clonespotify.repositor.MusicasRepository;
import br.com.ibmec.cloud.Clonespotify.services.AzureSearchIndex;
import br.com.ibmec.cloud.Clonespotify.services.AzureSearchService;
import br.com.ibmec.cloud.Clonespotify.services.AzureStorageAccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/banda")
public class BandaController {

    @Autowired
    private BandaRepository repository;

    @Autowired
    private MusicasRepository musicasRepository;

//    @Autowired
//    private AzureSearchService searchService;
//
//    @Autowired
//    private AzureStorageAccountService accountService;

    @PostMapping
    public ResponseEntity<Banda> criar(@Valid @RequestBody BandaRequest request) throws IOException {

        Banda banda = new Banda();
        banda.setNome(request.getNome());
        banda.setDescricao(request.getDescricao());
        banda.setImagemBase64(request.getImagemBase64());

//        String imageUrl = this.accountService.uploadFileToAzure(request.getImagemBase64());
//        banda.setImagemBase64(imageUrl);

        //Salva no banco de dados da banda
        this.repository.save(banda);

        //Verificar se o usuário enviou musicas
        for (MusicasRequest item : request.getMusicas()) {
            Musicas musicas = new Musicas();
            musicas.setId(UUID.randomUUID());
            musicas.setNome(item.getNome());
            musicas.setDuracao(item.getDuracao());
            musicas.setBanda(banda);

            //Associa a banda a musica
            banda.getMusicas().add(musicas);

            //Salva no banco de dados
            this.musicasRepository.save(musicas);
        }


        return new ResponseEntity<>(banda, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/adicionarMusicas")
    public ResponseEntity<Banda> adicionarMusicas(@PathVariable("id") UUID id, @Valid @RequestBody MusicasRequest musicasRequest) {
        Optional<Banda> optBanda = this.repository.findById(id);

        if (optBanda.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Banda banda = optBanda.get();

        //Cria uma nova instancia de musica para salvar
        Musicas musicas = new Musicas();
        musicas.setNome(musicasRequest.getNome());
        musicas.setDuracao(musicasRequest.getDuracao());
        musicas.setBanda(banda);
        banda.getMusicas().add(musicas);

        //Salva a musica e associa a banda
        this.musicasRepository.save(musicas);

        //Responde para o usuário
        return new ResponseEntity<>(banda, HttpStatus.CREATED);
    }


    @GetMapping("{id}")
    public ResponseEntity<Banda> obter(@PathVariable("id") UUID id) {
        return this.repository.findById(id).map(item -> {
            return new ResponseEntity<>(item, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<Banda>> obterTodos(){
        List<Banda> bandas = this.repository.findAll();
        return ResponseEntity.ok(bandas);
    }

    @GetMapping("{id}/musicas")
    public ResponseEntity<List<Musicas>> obterMusicas(@PathVariable("id") UUID id) {
        return this.repository.findById(id).map(item -> {
            return new ResponseEntity<>(item.getMusicas(), HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Banda> excluirBanda(@PathVariable("id") UUID id) {
        Optional<Banda> optBanda = this.repository.findById(id);

        if (optBanda.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Obtém a banda do Optional
        Banda banda = optBanda.get();

        // Exclui todas as músicas associadas à banda
        for (Musicas musica : banda.getMusicas()) {
            this.musicasRepository.delete(musica);
        }

        // Exclui a banda após todas as músicas serem excluídas
        this.repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Banda> atualizarBanda(@PathVariable("id") UUID id,@Valid @RequestBody BandaRequest request) {
        Optional<Banda> optBanda = this.repository.findById(id);

        if (optBanda.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Banda banda = optBanda.get();

        banda.setNome(request.getNome());
        banda.setDescricao(request.getDescricao());
        banda.setImagemBase64(request.getImagemBase64());

        this.repository.save(banda);

        return new ResponseEntity<>(banda, HttpStatus.OK);
    }

//    @GetMapping("/autocomplete/search")
//    public ResponseEntity<List<AzureSearchIndex>> autocomplete(@RequestParam("search") String search) {
//        return  new ResponseEntity<>(this.searchService.suggester(search), HttpStatus.OK);
//    }

}
