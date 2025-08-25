package work.javiermantilla.fibonacci.handler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.javiermantilla.fibonacci.service.FibonacciService;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class FibonacciHandler {

    private static final Logger logger = LoggerFactory.getLogger(FibonacciHandler.class);
    private final FibonacciService fibonacciService;

    public Mono<ServerResponse> getFibonacci(ServerRequest request) {
        return Mono.fromSupplier(() -> {
                    try {
                        int n = Integer.parseInt(request.pathVariable("n"));
                        if (n < 0) {
                            throw new IllegalArgumentException("El número debe ser mayor o igual a 0");
                        }
                        if (n > 100) {
                            throw new IllegalArgumentException("El número debe ser menor o igual a 45 para evitar timeouts");
                        }
                        return n;
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Número inválido");
                    }
                })
                .flatMap(n -> {
                    Instant startTime = Instant.now();
                    // Obtenemos tanto la serie completa como el último valor de manera reactiva
                    Mono<List<Long>> seriesMono = fibonacciService.calculateFibonacciMono(n);
                    Mono<Long> lastValueMono = fibonacciService.calculateLastFibonacci(n);
                    // Combinamos ambos resultados
                    return Mono.zip(seriesMono, lastValueMono)
                            .map(tuple -> {
                                List<Long> series = tuple.getT1();
                                Long lastValue = tuple.getT2();
                                Instant endTime = Instant.now();
                                long executionTime = Duration.between(startTime, endTime).toMillis();
                                logger.info("WebFlux - Fibonacci({}): Serie = {}, Último valor = {}, Tiempo = {}ms",
                                        n, series, lastValue, executionTime);
                                return Map.of(
                                        "n", n,
                                        "series", series,
                                        "lastValue", lastValue,
                                        "executionTime", executionTime,
                                        "type", "WebFlux-Reactive"
                                );
                            });
                })
                .flatMap(result -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(result))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage()))
                );
    }

    // Endpoint adicional que retorna streaming de valores (Flux)
    public Mono<ServerResponse> getFibonacciStream(ServerRequest request) {
        return Mono.fromSupplier(() -> {
                    try {
                        int n = Integer.parseInt(request.pathVariable("n"));

                        if (n < 0 || n > 45) {
                            throw new IllegalArgumentException("El número debe estar entre 0 y 45");
                        }

                        return n;
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Número inválido");
                    }
                })
                .flatMap(n -> {
                    logger.info("WebFlux Stream - Iniciando cálculo de Fibonacci({})", n);

                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_NDJSON) // New-line delimited JSON
                            .body(fibonacciService.calculateFibonacciFlux(n)
                                            .doOnNext(value -> logger.info("WebFlux Stream - Fibonacci valor: {}", value))
                                            .map(value -> Map.of("value", value, "type", "stream")),
                                    Object.class);
                })
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", e.getMessage()))
                );
    }

    public Mono<ServerResponse> health(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("status", "UP", "type", "WebFlux-Reactive"));
    }
}
