import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public final class HttpResponse extends Object {


    public HttpResponse(String header, byte[] body) throws IOException {
        byte headerBytes[] = header.getBytes();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(headerBytes);
        output.write(body);

        byte response[] = output.toByteArray();

    }
}


