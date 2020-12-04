import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class RequestParser {
    private int numberOfRequestParts;
    private int bodySize = -1;
    private String header;
    private byte[] body;
    private String method;
    private String target;

    public void interpretHeader(byte[] input) {
        header = new String(input, StandardCharsets.UTF_8);
        if (header.contains("\r\n\r\n")) {
            bodySize = findBodySize(header);
        }
    }

    public String[] splitRequest(byte[] request) {
        String requestAsString = new String(request, StandardCharsets.UTF_8);
        String[] splitRequest = requestAsString.split("\r\n\r\n");

        header = splitRequest[0];
        method = header.split(" ")[0];
        target = header.split(" ")[1];
        numberOfRequestParts = splitRequest.length;

        if (numberOfRequestParts > 1) {
            bodySize = findBodySize(header);
            setBody(request);
        }
        return splitRequest;
    }

    public int findBodySize(String header) {
        String[] splitHeader = header.split("\r\n");

        for (String entity : splitHeader) {
            if (entity.contains("Content-Length:")) {
                bodySize = Integer.parseInt(entity.split(" ")[1]);
            } else bodySize = 0;
        }

        return bodySize;
    }

    private void setBody(byte[] request) {
        if (bodySize > 0) {
            ByteBuffer buffer = ByteBuffer.wrap(request);
            body = new byte[bodySize];
            buffer.get(body, 0, bodySize);

        }
    }

    public int getPartsCount() {
        return numberOfRequestParts;
    }

    public byte[] getBody() {
        return body;
    }

    public String getHeader() {
        return header;
    }

    public String getMethod() {
        return method;
    }

    public String getTarget() {
        return target;
    }

    public int getBodySize() {
        return bodySize;
    }
}
