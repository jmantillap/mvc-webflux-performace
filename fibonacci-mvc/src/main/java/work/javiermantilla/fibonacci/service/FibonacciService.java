package work.javiermantilla.fibonacci.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FibonacciService {
    public List<Long> calculateFibonacciSeries(int n) {
        List<Long> series = new ArrayList<>();

        if (n == 0) {
            series.add(0L);
            return series;
        }

        if (n >= 1) {
            series.add(0L);
        }
        if (n >= 2) {
            series.add(1L);
        }

        for (int i = 2; i < n; i++) {
            long next = series.get(i - 1) + series.get(i - 2);
            series.add(next);
        }

        return series;
    }
}
