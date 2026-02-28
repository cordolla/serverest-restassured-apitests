# ServeRest API Automation Tests (Rest Assured)

Este projeto consiste numa suíte de testes automatizados para a API [ServeRest](https://serverest.dev), utilizando **Java** e **Rest Assured**. O foco principal é a validação dos fluxos de utilizadores, produtos e carrinhos, aplicando padrões de projeto para garantir uma automação robusta e de fácil manutenção.

## 🚀 Tecnologias Utilizadas

* **Linguagem:** Java 21
* **Framework de Teste:** Rest Assured 5.5.0
* **Engine de Execução:** JUnit 5 (Jupiter)
* **Massa de Dados:** Java Faker
* **Relatórios:** Allure Report
* **Manipulação de JSON:** Jackson Databind/Annotations
* **Build Tool:** Maven

## 🏗️ Arquitetura e Padrões de Projeto

O projeto foi estruturado seguindo as melhores práticas de engenharia de software para testes:

* **Data Factory:** Utilização da biblioteca Java Faker na classe `UserDataFactory` para gerar dados dinâmicos (nomes, e-mails e senhas aleatórios), evitando conflitos de dados em execuções repetidas.
* **BaseTest (Hooks):** Classe base abstrata que gere as configurações globais, como a `BASE_URL`, logs de requisição/resposta em caso de falha e o gerenciamento automático do token de autenticação.
* **POJOs (Modelagem):** Uso de classes de modelo (como `UserRequest`) para representar os payloads da API, facilitando a serialização e desserialização com Jackson.
* **Filtros Allure:** Configuração de filtros no Rest Assured para anexar automaticamente os detalhes de cada request/response aos relatórios visuais.

## 🧪 Cobertura de Testes (Principais Cenários)

A automação cobre fluxos críticos da API, incluindo:

* **Usuários:** Listagem, cadastro, busca por ID, exclusão e edição via PUT.
* **Validações de Erro:** Tentativa de cadastro com e-mail duplicado, busca por ID inexistente e restrição de exclusão para utilizadores com carrinhos ativos.
* **Integrações:** Fluxos complexos que envolvem a criação de produtos e carrinhos vinculados ao utilizador autenticado.

## 📊 Como Executar e Gerar Relatórios

1. **Pré-requisitos:** Ter o Java 21 e o Maven instalados.
2. **Executar os testes:**
   ```bash
   mvn test
