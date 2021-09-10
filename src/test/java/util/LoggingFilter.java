package util;

import jakarta.annotation.Priority;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import java.io.*;

@Priority(1)
public class LoggingFilter implements ClientRequestFilter, ClientResponseFilter {
    private boolean isDebug;
    private LoggingStreamWrapper outputStream;

    public LoggingFilter(boolean isDebug) {
        this.isDebug = isDebug;
    }

    @Override
    public void filter(ClientRequestContext context) {
        if (!isDebug) {
            return;
        }

        System.out.println("-----------------------------------------------------------");
        System.out.println("URL: " + context.getUri());
        System.out.println("METHOD: " + context.getMethod());
        System.out.println("HEADERS: " + context.getStringHeaders());
        outputStream = new LoggingStreamWrapper(context.getEntityStream());
        context.setEntityStream(outputStream);
    }

    @Override
    public void filter(ClientRequestContext clientRequestContext,
                       ClientResponseContext response) throws IOException {

        if (!isDebug) {
            return;
        }

        String responseData = readInputStream(response.getEntityStream());
        response.setEntityStream(new ByteArrayInputStream(responseData.getBytes()));

        System.out.println("DATA: " + outputStream.getBuffer());
        System.out.println("RESPONSE HEADERS: " + response.getHeaders());
        System.out.println("RESPONSE CODE: " + response.getStatus());
        System.out.println("RESPONSE DATA: " + responseData);
        System.out.println("-----------------------------------------------------------");
    }

    private static String readInputStream(InputStream is) throws IOException {
        BufferedReader buffer = new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();
        while (true) {
            int c = buffer.read();
            if (c == -1) {
                break;
            }
            sb.append(Character.toChars(c));
        }

        return sb.toString();
    }
}
