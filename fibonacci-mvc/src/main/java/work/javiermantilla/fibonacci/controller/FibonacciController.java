package work.javiermantilla.fibonacci.controller;


import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.javiermantilla.fibonacci.service.FibonacciService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class FibonacciController {
    private static final Logger logger = LoggerFactory.getLogger(FibonacciController.class);
    private final FibonacciService fibonacciService;

    @GetMapping("/fibonacci/{n}")
    public ResponseEntity<Map<String, Object>> getFibonacci(@PathVariable int n) {
        long startTime = System.currentTimeMillis();

        if (n < 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El número debe ser mayor o igual a 0"));
        }

        if (n > 100) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El número debe ser menor o igual a 45 para evitar timeouts"));
        }

        List<Long> fibonacciSeries = fibonacciService.calculateFibonacciSeries(n);
        long lastValue = fibonacciSeries.get(fibonacciSeries.size() - 1);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        logger.info("MVC - Fibonacci({}): Serie = {}, Último valor = {}, Tiempo = {}ms",
                n, fibonacciSeries, lastValue, executionTime);

        Map<String, Object> response = new HashMap<>();
        response.put("n", n);
        response.put("series", fibonacciSeries);
        response.put("lastValue", lastValue);
        response.put("executionTime", executionTime);
        response.put("type", "MVC");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "type", "MVC"));
    }
}
