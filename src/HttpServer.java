import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {
    private final HttpParser parser;
    private String root;
    public String bodyMessage;
    public String fields;
    public byte[] response;
    public byte[] bodyBytes;
    private String header;
    int bodyLength;
    private Map<String, String> contentTypes = new HashMap<String, String>();
    private int numberOfRequestParts;
    private String requestHeader;
    private byte[] requestBody;


    public HttpServer(String root) {
        contentTypes.put("html", "text");
        contentTypes.put("pdf", "application");
        contentTypes.put("jpeg", "image");
        contentTypes.put("png", "image");
        this.root = root;
        parser = new HttpParser();
    }

    public void submitRequest(byte[] request) throws ExceptionInfo, IOException {
        bodyMessage = null;
        bodyBytes = null;
        requestHeader = splitRequest(request)[0];

        if (numberOfRequestParts > 1) {
            bodyLength = findBodyLength(requestHeader);
            setRequestBody(request);
        }

        if (!requestHeader.contains("favicon")) {
            String method = requestHeader.split(" ")[0];
            String target = requestHeader.split(" ")[1];
            if (method.contains("GET"))
                respondToGET(requestHeader, target);
            else if (method.contains("POST")) {
                respondToPOST(requestHeader, requestBody);
            }
        }
    }

    public String[] splitRequest(byte[] request) {
        String requestAsString = new String(request, StandardCharsets.UTF_8);
        String[] splitRequest = requestAsString.split("\r\n\r\n");
        numberOfRequestParts = splitRequest.length;
        return splitRequest;
    }

    private void setRequestBody(byte[] request) {
        if (bodyLength > 0) {
            ByteBuffer buffer = ByteBuffer.wrap(request);
            requestBody = new byte[bodyLength];
            buffer.get(requestBody, 0, bodyLength);

        }
    }

    private int findBodyLength(String header) {
        String[] splitHeader = header.split("\r\n");

        for (String entity : splitHeader) {
            if (entity.contains("Content-Length")) {
                bodyLength = Integer.parseInt(entity.split(" ")[1]);
            }
        }
        return bodyLength;
    }

    private void respondToPOST(String request, byte[] requestBody) throws IOException {
        bodyMessage = new PostFormHandler().handle(request, requestBody);
        fields = "";
        setHeader(200, fields);
        setResponse();
    }


    private void respondToGET(String msg, String target) throws IOException, ExceptionInfo {
        if (target.matches("HTTP/1.1") || target.matches("/")) {
            getDefaultRoot();
        } else if (target.matches("/listing")) {
            getLinks("listing", "");
        } else if (target.matches("/listing/img")) {
            getLinks("listing", "img");
        } else if (target.contains(".")) {
            analyzeTarget(target);
        } else if (target.contains("form?")) {
            getFormResponse(target);
        } else
            throw new ExceptionInfo(msg, "<h1>The page you are looking for is 93 million miles away!</h1>");
    }

    private void getFormResponse(String target) throws IOException {
        bodyMessage = new FormInputHandler().handle(target);
        fields = "";
        setHeader(200, fields);
        setResponse();
    }


    private void getDefaultRoot() throws IOException {
        getFileMessage("index.html");
        fields = "";
        setHeader(200, fields);
        setResponse();
    }

    private void getLinks(String parent, String child) throws IOException {
        File directory = new File(root + "/" + child);
        String[] names;
        File[] files = directory.listFiles();
        names = directory.list();
        String[] links = new String[files.length];
        int i = 0;
        String linkMsg = null;
        for (File file : files) {
            String name = names[i];
            String linkName = getLinkName(name, child);
            if (file.isFile()) {
                links[i] = "<li><a href=\"/" + linkName + "\">" + name + "</a></li>";
            } else if (file.isDirectory()) {
                links[i] = "<li><a href=\"/" + parent + "/" + linkName + "\">" + name + "</a></li>";
            }

            if (linkMsg == null)
                linkMsg = links[i];
            linkMsg = linkMsg + links[i];
            i++;
        }
        bodyMessage = "<ul>" + linkMsg + "</ul>";
        fields = "Content-Type: text/html";
        setHeader(200, fields);
        setResponse();
    }

    private String getLinkName(String name, String child) {
        if (child.isBlank()) {
            return name;
        } else {
            return child + "/" + name;
        }
    }

    private void analyzeTarget(String target) throws IOException {
        String[] requestBreakdown = target.split("[/.]");
        int requestIndex = requestBreakdown.length - 1;
        String targetType = requestBreakdown[requestIndex];
        String targetName = requestBreakdown[requestIndex - 1];
        String name = targetName + "." + targetType;

        if (targetType.matches("html")) {
            bodyMessage = getFileMessage(name);
            fields = "Content-Type: " + contentTypes.get(targetType) + "/" + targetType;

            setHeader(200, fields);
            setResponse();

        } else if (targetType.matches("pdf")) {
            bodyBytes = convertFiletoBytes(name);
            fields = "Content-Type: " + contentTypes.get(targetType) + "/" + targetType + ", " +
                    "Content-Disposition: inline; name=\"" + targetName + "\"; filename=\"" + name + "\", ";

            setHeader(200, fields);
            setResponse();

        } else {
            bodyBytes = convertFiletoBytes("img/" + name);

            if (targetType.contains("jpg"))
                targetType = "jpeg";
            fields = "Content-Type: " + contentTypes.get(targetType) + "/" + targetType + ", " +
                    "Content-Disposition: inline; name=\"" + targetName + "\"; filename=\"" + name + "\", ";

            setHeader(200, fields);
            setResponse();
        }
    }

    private String getFileMessage(String fileName) throws IOException {
        String pathName = root + "/" + fileName;
        Path path = Path.of(pathName);
        bodyMessage = Files.readString(path, StandardCharsets.UTF_8);

        return bodyMessage;
    }

    public byte[] convertFiletoBytes(String fileName) throws IOException {
        File file = new File(root + "/" + fileName);
        FileInputStream input = new FileInputStream(file);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.readAllBytes());

        return inputStream.readAllBytes();
    }

    private void setHeader(int statusCode, String fields) {
        parser.setStatus(statusCode, "OK");
        parser.setHeaderField(bodyMessage, bodyBytes, fields);
        header = parser.getHeader();
    }

    private void setResponse() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte headerBytes[] = parser.getHeader().getBytes();
        output.write(headerBytes);

        if (bodyMessage != null) {
            output.write(bodyMessage.getBytes());
        }

        if (bodyBytes != null) {
            output.write(bodyBytes);
        }

        output.write("\r\n".getBytes());

        response = output.toByteArray();
    }

    public byte[] getResponse() {
        return response;
    }

    public String getFields() {
        return fields;
    }

    public String getBodyMessage() {
        return bodyMessage;
    }

    public byte[] getBodyBytes() {
        return bodyBytes;
    }

    public String getHeader() {
        return header;
    }

    public int getNumberOfRequestParts() {
        return numberOfRequestParts;
    }

    public String getRequestHeader() {
        return requestHeader;
    }

    public byte[] getRequestBody() {
        return requestBody;
    }
}

