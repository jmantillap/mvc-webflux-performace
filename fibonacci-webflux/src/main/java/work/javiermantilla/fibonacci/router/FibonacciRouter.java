package work.javiermantilla.fibonacci.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import work.javiermantilla.fibonacci.handler.FibonacciHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class FibonacciRouter {

    @Bean
    public RouterFunction<ServerResponse> routes(FibonacciHandler handler) {
        return RouterFunctions
                .route(GET("/api/fibonacci/{n}").and(accept(MediaType.APPLICATION_JSON)),
                        handler::getFibonacci)
                .andRoute(GET("/api/fibonacci/stream/{n}").and(accept(MediaType.APPLICATION_JSON)),
                        handler::getFibonacciStream)
                .andRoute(GET("/api/health").and(accept(MediaType.APPLICATION_JSON)),
                        handler::health);
    }
}