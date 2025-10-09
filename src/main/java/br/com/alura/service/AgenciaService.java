package br.com.alura.service;

import br.com.alura.domain.Agencia;
import br.com.alura.domain.http.AgenciaHttp;
import br.com.alura.domain.http.SituacaoCadastral;
import br.com.alura.exceptions.AgenciaNaoAtivaOuNaoEncontradaException;
import br.com.alura.repository.AgenciaRepository;
import br.com.alura.service.http.SituacaoCadastralHttpService;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@ApplicationScoped
public class AgenciaService {

    @RestClient
    private SituacaoCadastralHttpService situacaoCadastralHttpService;

    @Inject
    private AgenciaRepository agenciaRepository;

    @Inject
    private MeterRegistry meterRegistry;

    @WithTransaction
    public Uni<Void> cadastrar(Agencia agencia) {

        Uni<AgenciaHttp> agenciaHttp = situacaoCadastralHttpService.buscarPorCnpj(agencia.getCnpj());
        return agenciaHttp
                .onItem().ifNull().failWith(new AgenciaNaoAtivaOuNaoEncontradaException())
                .onItem().transformToUni(item -> persistirSeAtiva(agencia, item));
    }

    private Uni<Void> persistirSeAtiva(Agencia agencia, AgenciaHttp agenciaHttp) {
        if (agenciaHttp.getSituacaoCadastral().equals(SituacaoCadastral.ATIVO)) {
            Log.info("A agência com CNPJ " + agencia.getCnpj() + " foi cadastrada!");
            meterRegistry.counter("agencia_adicionada_counter").increment();
            return agenciaRepository.persist(agencia).replaceWithVoid();
        } else {
            Log.error("A agência com CNPJ " + agencia.getCnpj() + " não está ativa ou não foi cadastrada.");
            meterRegistry.counter("agencia_nao_adicionada_counter").increment();
            return Uni.createFrom().failure(new AgenciaNaoAtivaOuNaoEncontradaException());
        }
    }

    @WithSession
    public Uni<Agencia> buscarPorId(Long id) {return agenciaRepository.findById(id);}

    @WithSession
    public Uni<Void> deletar(Long id){
        Log.info("A agência com ID " + id + " foi deletada!");
        return agenciaRepository.deleteById(id).replaceWithVoid();
    }

    @WithTransaction
    public Uni<Agencia> alterar(Agencia agencia){

        return agenciaRepository.update(
                "nome = ?1, razaoSocial = ?2, cnpj = ?3 where id = ?4",
                agencia.getNome(),
                agencia.getRazaoSocial(),
                agencia.getCnpj(),
                agencia.getId()
            )
                .onItem().transform(rows -> {
                                Log.info("A agência com CNPJ " + agencia.getCnpj() + " foi alterada!");
                                return agencia;
                            });
    }

}