package tests;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tests.model.Order;
import tests.model.PoolInfo;
import util.SampleJsonProvider;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

public class Hw05a extends AbstractHw {

    private final String baseUrl = "http://localhost:8080/";

    private ExecutorService pool;

    @Test
    public void canQueryConnectionPoolState() {
        assertPoolState(2, 0);

        makeAsyncQuery("api/orders/slow");

        sleep(0.5);

        assertPoolState(1, 1);

        sleep(1);

        assertPoolState(2, 0);

        makeAsyncQuery("api/orders/slow");
        makeAsyncQuery("api/orders/slow");

        sleep(0.5);

        assertPoolState(0, 2);
    }

    @Test
    public void databaseQueriesAreBlockedWhenAllConnectionsAreInUse()
            throws ExecutionException, InterruptedException {

        String orderNumber = getRandomString();
        postOrder("api/orders", new Order(orderNumber));

        makeAsyncQuery("api/orders/slow");
        makeAsyncQuery("api/orders/slow");

        sleep(0.5);

        assertPoolState(0, 2);

        Future<List<Order>> orderListFuture = makeAsyncQuery("api/orders");

        sleep(0.5);

        assertThat(orderListFuture.isDone(), is(false));

        sleep(1);

        assertThat(orderListFuture.isDone(), is(true));

        List<String> allNumbers = orderListFuture.get().stream()
                .map(Order::getOrderNumber)
                .collect(Collectors.toList());

        assertThat(allNumbers, hasItems(orderNumber));
    }

    private void assertPoolState(int inPool, int inUse) {
        assertThat(getPoolInfo(), is(new PoolInfo(inPool, inUse)));
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

    @Before
    public void setUp() {
        pool = Executors.newFixedThreadPool(3);
    }

    @After
    public void shutDown() {
        pool.shutdown();
    }

    private Future<List<Order>> makeAsyncQuery(String url) {
        Callable<List<Order>> c1 = () -> getList(url);

        return pool.submit(c1);
    }

    private String getRandomString() {
        return new SampleJsonProvider(0).getRandomString(5, 7);
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }
}
