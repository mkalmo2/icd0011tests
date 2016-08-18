package tests;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;
import tests.model.Customer;
import tests.model.Phone;

import java.util.Arrays;


public class Hw6 {

    private Client restClient = ClientBuilder.newClient();
    private WebTarget target = restClient.target("http://localhost:8080/api/");

    @Test
    public void getCustomers() {
        Response response = target.path("customers").request(MediaType.APPLICATION_JSON)
                .get();

        System.out.println(response.getStatus());
        System.out.println(response.readEntity(String.class));
    }

    @Test
    public void saveCustomer() {
        Customer customer = new Customer(1L, "J", null, "customer_type.private", "A1");
        customer.setPhones(Arrays.asList(new Phone("phone_type.fixed", "123")));

        Response response = target.path("customers").request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(customer, MediaType.APPLICATION_JSON));

        System.out.println(response.getStatus());
        System.out.println(response.readEntity(String.class));
    }

}
