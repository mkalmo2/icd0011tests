package tests;

import org.junit.Test;
import tests.model.Order;
import tests.model.OrderRow;
import tests.model.Result;
import util.IfThisTestFailsMaxPoints;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class Hw04 extends AbstractHw {

    private final String baseUrl = "http://localhost:8080";

    @Test
    public void canGetOrderById() {
        Result<Order> result = postOrder("api/orders", new Order("A123"));

        assertThat(result.isSuccess(), is(true));

        String idOfPostedOrder = result.getValue().getId();

        Order order = getOne("api/orders", param("id", idOfPostedOrder));

        assertThat(order.getOrderNumber(), is("A123"));
        assertThat(order.getId(), is(idOfPostedOrder));
    }

    @Test
    public void addsOrderWithOrderRows() {
        Order order = new Order("A456");
        order.add(new OrderRow("CPU", 2, 100));
        order.add(new OrderRow("Motherboard", 3, 60));

        Result<Order> result = postOrder("api/orders", order);

        assertThat(result.isSuccess(), is(true));

        String idOfPostedOrder = result.getValue().getId();

        Order read = getOne("api/orders", param("id", idOfPostedOrder));

        assertThat(read.getOrderRows().size(), is(2));
        assertThat(read.getOrderRows().get(1).getItemName(),
                is("Motherboard"));
    }

    @Test
    @IfThisTestFailsMaxPoints(3)
    public void addsOrderFromFormInput() {
        Long idOfPostedOrder = postForm("orders/form",
                getForm("orderNumber", "A789"));

        Order read = getOne("api/orders", param("id", idOfPostedOrder));

        assertThat(read.getOrderNumber(), is("A789"));
    }

    private Form getForm(String name, String value) {
        Form form = new Form();
        form.param(name, value);
        return form;
    }

    private Long postForm(String path, Form form) {
        Response response = getTarget()
                .path(path)
                .request(MediaType.TEXT_PLAIN)
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));

        return response.readEntity(Long.class);
    }

    @Override
    protected String getBaseUrl() {
        return baseUrl;
    }

}
