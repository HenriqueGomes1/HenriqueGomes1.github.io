# RPG Master

Sistema web de apoio a mesas de RPG (regras D&D 5.5 / 2024), desenvolvido como
projeto de TCC. Possui dois modulos integrados na mesma interface, divididos
visualmente em duas metades da tela:

- **Lado do Jogador (verde):** criacao e consulta de fichas de personagem, com
  os seis atributos classicos (Forca, Destreza, Constituicao, Inteligencia,
  Sabedoria, Carisma), bonus calculados automaticamente e uma habilidade
  simplificada gerada por IA, ligada a um atributo especifico do personagem.
- **Lado do Mestre (roxo):** gerenciamento de campanhas, com geracao de NPCs e
  monstros sob demanda atraves de IA Generativa, a partir de um pedido em
  linguagem natural (ex: "um goblin batedor sorrateiro").

## Stack tecnica

| Camada               | Tecnologia                                   |
|-----------------------|-----------------------------------------------|
| Linguagem / build     | Java 17, Maven                                |
| Framework web         | Spring Boot 3.3, Spring MVC                   |
| Views                 | Thymeleaf                                     |
| Persistencia          | Spring Data JPA + Hibernate                   |
| Banco de dados         | H2 (em memoria)                               |
| IA Generativa          | Mistral AI (API REST, formato compativel OpenAI) |

## Como executar

### Pre-requisitos

- JDK 17 ou superior
- Maven 3.9+ (ou use o `mvnw` se estiver presente no seu ambiente)
- Conexao com a internet (para baixar dependencias do Maven Central na
  primeira execucao, e para a integracao com a API da Mistral)

### Passo a passo

1. **Configure a chave da API da Mistral** (opcional, mas recomendado para
   ver a geracao de conteudo por IA funcionando de verdade). Crie uma conta
   gratuita em [console.mistral.ai](https://console.mistral.ai), gere uma
   API key e exporte como variavel de ambiente:

   ```bash
   # Linux / macOS
   export MISTRAL_API_KEY=sua_chave_aqui

   # Windows (PowerShell)
   $env:MISTRAL_API_KEY="sua_chave_aqui"
   ```

   > **Sem a chave configurada, a aplicacao continua funcionando normalmente.**
   > O `MistralAiService` tem um modo de fallback: caso a chamada a API falhe
   > ou a chave nao esteja presente, ele gera uma descricao/habilidade ou NPC
   > "padrao" localmente, sem quebrar o fluxo de cadastro. Isso e proposital,
   > para que a demonstracao do TCC nunca trave por falta de internet ou
   > de credito na API.

2. **Compile e rode a aplicacao:**

   ```bash
   mvn spring-boot:run
   ```

3. **Acesse no navegador:**

   - Aplicacao: <http://localhost:8080>
   - Console do banco H2: <http://localhost:8080/h2-console>
     - JDBC URL: `jdbc:h2:mem:rpgmaster`
     - Usuario: `sa`
     - Senha: *(em branco)*

Ao subir, a aplicacao popula automaticamente uma campanha de exemplo
("A Maldicao de Vaelthorn") com 3 personagens e 3 NPCs, para que as telas
nunca comecem vazias durante a apresentacao. Esse seed roda apenas uma vez
(se ja existir alguma campanha no banco, ele e ignorado).

## Estrutura do projeto

```
src/main/java/com/tcc/rpgmaster/
├── RpgMasterApplication.java     # classe main
├── model/                        # entidades JPA (dominio OOP com heranca)
│   ├── Criatura.java             # classe abstrata (@MappedSuperclass)
│   ├── Personagem.java           # extends Criatura
│   ├── Npc.java                  # extends Criatura
│   ├── Habilidade.java
│   ├── Campanha.java
│   └── ClasseRPG.java / Atributo.java / TipoNpc.java   # enums
├── repository/                   # interfaces Spring Data JPA
├── service/                      # regras de negocio + integracao com IA
│   ├── MistralAiService.java     # chamadas HTTP para a API da Mistral
│   ├── PersonagemService.java
│   ├── NpcService.java
│   ├── CampanhaService.java
│   └── dto/                      # DTOs de request/response da IA
├── controller/                   # controllers Spring MVC
└── config/
    ├── RestTemplateConfig.java
    └── DataSeeder.java           # popula dados de exemplo no startup

src/main/resources/
├── application.properties
├── static/css/style.css          # design system verde (Jogador) / roxo (Mestre)
└── templates/                    # views Thymeleaf
    ├── index.html
    ├── personagens/
    └── campanhas/
```

## Sobre a integracao com IA Generativa

A geracao de conteudo acontece em dois pontos do sistema:

1. **Criacao de personagem (lado do Jogador):** ao preencher nome, classe e
   atributos, a IA recebe esses dados e devolve uma descricao narrativa e uma
   habilidade simplificada coerente com a classe, vinculada ao atributo mais
   relevante do personagem (ex: um Mago com Inteligencia alta recebe uma
   habilidade ofensiva magica).

2. **Geracao de NPC/monstro (lado do Mestre):** o Mestre descreve em
   linguagem natural o que precisa (ex: "um comerciante desconfiado" ou
   "um monstro de gelo para um grupo de nivel 5") e a IA devolve um NPC
   completo: nome, descricao, tipo, nivel de desafio, atributos e pontos
   de vida, ja vinculado a campanha selecionada.

Em ambos os casos, o prompt enviado a Mistral exige resposta em JSON
estrito, que e desserializado para os DTOs `PersonagemGeradoDTO` e
`NpcGeradoDTO` antes de popular as entidades JPA.

## Observacao sobre o ambiente de build deste pacote

O codigo deste projeto foi escrito e revisado manualmente (sintaxe Java e
Thymeleaf conferidas linha a linha), mas **nao foi compilado dentro do
ambiente que gerou este pacote**, pois esse ambiente nao tem acesso de rede
ao Maven Central. Rode `mvn spring-boot:run` (ou `mvn compile`) na sua
maquina, com internet normal, para baixar as dependencias e validar o build
antes da apresentacao.
