package br.com.ibmec.cloud.Clonespotify.repositor;


import br.com.ibmec.cloud.Clonespotify.models.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {
}
