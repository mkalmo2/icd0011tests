package tests;

import lombok.AllArgsConstructor;
import lombok.ToString;
import tests.model.Customer;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.*;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

public abstract class AbstractHw {

    protected abstract String getBaseUrl();

    protected static final Client client = getClient();

    private static Client getClient() {
        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[] {getX509TrustManager()}, new SecureRandom());
            return ClientBuilder.newBuilder().sslContext(sslcontext).hostnameVerifier((s1, s2) -> true).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static X509TrustManager getX509TrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
                    throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
                    throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    public WebTarget getTarget() {
        return client.target(getBaseUrl());
    }

    protected Customer getCustomer(String firstName, String lastName, String code) {
        return new Customer(null, firstName, lastName, null, code);
    }

    protected Customer getCustomer(String firstName) {
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        return customer;
    }

    protected List<Customer> getList(String path) {
        return getTarget()
                .path(path)
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Customer>>() {});
    }

    protected Customer getOne(String path, Parameter ... parameters) {
        return getOne(path, Customer.class, parameters);
    }

    protected <T> T getOne(String path, Class<T> clazz, Parameter ... parameters) {
        WebTarget target = getTarget().path(path);

        for (Parameter p : parameters) {
            target = target.queryParam(p.key, p.value);
        }

        return target
                .request(MediaType.APPLICATION_JSON)
                .get(clazz);
    }

    protected Parameter param(String key, Object value) {
        return new Parameter(key, String.valueOf(value));
    }

    @ToString
    @AllArgsConstructor
    public static class Parameter {
        private String key;
        private String value;
    }

    protected void postJson(String path, Customer data) {
        getTarget()
            .path(path)
            .request()
            .post(Entity.entity(data, MediaType.APPLICATION_JSON));
    }

    protected void delete(String path, Parameter ... parameters) {
        WebTarget target = getTarget().path(path);

        for (Parameter p : parameters) {
            target = target.queryParam(p.key, p.value);
        }

        target.request().delete();
    }
}
