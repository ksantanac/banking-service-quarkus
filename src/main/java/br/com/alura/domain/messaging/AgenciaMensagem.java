package br.com.alura.domain.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class AgenciaMensagem {

    private final Integer id;
    private final String nome;
    private final String razaoSocial;
    private final String cnpj;
    private final String situacaoCadastral;

}
