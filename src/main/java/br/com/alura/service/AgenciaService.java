package br.com.alura.service;

import br.com.alura.domain.Agencia;
import br.com.alura.domain.http.AgenciaHttp;
import br.com.alura.domain.http.SituacaoCadastral;
import br.com.alura.exceptions.AgenciaNaoAtivaOuNaoEncontradaException;
import br.com.alura.repository.AgenciaRepository;
import br.com.alura.service.http.SituacaoCadastralHttpService;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.logging.Log;
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

    public void cadastrar(Agencia agencia) {
        AgenciaHttp agenciaHttp = situacaoCadastralHttpService.buscarPorCnpj(agencia.getCnpj());
        if (agenciaHttp != null && agenciaHttp.getSituacaoCadastral().equals(SituacaoCadastral.ATIVO)) {
            Log.info("A agência com CNPJ " + agencia.getCnpj() + " foi cadastrada!");
            meterRegistry.counter("agencia_adicionada_counter").increment();
            agenciaRepository.persist(agencia);
        } else {
            Log.error("A agência com CNPJ " + agencia.getCnpj() + " não está ativa ou não foi cadastrada.");
            meterRegistry.counter("agencia_nao_adicionada_counter").increment();
            throw new AgenciaNaoAtivaOuNaoEncontradaException();
        }
    }

    public Agencia buscarPorId(Long id) {
        return agenciaRepository.findById(id);
    }

    public void deletar(Long id){
        Log.info("A agência com ID " + id + " foi deletada!");
        agenciaRepository.deleteById(id);
    }

    public Agencia alterar(Agencia agencia){
        Log.info("A agência com CNPJ " + agencia.getCnpj() + " foi alterada!");

        agenciaRepository.update(
                "nome = ?1, razaoSocial = ?2, cnpj = ?3 where id = ?4",
                agencia.getNome(),
                agencia.getRazaoSocial(),
                agencia.getCnpj(),
                agencia.getId()
        );

        return agencia;
    }


//    public void alterar(Agencia agencia) {
//        // Busca a entidade pelo ID
//        Agencia entidadeExistente = agenciaRepository.findById(agencia.getId());
//
//        if (entidadeExistente != null) {
//            // Atualiza os atributos desejados
//            entidadeExistente.setNome(agencia.getNome());
//            entidadeExistente.setRazaoSocial(agencia.getRazaoSocial());
//            entidadeExistente.setCnpj(agencia.getCnpj());
//        } else {
//            throw new IllegalStateException("Agência com ID " + agencia.getId() + " não encontrada");
//        }
//    }

}