package util;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import java.io.IOException;
import java.util.List;

public class ContentTypeFilter implements ClientResponseFilter {

    @Override
    public void filter(ClientRequestContext clientRequestContext,
                       ClientResponseContext response) throws IOException {

        List<String> values = response.getHeaders().get("Content-Type");

        if (values == null || values.isEmpty()) {
            throw new RuntimeException("Content-Type is missing");
        }

        String contentType = values.get(0);

        if (!contentType.toLowerCase().contains("json")) {
            throw new RuntimeException("Unexpected Content-Type: " + contentType);
        }
    }
}
