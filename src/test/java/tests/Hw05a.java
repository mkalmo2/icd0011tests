package tests;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tests.model.Order;
import tests.model.PoolInfo;

import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

public class Hw05a extends AbstractHw {

    private final String baseUrl = "http://localhost:8080/";

    private ExecutorService pool;

    @Test
    public void canQueryConnectionPoolState() {
        assertPoolState(2, 0); // 2 available, 0 used

        makeAsyncQuery("api/orders/slow");

        sleep(0.5);

        assertPoolState(1, 1);  // 1 available, 1 used

        sleep(1);

        assertPoolState(2, 0);  // 2 available, 0 used

        makeAsyncQuery("api/orders/slow");
        makeAsyncQuery("api/orders/slow");

        sleep(0.5);

        assertPoolState(0, 2);  // 0 available, 2 used
    }

    @Test
    public void databaseQueriesAreBlockedWhenAllConnectionsAreInUse()
            throws ExecutionException, InterruptedException {

        String orderNumber = getRandomString(5, 7);
        postOrder("api/orders", new Order(orderNumber));

        makeAsyncQuery("api/orders/slow");
        makeAsyncQuery("api/orders/slow");

        sleep(0.5);

        assertPoolState(0, 2);

        Future<List<Order>> orderListFuture = makeAsyncQuery("api/orders");

        sleep(0.5);

        assertThat(orderListFuture.isDone()).isFalse();

        sleep(1);

        assertThat(orderListFuture.isDone()).isTrue();

        List<String> allNumbers = orderListFuture.get().stream()
                .map(Order::getOrderNumber)
                .toList();

        assertThat(allNumbers).contains(orderNumber);
    }

    private void assertPoolState(int inPool, int inUse) {
        assertThat(getPoolInfo()).isEqualTo(new PoolInfo(inPool, inUse));
    }

    private void sleep(double seconds) {
        try {
            Thread.sleep(Double.valueOf(seconds * 1000).intValue());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private PoolInfo getPoolInfo() {
        Response response = getTarget()
                .path("api/pool/info")
                .request(MediaType.APPLICATION_JSON)
                .get();

        return response.readEntity(PoolInfo.class);
    }

    @BeforeEach
    public void setUp() {
        pool = Executors.newFixedThreadPool(3);
    }

    @AfterEach
    public void shutDown() {
        pool.shutdown();
    }

    private Future<List<Order>> makeAsyncQuery(String url) {
        Callable<List<Order>> c1 = () -> getList(url);

        return pool.submit(c1);
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }
}