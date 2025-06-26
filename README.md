
# 📧 Notificador de Inadimplência

Este projeto Java é uma ação agendada para a plataforma Sankhya, responsável por enviar e-mails mensais com relatórios de inadimplência aos vendedores, contendo apenas os dados dos seus respectivos clientes.

## 🚀 Funcionalidades

- Envio de relatórios de inadimplência todo dia 5 de cada mês.
- Agrupamento de títulos por vendedor.
- E-mails formatados em HTML, com layout responsivo e elegante.
- Integração com o módulo `MSDFilaMensagem` da Sankhya para envio de e-mails.

## 🛠️ Tecnologias e Dependências

- Java 8
- Maven
- Sankhya JAPE API
- Apache POI (opcional)
- Jackson (opcional)
- Log4j

## 📂 Estrutura do Projeto

```
src/
└── main/
    └── java/
        └── br/
            └── inad/
                ├── AcAgNotiInadimplencia.java       # Classe principal da ação agendada
                ├── model/
                │   └── DadosInadimplencia.java      # POJO com os dados do relatório
                └── util/
                    ├── HtmlInadimplenciaBuilder.java  # Geração do HTML do e-mail
                    └── WebhookService.java           # Envio de mensagens para Webhook (log)
```

Desenvolvido com atenção à arquitetura modular e boas práticas de Java e integração Sankhya.
