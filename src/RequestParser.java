import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RequestParser {
    public int contentLength;
    private String header;
    private byte[] body;
    private int part = 0;

    public void interpretHeader(byte[] input) throws IOException {
        contentLength = -1;
        header = new String(input, StandardCharsets.UTF_8);

        if (header.contains("\r\n\r\n")) {
            contentLength = findContentLength(header);
            if (header.contains("multipart/form-data")) {
                String headers[] = header.split("------");
                if (headers.length > 1) {
                    if (headers[1].contains("\r\n\r\n")) {
                        contentLength = calcBodySize(headers[1]);
                    } else {
                        contentLength = -1;
                    }
                } else {
                    contentLength = -1;
                }
                System.out.println("header = " + header);
                System.out.println("contentLength = " + contentLength);
            } else {
                findContentLength(header);
                if (contentLength == -1) {
                    contentLength = 0;
                }
            }
        }
    }

    private int calcBodySize(String header) {
        String fileName = "";
        String[] header2Parts = header.split("\r\n");
        if (header2Parts.length > 2) {
            String disposition = header2Parts[1];
            String[] dispositions = disposition.split("; ");
            fileName = dispositions[2].split("=")[1];
            fileName.replaceAll("\"", "");
        }
//        return contentLength - 172 - fileName.length();
    return contentLength;
    }

    private void resetAll () {
            contentLength = -1;
            header = null;
            body = null;
        }

        public int findContentLength(String header) throws IOException {
            String[] splitHeader = header.split("\r\n");
            List<String> entityList = new ArrayList<>();

            for (String line : header.split("\r\n")) {
                if (!(line.isBlank() || line.length() < 3))
                    entityList.add(line);
            }

            for (String entity : entityList) {
                if (entity.contains("Content-Length")) {
                    contentLength = Integer.parseInt(entity.split(" ")[1]);
                }
            }
            return contentLength;
        }

        private void setContentLength(int contentLength){
            this.contentLength = contentLength;
        }

        private void setBody ( byte[] request){
            if (contentLength > 0) {
                ByteBuffer buffer = ByteBuffer.wrap(request);
                body = new byte[contentLength];
                buffer.get(body, 0, contentLength);

            }
        }

        public byte[] getBody () {
            return body;
        }

        public String getHeader () {
            return header;
        }


        public int getContentLength() {
            return contentLength;
        }
    }
