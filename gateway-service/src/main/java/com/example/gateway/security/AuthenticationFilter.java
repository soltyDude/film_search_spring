package com.example.gateway.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        log.info("AuthenticationFilter - Incoming path: {}", path);

        // Простая проверка: если в path есть /auth/login или /users/register или /actuator — не проверяем JWT.
        // (Можете добавить дополнительные эндпоинты, если нужно.)
        if (path.contains("/auth/login") || path.contains("/users/register") || path.contains("/actuator")) {
            log.info("Public endpoint => skipping JWT check");
            return chain.filter(exchange);
        }

        // Проверяем заголовок Authorization
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Проверяем токен
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            log.warn("JWT validation failed");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Извлекаем email, role, пробрасываем дальше
        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractRole(token);

        log.info("JWT is valid => email={}, role={}", email, role);

        // "Обогащаем" запрос заголовками
        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header("X-User-Email", email)
                .header("X-User-Role", role)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        // Ставим повыше приоритет (отрицательное значение)
        return -2;
    }
}




//curl -X GET "http://localhost:8081/user-service/users/exists/1" \
//-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtZW50b3JAZXhhbXBsZS5jb20iLCJyb2xlIjoiVVNFUiIsImlhdCI6MTc0NDY2NjU1NCwiZXhwIjoxNzQ0NzUyOTU0fQ.Wamp1H02EBFhlV2uDwdacbkhW1WTb4AM-VhTQVh1kt6k9y31eFgBs0rz9jC7tRor2kLmZlDWxD_gM-9IWPeoBA"