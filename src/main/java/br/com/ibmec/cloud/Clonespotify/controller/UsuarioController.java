package br.com.ibmec.cloud.Clonespotify.controller;


import br.com.ibmec.cloud.Clonespotify.controller.request.LoginRequest;
import br.com.ibmec.cloud.Clonespotify.controller.request.PlaylistRequest;
import br.com.ibmec.cloud.Clonespotify.models.*;
import br.com.ibmec.cloud.Clonespotify.repositor.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/usuario")

public class UsuarioController {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private JwtUtilController jwtUtil;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private MusicasRepository musicasRepository;

    @Autowired
    private AssinaturaRepository assinaturaRepository;

    @Autowired
    private PlanoRepository planoRepository;

    private final String DEFAULT_LISTA_DESEJO = "Músicas Favoritas";


    @PostMapping
    public ResponseEntity<Usuario> criar(@Valid @RequestBody Usuario usuario) {

        if (this.repository.findUsuarioByEmail(usuario.getEmail()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Verificar se o plano "planoFree" já existe
        Optional<Plano> planoExistente = planoRepository.findByNome("planoFree");

        if (planoExistente.isEmpty()) {
            // Cria e salva um novo plano "planoFree" e retorna sem criar o usuário
            Plano novoPlano = new Plano("planoFree", 0.0);
            planoRepository.save(novoPlano);

            // Opcional: Retornar uma resposta indicando que o plano foi criado
            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        Plano planoPersistido = planoExistente.get();

        //Criar a lista default do usuário
        Playlist lista = new Playlist();
        lista.setId(UUID.randomUUID());
        lista.setNome(this.DEFAULT_LISTA_DESEJO);
        lista.setUsuario(usuario);

        usuario.getListaDesejo().add(lista);


        //Cria Assinatura do Usuário
        Assinatura assinatura = new Assinatura();
        assinatura.setUsuario(usuario);
        assinatura.setAtivo(true);
        assinatura.setPlano(planoPersistido);

        usuario.getAssinatura().add(assinatura);

        this.repository.save(usuario);
        this.playlistRepository.save(lista);
        this.assinaturaRepository.save(assinatura);

        return new ResponseEntity<>(usuario, HttpStatus.CREATED);
    }

    @GetMapping("{id}/assinatura")
    public ResponseEntity<List<Assinatura>> obterAssinaturas(@PathVariable("id") UUID id) {
        return this.repository.findById(id).map(usuario -> {
            return new ResponseEntity<>(usuario.getAssinatura(), HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("{id}")
    public ResponseEntity<Usuario> obter(@PathVariable("id") UUID id) {
        return this.repository.findById(id).map(usuario -> {
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> obterTodosUsuarios(){
        List<Usuario> usuarios = this.repository.findAll();
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Optional<Usuario> optUsuario = repository.findUsuarioByEmailAndSenha(request.getEmail(), request.getSenha());
        return optUsuario.map(usuario -> {
            String token = jwtUtil.generateToken(usuario.getEmail());
            return ResponseEntity.ok(new AuthResponse(token));
        }).orElse(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }


    @PostMapping("{id}/favoritar/{idMusicas}")
    public ResponseEntity<Object> favoritar(@PathVariable("id") UUID id, @PathVariable("idMusicas") UUID idMusicas) {
        //Faço as buscas do usuário e carro
        Optional<Usuario> optUsuario = this.repository.findById(id);

        Optional<Musicas> optMusicas = this.musicasRepository.findById(idMusicas);

        //Caso não ache o usuário, retornar um 404
        if (optUsuario.isEmpty()) {
            return  new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        //Caso não ache o carro a ser associado retornar um 404
        if (optMusicas.isEmpty()) {
            return  new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        Usuario usuario = optUsuario.get();
        Musicas musicas = optMusicas.get();

        usuario.getListaDesejo().get(0).getMusicas().add(musicas);
        playlistRepository.save(usuario.getListaDesejo().get(0));

        return new ResponseEntity(usuario, HttpStatus.OK);
    }

    @PostMapping("{id}/desfavoritar/{idMusicas}")
    public ResponseEntity desfavoritar(@PathVariable("id") UUID id, @PathVariable("idMusicas") UUID idMusicas) {

        //Faço as buscas do usuário e carro
        Optional<Usuario> optUsuario = this.repository.findById(id);

        Optional<Musicas> optMusicas = this.musicasRepository.findById(idMusicas);

        //Caso não ache o usuário, retornar um 404
        if (optUsuario.isEmpty()) {
            return  new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        //Caso não ache o carro a ser associado retornar um 404
        if (optMusicas.isEmpty()) {
            return  new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        Usuario usuario = optUsuario.get();
        Musicas musicas = optMusicas.get();

        for(Musicas item : usuario.getListaDesejo().get(0).getMusicas()) {
            if (item.getId() == musicas.getId()) {
                usuario.getListaDesejo().get(0).getMusicas().remove(musicas);
                break;
            }
        }
        playlistRepository.save(usuario.getListaDesejo().get(0));
        return new ResponseEntity(usuario, HttpStatus.OK);
    }



    @PostMapping("{id}/criar-lista")
    public ResponseEntity<Usuario> criarLista(@PathVariable("id") UUID id, @Valid @RequestBody PlaylistRequest request) {
        Optional<Usuario> optUsuario = this.repository.findById(id);

        //Caso não ache o usuário, retornar um 404
        if (optUsuario.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Usuario usuario = optUsuario.get();

        Playlist playlist = new Playlist();
        playlist.setUsuario(usuario);
        playlist.setNome(request.getNome());

        playlistRepository.save(playlist);

        return new ResponseEntity<>(usuario, HttpStatus.OK);

    }


    @DeleteMapping("{Id}/remover-lista/{playlistId}")
    public ResponseEntity removerLista(@PathVariable("Id") UUID Id, @PathVariable("playlistId") UUID playlistId) {
        Optional<Usuario> optUsuario = repository.findById(Id);

        // Verifica se o usuário existe
        if (optUsuario.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Usuario usuario = optUsuario.get();

        Optional<Playlist> optPlaylist = playlistRepository.findById(playlistId);

        // Se a playlist não for encontrada
        if (optPlaylist.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Playlist playlistParaRemover = optPlaylist.get();

        // Remove a playlist do repositório
        playlistRepository.delete(playlistParaRemover);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping("/playlists")
    public ResponseEntity<List<Playlist>> obterTodasPLaylists(){
        List<Playlist> playlists = this.playlistRepository.findAll();
        return ResponseEntity.ok(playlists);
    }


    @PostMapping("{id}/lista/{idLista}/adicionar/{idMusica}")
    public ResponseEntity adicionarMusicaLista(@PathVariable("id") UUID id, @PathVariable("idLista") UUID idLista, @PathVariable("idMusica") UUID idMusica) {
        //Faço as buscas do usuário, musica e lista
        Optional<Usuario> optUsuario = this.repository.findById(id);
        Optional<Musicas> optMusicas = this.musicasRepository.findById(idMusica);
        Optional<Playlist> optPlaylist = this.playlistRepository.findById(idLista);

        //Caso não ache o usuário, retornar um 404
        if (optUsuario.isEmpty()) {
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        //Caso não ache o a lista a ser associada o carro retornar um 404
        if (optPlaylist.isEmpty()) {
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        //Caso não ache o carro a ser associado retornar um 404
        if (optMusicas.isEmpty()) {
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Playlist lista = optPlaylist.get();
        Musicas musicas = optMusicas.get();

        //Adiciona na lista
        lista.getMusicas().add(musicas);

        //Salva no banco de dados
        playlistRepository.save(lista);

        return new ResponseEntity(optUsuario.get(), HttpStatus.OK);
    }

    @DeleteMapping("{id}/lista/{idLista}/remover/{idMusicas}")
    public ResponseEntity removerMusicasLista(@PathVariable("id") UUID id, @PathVariable("idLista") UUID idLista, @PathVariable("idMusicas") UUID idMusicas) {
        //Faço as buscas do usuário, musicas e lista
        Optional<Usuario> optUsuario = this.repository.findById(id);
        Optional<Musicas> optMusicas = this.musicasRepository.findById(idMusicas);
        Optional<Playlist> optPlaylist = this.playlistRepository.findById(idLista);

        //Caso não ache o usuário, retornar um 404
        if (optUsuario.isEmpty()) {
            return  new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        //Caso não ache o a lista a ser associada o carro retornar um 404
        if (optPlaylist.isEmpty()) {
            return  new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        //Caso não ache o carro a ser associado retornar um 404
        if (optMusicas.isEmpty()) {
            return  new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        Playlist lista = optPlaylist.get();
        Musicas musicas = optMusicas.get();

        //Adiciona na lista
        for(Musicas item : lista.getMusicas()) {
            if (item.getId() == musicas.getId()) {
                lista.getMusicas().remove(musicas);
                break;
            }
        }

        //Salva no banco de dados
        playlistRepository.save(lista);

        return new ResponseEntity(optUsuario.get(), HttpStatus.OK);
    }

    @PostMapping("{id}/TrocarAssinatura/{idAssinatura}/{novoPlanoId}")
    public ResponseEntity<Usuario> trocarAssinatura(@PathVariable("id") UUID id, @PathVariable("idAssinatura") UUID idAssinatura, @PathVariable("novoPlanoId") UUID novoPlanoId) {

        // Buscar usuário e assinatura pelos IDs fornecidos
        Optional<Usuario> optUsuario = this.repository.findById(id);
        Optional<Assinatura> optAssinatura = this.assinaturaRepository.findById(idAssinatura);

        // Verificar se ambos foram encontrados
        if (optUsuario.isEmpty() || optAssinatura.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Buscar novo plano
        Optional<Plano> optNovoPlano = this.planoRepository.findById(novoPlanoId);
        if (optNovoPlano.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Desativar a assinatura antiga e ativar a nova
        Assinatura assinaturaAtual = optAssinatura.get();
        assinaturaAtual.setAtivo(false);
        this.assinaturaRepository.save(assinaturaAtual);

        // Criar nova assinatura com novo plano
        Assinatura novaAssinatura = new Assinatura();
        novaAssinatura.setUsuario(optUsuario.get());
        novaAssinatura.setPlano(optNovoPlano.get());
        novaAssinatura.setAtivo(true);

        this.assinaturaRepository.save(novaAssinatura);

        // Retornar o usuário atualizado
        return new ResponseEntity<>(optUsuario.get(), HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<Usuario> update(@PathVariable("id") UUID id, @RequestBody @Valid Usuario usuario) {
        Optional<Usuario> optUser = this.repository.findById(id);
        if (optUser.isPresent()) {
            Usuario update = optUser.get();
            update.setNome(usuario.getNome());
            update.setEmail(usuario.getEmail());
            update.setSenha(usuario.getSenha());

            this.repository.save(update);

        }

        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }


    @GetMapping("/me")
    public ResponseEntity<Usuario> obterUsuarioPorToken(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", ""); // Remove o prefixo "Bearer " do token
        String email = jwtUtil.getEmailFromToken(token); // Extrai o email do token

        Optional<Usuario> usuarioOpt = repository.findUsuarioByEmail(email);
        if (usuarioOpt.isPresent()) {
            return new ResponseEntity<>(usuarioOpt.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
