package learn.concurrency;

import learn.concurrency.domain.entity.Stock;
import learn.concurrency.domain.repository.StockRepo;
import learn.concurrency.domain.service.OptimisticLockStockService;
import learn.concurrency.domain.service.PessimisticLockStockService;
import learn.concurrency.domain.service.StockService;
import learn.concurrency.domain.service.facade.LettuceStockFacade;
import learn.concurrency.domain.service.facade.NamedLockStockFacade;
import learn.concurrency.domain.service.facade.OptimisticLockStockFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ConcurrencyApplicationTests {

	@Autowired
	StockService stockService;

	@Autowired
	StockRepo stockRepo;

	@Autowired
	PessimisticLockStockService pessimisticLockStockService;

	@Autowired
	OptimisticLockStockFacade optimisticLockStockFacade;

	@Autowired
	NamedLockStockFacade namedLockStockFacade;

	@Autowired
	LettuceStockFacade lettuceStockFacade;

	@BeforeEach
	void before() {
		Stock stock = new Stock(1L, 100L);
		stockRepo.saveAndFlush(stock);
	}

	@Test
	void stock_decrease_test() {
		stockService.decrease(1L, 1L);
		Stock stock = stockRepo.findById(1L).orElseThrow();

		assertEquals(99, stock.getQuantity());

	}

	@Test
	void request_100_simultaneously() throws Exception {
		int threadCnt = 100;
		CountDownLatch countDownLatch = new CountDownLatch(threadCnt);
		ExecutorService executorService = Executors.newFixedThreadPool(32);

		for (int i = 0; i < threadCnt; i++) {
			executorService.submit(() -> {
				try {
					lettuceStockFacade.decrease(1L, 1L);
				} catch (Exception e) {
					throw new RuntimeException();
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();

		Stock stock = stockRepo.findById(1L).orElseThrow();

		 stock.getQuantity();
	}
}
