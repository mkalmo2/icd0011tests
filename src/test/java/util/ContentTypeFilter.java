package util;

import jakarta.annotation.Priority;
import jakarta.ws.rs.client.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Priority(0)
public class ContentTypeFilter implements ClientResponseFilter {

    @Override
    public void filter(ClientRequestContext request,
                       ClientResponseContext response) {

        String accept = getFirst(request.getHeaders().get("Accept"));

        if (!accept.contains("json")) {
            return;
        }

        String contentType = getFirst(response.getHeaders().get("Content-Type"));

        if (!List.of(200, 201, 400).contains(response.getStatus())) {
            throw new RuntimeException("Unexpected Http status: " + response.getStatus());
        } else if (contentType.isEmpty()) {
            throw new RuntimeException("Content-Type is missing");
        } else if (!contentType.contains("json")) {
            throw new RuntimeException("Unexpected Content-Type: " + contentType);
        }
    }

    private String getFirst(List<? super String> list) {
        return Optional.ofNullable(list)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .map(String::valueOf)
                .findFirst()
                .orElse("");
    }
}
