import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {
    private final HttpParser parser;
    private final PostFormHandler poster;
    private String root;
    public String responseBodyMessage;
    public String fields;
    public byte[] response;
    public byte[] responseBodyBytes;
    private String responseHeader;
    private Map<String, String> contentTypes = new HashMap<String, String>();
    private int numberOfRequestParts;
    private String requestHeader;
    private byte[] requestBody;
    private int multiPartRequestCounter = 0;
    private RequestParser requestParser;
    private String fileName;


    public HttpServer(String root) {
        contentTypes.put("html", "text");
        contentTypes.put("pdf", "application");
        contentTypes.put("jpeg", "image");
        contentTypes.put("png", "image");
        this.root = root;
        parser = new HttpParser();
        requestParser = new RequestParser();
        poster = new PostFormHandler();
    }

    public void submitRequest(String requestHeader, byte[] requestBody) throws ExceptionInfo, IOException {
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
        responseHeader = null;
        responseBodyMessage = null;
        responseBodyBytes = null;

        String method = requestHeader.split(" ")[0];
        String target = requestHeader.split(" ")[1];

        if (!requestHeader.contains("favicon")) {
            if (method.contains("GET"))
                respondToGET(requestHeader, target);
            else if (method.contains("POST")) {
                respondToPOST(requestHeader, requestBody);
            }
        }
    }

    private void respondToPOST(String request, byte[] requestBody) throws IOException {
        if (requestBody != null) {
            if (poster.getFileName() != null) {
                fileName = root + "/img/" + poster.getFileName();
                File file = new File(fileName);
                OutputStream output = new FileOutputStream(file);
                output.write(requestBody);
                output.close();
            }
        }
        poster.handle(request, requestBody);
        responseBodyMessage = poster.getResponseBody();
        fields = "";
        setResponseHeader(200, fields);
        setResponse();
        System.out.println("responseBodyMessage = " + responseBodyMessage);
//        } else {
//            fields = "";
//            setHeader(200, fields);
//            setResponse();
//        }
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
        } else if (target.contains("/ping")) {
            respondToPing(target);
        } else
            throw new ExceptionInfo("<h1>The page you are looking for is 93 million miles away!</h1>");
    }

    private void respondToPing(String target) throws IOException {
        Ping ping = new Ping(target);
        ping.respond();
        responseBodyMessage = ping.getResponse();
        fields = "";
        setResponseHeader(200, fields);
        setResponse();
    }

    private void getFormResponse(String target) throws IOException {
        responseBodyMessage = new FormInputHandler().handle(target);
        fields = "";
        setResponseHeader(200, fields);
        setResponse();
    }

    private void getDefaultRoot() throws IOException {
        setFileMessage("index.html");
        fields = "";
        setResponseHeader(200, fields);
        setResponse();
    }

    private void getLinks(String parent, String child) throws IOException {
        File directory = new File(root + "/" + child);
        String[] names;
        File[] files = directory.listFiles();
        names = directory.list();
        String[] links = new String[files.length];
        int i = 0;
        String linkMsg = "";
        for (File file : files) {
            String name = names[i];
            String linkName = getLinkName(name, child);
            if (file.isFile()) {
                links[i] = "<li><a href=\"/" + linkName + "\">" + name + "</a></li>";
            } else if (file.isDirectory()) {
                links[i] = "<li><a href=\"/" + parent + "/" + linkName + "\">" + name + "</a></li>";
            }
            linkMsg = linkMsg + links[i];
            i++;

        }
        responseBodyMessage = "<ul>" + linkMsg + "</ul>";
        fields = "Content-Type: text/html";
        setResponseHeader(200, fields);
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
        response = null;
        responseBodyBytes = null;
        responseBodyMessage = null;

        String[] requestBreakdown = target.split("[/.]");
        int requestIndex = requestBreakdown.length - 1;
        String targetType = requestBreakdown[requestIndex];
        String targetName = requestBreakdown[requestIndex - 1];
        String name = targetName + "." + targetType;

        if (targetType.matches("html")) {
            setFileMessage(name);
            fields = "Content-Type: " + contentTypes.get(targetType) + "/" + targetType;

            setResponseHeader(200, fields);
            setResponse();

        } else if (targetType.matches("pdf")) {
            convertFiletoBytes(name);
            fields = "Content-Type: " + contentTypes.get(targetType) + "/" + targetType + ", " +
                    "Content-Disposition: inline; name=\"" + targetName + "\"; filename=\"" + name + "\", ";

            setResponseHeader(200, fields);
            setResponse();

        } else {
            convertFiletoBytes("img/" + name);

            if (targetType.contains("jpg"))
                targetType = "jpeg";
            fields = "Content-Type: " + contentTypes.get(targetType) + "/" + targetType + ", " +
                    "Content-Disposition: inline; name=\"" + targetName + "\"; filename=\"" + name + "\", ";

            setResponseHeader(200, fields);
            setResponse();
        }
    }

    private void setFileMessage(String fileName) throws IOException {
        String pathName = root + "/" + fileName;
        Path path = Path.of(pathName);
        responseBodyMessage = Files.readString(path, StandardCharsets.UTF_8);
    }

    public void convertFiletoBytes(String fileName) throws IOException {
        File file = new File(root + "/" + fileName);
        FileInputStream input = new FileInputStream(file);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.readAllBytes());

        responseBodyBytes = inputStream.readAllBytes();
    }

    private void setResponseHeader(int statusCode, String fields) {
        parser.setStatus(statusCode, "OK");
        parser.setHeaderField(responseBodyMessage, responseBodyBytes, fields);
        responseHeader = parser.getHeader();
    }

    private void setResponse() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] headerBytes = parser.getHeader().getBytes();

        output.write(headerBytes);

        if (responseBodyMessage != null) {
            output.write(responseBodyMessage.getBytes());
        }

        if (responseBodyBytes != null) {
            output.write(responseBodyBytes);
        }

//        output.write("\r\n\r\n".getBytes());

        response = output.toByteArray();
        output.close();
    }

    public byte[] getResponse() {
        return response;
    }

    public String getFields() {
        return fields;
    }

    public String getResponseBodyMessage() {
        return responseBodyMessage;
    }

    public byte[] getResponseBodyBytes() {
        return responseBodyBytes;
    }

    public String getResponseHeader() {
        return responseHeader;
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

    public RequestParser getRequestParser() {
        return requestParser;
    }
}

