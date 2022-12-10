package learn.concurrency.domain.service;

import learn.concurrency.domain.entity.Stock;
import learn.concurrency.domain.repository.StockRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OptimisticLockStockService {

    private final StockRepo stockRepo;

    public OptimisticLockStockService(StockRepo stockRepo) {
        this.stockRepo = stockRepo;
    }

    @Transactional
    public void decrease(Long id, Long quantity) {
        Stock stock = stockRepo.findByIdWithOptimisticLock(id);
        stock.decrease(quantity);

        stockRepo.saveAndFlush(stock);
    }
}
