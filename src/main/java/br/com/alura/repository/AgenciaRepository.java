package br.com.alura.repository;

import br.com.alura.domain.Agencia;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AgenciaRepository implements PanacheRepository<Agencia> {

    @WithTransaction
    public Uni<Agencia> findByCnpj(String cnpj) {
        return find("cnpj", cnpj).firstResult();
    }

}
