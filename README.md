# ServeRest REST Assured API Tests

Projeto de automação de testes de API utilizando **Java**, **REST Assured** e **JUnit**, com foco na validação dos endpoints da API pública ServeRest.

Repositório da API: https://serverest.dev
Projeto versionado: https://github.com/cordolla/serverest-restassured-apitests

---

## Sumário

* [Sobre o Projeto](#sobre-o-projeto)
* [Objetivos](#objetivos)
* [Tecnologias Utilizadas](#tecnologias-utilizadas)
* [Arquitetura e Organização](#arquitetura-e-organização)
* [Estrutura do Projeto](#estrutura-do-projeto)
* [Pré-requisitos](#pré-requisitos)
* [Configuração do Ambiente](#configuração-do-ambiente)
* [Como Executar os Testes](#como-executar-os-testes)
* [Estratégia de Testes](#estratégia-de-testes)
* [Boas Práticas Adotadas](#boas-práticas-adotadas)
* [Exemplos de Cenários Cobertos](#exemplos-de-cenários-cobertos)
* [Integração Contínua](#integração-contínua)
* [Possíveis Melhorias](#possíveis-melhorias)
* [Autor](#autor)
* [Licença](#licença)

---

## Sobre o Projeto

Este projeto tem como objetivo demonstrar a automação de testes de API REST utilizando a biblioteca REST Assured, validando os principais endpoints da API ServeRest.

A API ServeRest é amplamente utilizada para estudos e simulações de cenários reais de testes de API, permitindo validação de fluxos como:

* Cadastro de usuários
* Autenticação (login)
* CRUD de produtos
* Carrinhos
* Regras de negócio
* Validações de autenticação e autorização

O projeto foi estruturado seguindo boas práticas de organização, reutilização de código e separação de responsabilidades.

---

## Objetivos

* Demonstrar conhecimento em automação de testes de API
* Aplicar boas práticas com REST Assured
* Utilizar arquitetura organizada e escalável
* Implementar testes positivos e negativos
* Trabalhar com dados dinâmicos para evitar conflitos
* Estruturar o projeto pensando em integração contínua

---

## Tecnologias Utilizadas

* Java 11+
* REST Assured
* JUnit 5
* Maven
* Hamcrest
* Jackson (serialização/desserialização JSON)

---

## Arquitetura e Organização

O projeto segue uma abordagem em camadas:

* Camada de Testes
* Camada de Serviços
* Camada de Modelos
* Camada de Fábricas de Dados
* Configuração Base

Essa separação facilita:

* Manutenção
* Escalabilidade
* Reutilização de código
* Clareza na leitura

---

## Estrutura do Projeto

```
serverest-restassured-apitests
│
├── src
│   ├── main
│   │   └── java
│   │
│   └── test
│       └── java
│           ├── base
│           ├── factories
│           ├── models
│           ├── services
│           └── tests
│
├── pom.xml
└── README.md
```

### Descrição das Pastas

#### base

Contém configurações globais como:

* Base URI
* Configuração padrão do REST Assured
* Setup comum para os testes

#### factories

Responsável por gerar dados dinâmicos para os testes:

* Usuários com e-mails únicos
* Produtos com dados variados
* Payloads personalizados

#### models

Representação das entidades da API:

* User
* Product
* Login
* Cart
* Outros DTOs necessários

#### services

Centraliza as chamadas HTTP:

* GET
* POST
* PUT
* DELETE

Essa camada evita repetição de código dentro dos testes.

#### tests

Contém as classes de teste organizadas por funcionalidade ou domínio.

---

## Pré-requisitos

Antes de executar o projeto, é necessário ter instalado:

* Java 11 ou superior
* Maven 3.8+
* Git

Para verificar as versões instaladas:

```
java -version
mvn -version
```

---

## Configuração do Ambiente

### 1. Clonar o repositório

```
git clone https://github.com/cordolla/serverest-restassured-apitests.git
```

### 2. Acessar o diretório do projeto

```
cd serverest-restassured-apitests
```

### 3. Instalar dependências

```
mvn clean install -DskipTests
```

---

## Como Executar os Testes

### Executar todos os testes

```
mvn clean test
```

### Executar uma classe específica

```
mvn -Dtest=NomeDaClasseTest test
```

### Executar um método específico

```
mvn -Dtest=NomeDaClasseTest#nomeDoMetodo test
```

---

## Estratégia de Testes

Os testes seguem o padrão:

* Given (pré-condição)
* When (ação)
* Then (validação)

Utilizando a estrutura fluente da REST Assured.

### Testes Positivos

* Status code esperado (200, 201, 204)
* Validação do corpo da resposta
* Validação de campos obrigatórios
* Persistência correta dos dados

### Testes Negativos

* Campos obrigatórios ausentes
* Dados inválidos
* Usuário já existente
* Token inválido ou ausente
* Acesso não autorizado
* Regras de negócio violadas

---

## Exemplos de Cenários Cobertos

* Criar usuário com sucesso
* Não permitir criação de usuário com e-mail duplicado
* Realizar login com credenciais válidas
* Falhar login com senha inválida
* Criar produto autenticado
* Não permitir criação de produto sem token
* Atualizar produto existente
* Deletar produto
* Listar produtos cadastrados

---

## Boas Práticas Adotadas

* Separação clara de responsabilidades
* Reutilização de métodos HTTP
* Uso de DTOs para serialização
* Geração de dados dinâmicos
* Testes independentes
* Padronização de assertions
* Código limpo e legível
* Estrutura escalável

---

## Integração Contínua

O projeto pode ser integrado facilmente com:

* GitHub Actions
* Jenkins
* GitLab CI

Comando padrão para execução em pipeline:

```
mvn clean test
```

É possível também gerar relatórios adicionais com plugins do Maven, como Surefire Reports ou Allure.

---

## Possíveis Melhorias

* Implementação de testes de contrato (JSON Schema)
* Integração com Allure Reports
* Execução paralela de testes
* Parametrização de ambientes (dev, hml, prod)
* Dockerização do ambiente
* Integração com ferramentas de quality gate

---

## Autor

Projeto desenvolvido para fins de estudo, prática e portfólio.

GitHub: https://github.com/cordolla

---

## Licença

Este projeto é destinado a fins educacionais e de portfólio.

Sinta-se livre para estudar, adaptar e reutilizar o código.
