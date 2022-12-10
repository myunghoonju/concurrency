package learn.concurrency.domain.service.facade;

import learn.concurrency.domain.repository.RedisLockRepository;
import learn.concurrency.domain.service.StockService;
import org.springframework.stereotype.Component;

@Component
public class LettuceStockFacade {

    private final RedisLockRepository redisLockRepository;
    private final StockService stockService;

    public LettuceStockFacade(RedisLockRepository redisLockRepository, StockService stockService) {
        this.redisLockRepository = redisLockRepository;
        this.stockService = stockService;
    }

    public void decrease(Long key, Long quantity) throws InterruptedException {
        while (!redisLockRepository.lock(key)) {
            Thread.sleep(100);
        }

        try {
            stockService.decrease(key, quantity);
        } finally {
            redisLockRepository.unlock(key);
        }
    }
}
