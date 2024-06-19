package br.com.ibmec.cloud.Clonespotify.repositor;

import br.com.ibmec.cloud.Clonespotify.models.Plano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlanoRepository extends JpaRepository<Plano, UUID> {

    Optional<Plano> findByNome(String planoFree);
}
