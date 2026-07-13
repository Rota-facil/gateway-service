# gateway-service

API Gateway do Rota Fácil. É a entrada HTTP única e concentra roteamento, CORS, validação de JWT, autorização, invalidação de tokens e propagação de identidade.

## Porta e rotas

- Porta: `8080`
- Eureka: `${EUREKA_URL:http://localhost:8081/eureka}`

| Prefixo | Destino |
| --- | --- |
| `/auth/**` | `auth-service` |
| `/files/**` | `file-service` |
| `/audit/**` | `audit-service` |
| `/transports/**` | `transport-service` |
| `/places/**` | `places-service` |
| `/locations/**` | `location-service` |
| `/notifications/**` | `notification-service` |

O `intelligence-service` é chamado diretamente pelo `transport-service`.

## Autenticação e headers

O filtro valida o JWT com a chave pública, consulta o Redis e encaminha `x-user-id`, `x-user-email`, `x-user-role`, `x-prefecture-id` e `x-user-token`.

## Regras de autorização

Rotas públicas incluem `OPTIONS`, Actuator, Swagger/OpenAPI, health checks configurados e fluxos de login, cadastro público e OAuth.

- `GET /places/**`: autenticado; escritas em `/places/**`: `ADMIN` ou `SUPERUSER`.
- `/audit/**`, métricas, relatórios, consulta de feedbacks e recursos analíticos: `ADMIN` ou `SUPERUSER`.
- `/auth/user/prefecture/register` e operações de prefeitura: `SUPERUSER`; GETs de prefeitura são públicos.
- Listagem de estudantes: `ADMIN` ou `SUPERUSER`.
- Cadastro/edição de motorista, ônibus, rotas e viagens: regras explícitas de `ADMIN`.
- Entrada, saída e check-in em viagem, e troca de prefeitura: `STUDENT`.
- `GET /transports/trips/my-trips`: `STUDENT` ou `DRIVER`.
- Início da ida, início da volta e cancelamento: `DRIVER`.
- Demais rotas: autenticado.

Há duas regras para `/auth/user/prefecture/register`; a de `SUPERUSER` aparece primeiro e prevalece.

## Redis e eventos

Consome `user.deleted`, `user.email.changed` e `user.logout` de `auth.events`. Os bindings usam a fila `gateway.invalid.user.token.queue` para invalidar tokens no Redis.

Variáveis principais: `PUBLIC_KEY`, `REDIS_*`, `RABBITMQ_*`, `EUREKA_URL`, `WEB_BASE_URL`, `APP_BASE_URL` e `CLOUD_URL`.

## Como rodar

Pré-requisitos: Java 21, Eureka, Redis e RabbitMQ.

```bash
cd gateway-service
./mvnw spring-boot:run
```

## Limite do domínio

O gateway atua como borda HTTP e não implementa regras dos domínios.
