# Desafio Eso 

API REST desenvolvida em Spring Boot para gerenciamento de usuários, cosméticos e transações relacionadas ao Fortnite. Esta aplicação integra com a API pública do Fortnite para buscar informações sobre cosméticos e permite que usuários gerenciem seus perfis, comprem cosméticos e acompanhem suas transações.

## 📋 Sobre o Projeto

Esta API foi desenvolvida como parte de um desafio técnico e oferece as seguintes funcionalidades principais:

- **Gerenciamento de Usuários**: CRUD completo para cadastro e gestão de usuários
- **Cosméticos do Fortnite**: Integração com a API do Fortnite para buscar e listar cosméticos disponíveis
- **Transações**: Sistema de registro de transações financeiras (compras de cosméticos, créditos iniciais, etc.)
- **Relacionamentos**: Gerenciamento da relação entre usuários e cosméticos adquiridos

## 🛠️ Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.5.7**
- **Spring Data JPA** - Persistência de dados
- **PostgreSQL** - Banco de dados relacional
- **Docker & Docker Compose** - Containerização e orquestração
- **Spring WebFlux** - Cliente reativo para integração com APIs externas
- **MapStruct** - Mapeamento entre entidades e DTOs
- **Lombok** - Redução de boilerplate
- **SpringDoc OpenAPI** - Documentação automática da API (Swagger)
- **Maven** - Gerenciamento de dependências

## 📦 Pré-requisitos

Antes de executar o projeto, certifique-se de ter instalado:

- **Java 17** ou superior
- **Maven 3.6+** (ou utilize o Maven Wrapper incluído - `./mvnw`)
- **Docker** e **Docker Compose**
- **Chave da API do Fortnite** (opcional, para funcionalidades completas)

## 🚀 Como Executar o Projeto

### Passo 1: Compilar o Projeto

Primeiro, compile o projeto Maven gerando o arquivo JAR:

```bash
./mvnw -DskipTests clean package
```

**O que este comando faz:**
- `./mvnw` - Executa o Maven Wrapper (Maven embutido no projeto)
- `-DskipTests` - Pula a execução dos testes durante a compilação (opcional, mas acelera o build)
- `clean` - Remove arquivos de compilação anteriores (pasta `target/`)
- `package` - Compila o projeto e gera o arquivo JAR na pasta `target/`

O resultado será um arquivo JAR gerado em `target/desafio-tech-0.0.1-SNAPSHOT.jar`.

### Passo 2: Construir as Imagens Docker

Em seguida, construa as imagens Docker dos serviços definidos no `compose.yaml`:

```bash
docker compose build
```

**O que este comando faz:**
- Lê o arquivo `compose.yaml` na raiz do projeto
- Constrói a imagem Docker da aplicação Spring Boot usando o `Dockerfile`
- Prepara a imagem do PostgreSQL (já disponível no Docker Hub)
- Prepara a rede Docker para comunicação entre os serviços

### Passo 3: Iniciar os Serviços

Por fim, inicie todos os serviços com Docker Compose:

```bash
docker compose up
```

**O que este comando faz:**
- Inicia o container do PostgreSQL na porta 5432
- Aguarda o PostgreSQL estar saudável (healthcheck)
- Inicia o container da aplicação Spring Boot na porta 8080
- Conecta os serviços na mesma rede Docker

Para executar em modo daemon (background), use:

```bash
docker compose up -d
```

### Acessar a Aplicação

Após a execução dos comandos acima, a aplicação estará disponível em:

- **API Base**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

## 📚 Endpoints Principais

### Usuários (`/api/users`)
- `GET /api/users` - Lista todos os usuários (com paginação)
- `GET /api/users/{id}` - Busca usuário por ID
- `POST /api/users` - Cria um novo usuário
- `PUT /api/users/{id}` - Atualiza um usuário
- `DELETE /api/users/{id}` - Remove um usuário

### Cosméticos (`/api/cosmetics`)
- `GET /api/cosmetics` - Lista todos os cosméticos (com filtros e paginação)
- `GET /api/cosmetics/new` - Lista cosméticos recém-lançados
- `GET /api/cosmetics/shop` - Lista cosméticos disponíveis na loja

### Transações (`/api/transactions`)
- Endpoints para gerenciamento de transações

### Usuários e Cosméticos (`/api/user-cosmetics`)
- Endpoints para relacionar usuários com cosméticos adquiridos

## ⚙️ Configuração

As configurações principais estão no arquivo `src/main/resources/application.properties`:

- **Perfil ativo**: `prod`
- **CORS**: Configurado para permitir requisições de `localhost:3000`, `localhost:4200` e `localhost:8080`
- **API Fortnite**: Base URL configurada em `https://fortnite-api.com/v2`
- **Banco de Dados**: Configurações definidas via variáveis de ambiente no `compose.yaml`

### Configuração do Banco de Dados (Docker Compose)

O `compose.yaml` configura automaticamente:
- **PostgreSQL** na porta `5432`
- **Database**: `mydatabase`
- **Usuário**: `myuser`
- **Senha**: `secret`

## 📖 Documentação da API

A documentação interativa da API está disponível através do Swagger UI após iniciar a aplicação:

```
http://localhost:8080/swagger-ui.html
```

Lá você encontrará:
- Descrição de todos os endpoints
- Parâmetros de requisição e resposta
- Possibilidade de testar os endpoints diretamente
- Modelos de dados (schemas)

## 🛑 Parar os Serviços

Para parar os containers Docker em execução:

```bash
docker compose down
```

Para parar e remover também os volumes (limpando o banco de dados):

```bash
docker compose down -v
```

## 📁 Estrutura do Projeto

```
desafio-tech/
├── src/
│   ├── main/
│   │   ├── java/com/barbosa/desafio_tech/
│   │   │   ├── config/          # Configurações (OpenAPI, WebClient, etc.)
│   │   │   ├── controller/      # Controllers REST
│   │   │   ├── domain/
│   │   │   │   ├── dto/         # Data Transfer Objects
│   │   │   │   ├── entities/    # Entidades JPA
│   │   │   │   ├── mappers/     # MapStruct mappers
│   │   │   │   ├── repository/  # Repositórios Spring Data JPA
│   │   │   │   ├── response/    # Classes de resposta da API Fortnite
│   │   │   │   └── service/     # Lógica de negócio
│   │   │   └── DesafioTechApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/                    # Testes unitários
├── compose.yaml                 # Configuração Docker Compose
├── Dockerfile                   # Imagem Docker da aplicação
├── pom.xml                      # Dependências Maven
└── README.md                    # Este arquivo
```

## 🔧 Desenvolvimento

Para executar o projeto em modo desenvolvimento (sem Docker):

1. Certifique-se de ter um PostgreSQL rodando localmente
2. Configure as propriedades do banco no `application.properties`
3. Execute: `./mvnw spring-boot:run`

## 📝 Notas

- Os usuários recebem automaticamente 10.000 V-Bucks ao serem criados
- Todas as transações são registradas automaticamente no sistema
- A aplicação utiliza validações Bean Validation para garantir a integridade dos dados



