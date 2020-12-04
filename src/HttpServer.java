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
    public String responseBody;
    public String fields;
    public byte[] response;
    public byte[] bodyBytes;
    private String header;
    int bodyLength;
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
        responseBody = null;
        bodyBytes = null;

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
        multiPartRequestCounter = multiPartRequestCounter + 1;
        poster.handle(request, multiPartRequestCounter);

        if (multiPartRequestCounter == 3) {
            if(poster.getFileName() != null) {
                fileName = root + "/img/" + poster.getFileName();
                File file = new File(fileName);
//                FileInputStream inputStream = new FileInputStream(file);
//
//                ByteArrayInputStream input = new ByteArrayInputStream(inputStream.readNBytes(bodyLength));
                System.out.println("fileName = " + fileName);
                OutputStream output = new FileOutputStream(file);
                output.write(requestBody);
                output.close();
            }
            responseBody = poster.getResponseBody();
            fields = "";
            setHeader(200, fields);
            setResponse();
        } else if(multiPartRequestCounter < 3)
            poster.handle(request, multiPartRequestCounter);
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
        responseBody = new FormInputHandler().handle(target);
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
        responseBody = "<ul>" + linkMsg + "</ul>";
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
            responseBody = getFileMessage(name);
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
        responseBody = Files.readString(path, StandardCharsets.UTF_8);

        return responseBody;
    }

    public byte[] convertFiletoBytes(String fileName) throws IOException {
        File file = new File(root + "/" + fileName);
        FileInputStream input = new FileInputStream(file);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.readAllBytes());

        return inputStream.readAllBytes();
    }

    private void setHeader(int statusCode, String fields) {
        parser.setStatus(statusCode, "OK");
        parser.setHeaderField(responseBody, bodyBytes, fields);
        header = parser.getHeader();
    }

    private void setResponse() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte headerBytes[] = parser.getHeader().getBytes();
        output.write(headerBytes);

        if (responseBody != null) {
            output.write(responseBody.getBytes());
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

    public String getResponseBody() {
        System.out.println("bodyMessage = " + responseBody);
        return responseBody;
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

    public RequestParser getRequestParser() {
        return requestParser;
    }
}

