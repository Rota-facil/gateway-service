# gateway-service

API Gateway do Rota Facil. E a porta de entrada HTTP do sistema e concentra roteamento, CORS, validacao de JWT, autorizacao por perfil e propagacao de contexto do usuario para os microservicos.

## Para que serve

- Expoe uma entrada unica em `http://localhost:8080`.
- Resolve servicos via Eureka usando `lb://...`.
- Valida JWT com chave publica.
- Consulta Redis para bloquear tokens invalidados.
- Injeta headers de usuario para os servicos internos.

## Porta e nome

- Aplicacao: `gateway-service`
- Porta: `8080`
- Eureka: `${EUREKA_URL:http://localhost:8081/eureka}`

## Rotas

- `/auth/**` -> `auth-service`
- `/files/**` -> `file-service`
- `/audit/**` -> `audit-service`
- `/transports/**` -> `transport-service`
- `/places/**` -> `places-service`
- `/locations/**` -> `location-service`
- `/notifications/**` -> `notification-service`

## Seguranca

Rotas publicas:

- Swagger/OpenAPI: `/v3/api-docs/**`, `/swagger-ui/**`, `/*/v3/api-docs/**`, `/*/swagger-ui/**`
- Actuator: `/actuator/**`
- Health checks: `/auth/health-check`, `/transports/health-check`, `/files/health-check`, `/places/health-check`, `/audit/health-check`, `/locations/health-check`
- Auth publico: `/auth/user/login`, `/auth/register/**`, `/auth/google/complete-registration`, `/auth/login/oauth2/**`, `/auth/oauth2/**`, `/auth/auth/google/success`

Regras por perfil:

- `SUPERUSER`: `/auth/user/prefecture/register`, `/auth/prefectures/**`
- `ADMIN` ou `SUPERUSER`: `/places/**`, `/audit/**`
- `ADMIN`: `/auth/driver/register`, `/transports/routes/register`, `/transports/trips/register`, `/transports/bus/register`
- Demais rotas: usuario autenticado.

Headers encaminhados aos servicos:

- `x-user-id`
- `x-user-role`
- `x-user-email`
- `x-prefecture-id`
- `x-user-token`

## Redis e invalidacao de token

O gateway usa Redis para armazenar tokens invalidados por eventos do `auth-service`, especialmente quando usuario e deletado ou troca email. Propriedades principais:

- `REDIS_HOST`
- `REDIS_PORT`
- `REDIS_PASSWORD`

## Eventos consumidos

Exchange: `auth.events`

- `user.deleted`
- `user.email.changed`

As filas default sao `gateway.user.deleted.queue` e `gateway.user.email.changed.queue`.

## Como rodar

Pre-requisitos:

- Java 21.
- Eureka rodando.
- Redis rodando.
- RabbitMQ rodando.

Comando:

```bash
cd gateway-service
./mvnw spring-boot:run
```

Variaveis comuns:

- `EUREKA_URL`
- `PUBLIC_KEY`
- `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USER`, `RABBITMQ_PASSWORD`
- `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`
- `WEB_BASE_URL`, `APP_BASE_URL`

## Especializacao

Este servico nao deve conter regra de negocio de transporte, usuarios, arquivos ou lugares. Sua responsabilidade e borda HTTP, seguranca, roteamento, CORS e propagacao de identidade.
