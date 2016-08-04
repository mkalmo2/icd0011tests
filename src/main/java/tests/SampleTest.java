package tests;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;


public class SampleTest {

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
        customer.getPhones().add(new Phone(2L, "123", "phone_type.fixed"));

        Response response = target.path("customers").request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(customer, MediaType.APPLICATION_JSON));

        System.out.println(response.getStatus());
        System.out.println(response.readEntity(String.class));
    }

}
