package learn.concurrency.domain.service;

import learn.concurrency.domain.entity.Stock;
import learn.concurrency.domain.repository.StockRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

    private final StockRepo stockRepo;

    public StockService(StockRepo stockRepo) {
        this.stockRepo = stockRepo;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public synchronized void decrease(Long id, Long quantity) {
        Stock stock = stockRepo.findById(id).orElseThrow();
        stock.decrease(quantity);
        stockRepo.saveAndFlush(stock);
    }
}
