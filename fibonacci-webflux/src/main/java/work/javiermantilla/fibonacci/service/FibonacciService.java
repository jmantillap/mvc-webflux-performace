package work.javiermantilla.fibonacci.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

@Service
public class FibonacciService {

    // Opción 1: Retorna Flux<Long> - Streaming de valores individuales
    public Flux<Long> calculateFibonacciFlux(int n) {
        if (n <= 0) {
            return Flux.just(0L);
        }

        return Flux.generate(
                () -> new FibonacciState(0L, 1L, 0),
                (state, sink) -> {
                    if (state.index >= n) {
                        sink.complete();
                        return state;
                    }

                    if (state.index == 0) {
                        sink.next(0L);
                    } else if (state.index == 1) {
                        sink.next(1L);
                    } else {
                        long next = state.current + state.previous;
                        sink.next(next);
                        state = new FibonacciState(state.current, next, state.index);
                    }

                    return new FibonacciState(state.previous, state.current, state.index + 1);
                }
        );
    }

    // Opción 2: Retorna Mono<List<Long>> - Lista completa de manera reactiva
    public Mono<List<Long>> calculateFibonacciMono(int n) {
        return calculateFibonacciFlux(n)
                .collectList();
    }

    // Método utilitario para obtener solo el último valor
    public Mono<Long> calculateLastFibonacci(int n) {
        return calculateFibonacciFlux(n)
                .takeLast(1)
                .single();
    }

    // Clase helper para mantener el estado
    private static class FibonacciState {
        final long previous;
        final long current;
        final int index;

        FibonacciState(long previous, long current, int index) {
            this.previous = previous;
            this.current = current;
            this.index = index;
        }
    }
}