package br.com.alura.service.messaging;

import br.com.alura.domain.messaging.AgenciaMensagem;
import br.com.alura.repository.AgenciaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class RemoverAgenciaConsumer {

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private AgenciaRepository agenciaRepository;

    @WithTransaction
    @Incoming("banking-service-channel")
    public Uni<Void> consumirMensagem(String mensagem) {
        return Uni.createFrom().item(() -> {
            try {
                Log.info(mensagem);
                return objectMapper.readValue(mensagem, AgenciaMensagem.class);
            } catch (JsonProcessingException ex) {
                Log.error(ex.getMessage());
                throw new RuntimeException();
            }
        }).onItem().transformToUni(agenciaMensagem ->
                    agenciaRepository.findByCnpj(agenciaMensagem.getCnpj()))
            .onItem().ifNotNull().transformToUni(agencia ->
                    agenciaRepository.deleteById(agencia.getId()))
            .replaceWithVoid();
    }

}
