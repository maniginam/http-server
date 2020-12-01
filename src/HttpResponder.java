import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HttpResponder {
    public byte[] response;
    public HttpResponder() {

    }

    public byte[] respond(String header, byte[] body) throws IOException {
        byte headerBytes[] = header.getBytes();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(headerBytes);
        output.write(body);

        byte response[] = output.toByteArray();
        return response;
    }
}
