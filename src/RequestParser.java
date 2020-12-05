import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RequestParser {
    private int numberOfRequestParts;
    public int bodySize;
    private String header;
    private byte[] body;
    private String method;
    private String target;

    public void interpretHeader(byte[] input) {
        resetAll();
        header = new String(input, StandardCharsets.UTF_8);
        if (header.contains("\r\n\r\n")) {
            bodySize = findBodySize(header);
            if(bodySize == -1) {
                bodySize = 0;
            }
        }
    }

    private void resetAll() {
        bodySize = -1;
        header = null;
        body = null;
    }

    public int findBodySize(String header) {
        String[] splitHeader = header.split("\r\n");
        List<String> entityList = new ArrayList<>();

        for (String line : header.split("\r\n")) {
            if(!(line.isBlank() || line.length() < 3))
                entityList.add(line);
        }

        entityList.remove(0);

        for (String entity : splitHeader) {
            if (entity.contains("Content-Length")) {
                bodySize = Integer.parseInt(entity.split(" ")[1]);
            }
        }

        return bodySize;
    }

    private void setBodySize(int bodySize) {
        this.bodySize = bodySize;
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
