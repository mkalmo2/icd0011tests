package util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LoggingStreamWrapper extends OutputStream {
    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private OutputStream stream;

    public LoggingStreamWrapper(OutputStream stream) {
        this.stream = stream;
    }

    public String getBuffer() {
        return buffer.toString();
    }

    @Override
    public void write(int b) throws IOException {
        stream.write(b);
        buffer.write(b);
    }

    public void close() throws IOException {
        stream.close();
    }
}
