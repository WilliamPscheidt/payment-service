
# 💳 payment-service

Microserviço de pagamentos desenvolvido em Java com Spring Boot, parte da arquitetura do sistema **academyWorkout**.

## 📦 Visão Geral

Este serviço é responsável pelo processamento de pagamentos de assinaturas. Ele lida com a criação de cobranças, verificação de status de pagamento e integração com gateways de pagamento.

## ⚙️ Tecnologias Utilizadas

- Java 21
- Spring Boot
- Maven
- MySQL
- JPA / Hibernate
- Docker
- Lombok
- Spring Web
- Spring Data JPA
- Spring DevTools

## 🚀 Como Executar Localmente

### Pré-requisitos

- Java 17
- Maven
- Docker e Docker Compose (opcional, mas recomendado)

### Passos

1. **Clone o projeto:**

```bash
git clone https://github.com/WilliamPscheidt/payment-service.git
cd payment-service
```

2. **Configure o arquivo `.env` ou `application.yml`:**

Atualize as configurações de banco de dados em `src/main/resources/application.yml` com suas credenciais locais ou use Docker para rodar o banco.

3. **Suba os containers com Docker (se desejar):**

```bash
docker-compose up -d
```

4. **Build e execute a aplicação:**

```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em:  
📍 `http://localhost:8080`

## 🧪 Endpoints Principais

| Método | Endpoint                                     | Descrição                      |
|--------|----------------------------------------------|--------------------------------|
| POST   | `/api/payments/subscribe`                    | Criar uma nova cobrança        |
| POST   | `/api/payments/subscription/expiration`      | Verificar expiração da assinatura |
| DELETE | `/api/payments/subscription`                 | Cancelar a assinatura          |


## 🗃️ Estrutura do Projeto

```
payment-service
├── config        # Classes de configuração (ex: KeyCloak)
├── controllers   # Camada de Controle (Endpoints REST) que recebe as requisições HTTP
├── dto           # Objetos de Transferência de Dados (Data Transfer Objects) para requests e responses
├── exception     # Exceções customizadas e handlers para tratamento de erros globais
├── factory       # Fábricas para a criação de objetos
├── model         # Modelos de dados e Entidades JPA que representam as tabelas do banco
├── repositories  # Repositórios (Data Access Layer) para interagir com o banco de dados
└── service       # Camada de Serviço que contém a lógica de negócio da aplicação
```

### Desenvolvido por William Pscheidt
