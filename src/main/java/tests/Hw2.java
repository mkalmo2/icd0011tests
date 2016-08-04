package tests;

import org.junit.Test;

import javax.ws.rs.client.*;
import javax.ws.rs.core.*;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


public class Hw2 {

    private static final String BASE_URL = "http://localhost:8080/";
    private static final Client CLIENT = ClientBuilder.newClient();

    // logout

    @Test
    public void insertFromForm() {
        Browser browser = new Browser(getTarget());

        browser.postForm("insert-from-form", getForm("name", "Jack"));
        browser.postForm("insert-from-form", getForm("name", "Jill"));

        assertThat(browser.get("show-names"), is("Jack, Jill"));
    }

    @Test
    public void insertFromRaw() {
        Browser browser = new Browser(getTarget());

        browser.postRaw("insert-from-raw", "Jill, Jack");
        browser.postRaw("insert-from-raw", "John");

        assertThat(browser.get("show-names"), is("Jill, Jack, John"));
    }

    private WebTarget getTarget() {
        return CLIENT.target(BASE_URL);
    }

    private Form getForm(String name, String value) {
        Form form = new Form();
        form.param(name, value);
        return form;
    }

    private static class Browser {
        Map<String, NewCookie> cookies = new HashMap<>();
        private WebTarget target;

        public Browser(WebTarget target) {
            this.target = target;
        }

        public String get(String path) {
            Response response = getRequest(path).get();
            cookies.putAll(response.getCookies());
            return response.readEntity(String.class);
        }

        public void postForm(String path, Form form) {
            Response response = getRequest(path)
                    .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
            cookies.putAll(response.getCookies());
        }

        public void postRaw(String path, String data) {
            Response response = getRequest(path)
                    .post(Entity.entity(data, MediaType.TEXT_PLAIN));
            cookies.putAll(response.getCookies());
        }

        private Invocation.Builder getRequest(String path) {
            return target
                    .path(path)
                    .request()
                    .cookie(cookies.get("JSESSIONID"));
        }
    }

}
