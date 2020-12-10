import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {
    private String header;
    private byte[] body;
    public int contentLength;
    private String boundary;
    private String bodyHeader;
    private HashMap<String, String> headerMap;


    public void interpretHeader(byte[] input) throws IOException {
        contentLength = -1;
        header = new String(input, StandardCharsets.UTF_8);

        if (header.endsWith("\r\n\r\n")) {
            headerMap = new HashMap<String, String>();
            for (String entity : header.split("\r\n")) {
                if (entity.contains("HTTP/")) {
                    headerMap.put("status", entity);
                } else if(entity.contains(": ")){
                    String[] parts = entity.split(": ");
                    headerMap.put(parts[0], parts[1]);
                }

                contentLength = findContentLength(headerMap);
                if (contentLength == -1) {
                    contentLength = 0;
                }
            }
        }
    }

    public void interpretBody(byte[] requestBody) throws IOException {
        String boundary = setBoundary();
        int boundaryByteLength = boundary.length();

        int remainingBytes = contentLength;
        ByteArrayInputStream input = new ByteArrayInputStream(requestBody);
        BufferedInputStream buffed = new BufferedInputStream(input);
        ByteArrayOutputStream outputHeader = new ByteArrayOutputStream();

        bodyHeader = "";
        while (!(bodyHeader.endsWith("\r\n\r\n"))) {
            int b = buffed.read();
            outputHeader.write(b);
            remainingBytes--;
            byte[] bodyHeadBytes = outputHeader.toByteArray();
            bodyHeader = new String(bodyHeadBytes, StandardCharsets.UTF_8);
        }

        String[] bodyEntities = bodyHeader.split("\r\n");
        for (String entity : bodyEntities) {
            if (entity.contains(": ")) {
                String[] parts = entity.split(": ");
                headerMap.put(parts[0], parts[1]);
            }
        }

        ByteArrayOutputStream outputBody = new ByteArrayOutputStream();
        outputBody.write(buffed.readNBytes(remainingBytes - boundaryByteLength));

        body = outputBody.toByteArray();
        headerMap.put("bodySize", String.valueOf(body.length - "--\r\n\r\n".length()));
    }

    private String setBoundary() {
        String line = headerMap.get("Content-Type");
        boundary = line.split("boundary=")[1];
        return "--" + boundary;
    }

    public int findContentLength(HashMap<String, String> headerMap) throws IOException {
        if (headerMap.containsKey("Content-Length")) {
            contentLength = Integer.parseInt(headerMap.get("Content-Length"));
        }
        return contentLength;
    }

    public byte[] getBody() {
        return body;
    }

    public String getHeader() {
        return header;
    }

    public int getContentLength() {
        return contentLength;
    }

    public Map getHeaderMap() {
        return headerMap;
    }
}
