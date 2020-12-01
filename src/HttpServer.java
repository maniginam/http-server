import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpServer {
    private final HttpParser parser;
    private int port;
    private String root;
    public String bodyMessage;
    public String fields;
    public byte[] response;
    public HttpResponder responder;
    public byte[] bodyBytes;
    private String header;

    public HttpServer(int port, String root) {
        this.port = port;
        this.root = root;
        parser = new HttpParser();
        responder = new HttpResponder();
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

    public void submitRequest(String msg) throws ExceptionInfo, IOException {
        String request = msg.split(" ")[1];
        if(!request.contains("/favicon.ico")) {
            if (request.matches("HTTP/1.1") || request.matches("/")) {
                getDefaultRoot(request);
            } else if (request.matches("/listing")) {
                getLinks("listing", "");
            } else if (request.matches("/listing/img")) {
                getLinks("listing", "img");
            } else if (request.contains(".")) {
                analyzeRequest(request);
            } else {
                throw new ExceptionInfo(msg, "The page you are looking for was not found, but the Sun is 93 million miles away!");
            }
        }
    }

    private void getDefaultRoot(String request) throws IOException {
        bodyMessage = getFileMessage("index.html");
        fields = "";
        parser.setStatus("", 200, "OK");
        parser.setHeaderField(bodyMessage, bodyBytes, fields);
        parser.getHeader();
        response = responder.respond(parser.getHeader(), bodyMessage.getBytes());
    }


    private String getFileMessage(String fileName) throws IOException {
        Path path = Path.of(root + "/" + fileName);
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    public byte[] convertFiletoBytes(String fileName) throws IOException {
        File file = new File(root + "/img/" + fileName);
        FileInputStream input = new FileInputStream(file);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.readAllBytes());

        return inputStream.readAllBytes();
    }

    private void analyzeRequest(String request) throws IOException {
        String[] requestBreakdown = request.split("[/.]");
        int requestIndex = requestBreakdown.length - 1;
        String targetType = requestBreakdown[requestIndex];
        String targetName = requestBreakdown[requestIndex - 1];
        String name = targetName + "." + targetType;

        if (targetType.matches("html")) {
            bodyMessage = getFileMessage(name);
            fields = "Content-Type: " + "text/html";
            createHeader(200, fields);
            response = responder.respond(header, bodyMessage.getBytes());
        } else {
            bodyBytes = convertFiletoBytes(name);

            if(targetType.contains("jpg"))
                targetType = "jpeg";
            fields = "Content-Type: " + "image/" + targetType + ", " +
                    "Content-Disposition: inline; name=\"" + targetName + "\"; filename=\"" + name + "\", ";

            createHeader(200, fields);

            response = responder.respond(parser.getHeader(), bodyBytes);
        }
    }

    private void createHeader(int statusCode, String fields) {
        parser.setStatus("", statusCode, "OK");
        parser.setHeaderField(bodyMessage, bodyBytes, fields);
        header = parser.getHeader();
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
        parser.setStatus("", 200, "OK");
        parser.setHeaderField(bodyMessage, bodyBytes, fields);
        response = responder.respond(parser.getHeader(), bodyMessage.getBytes());
    }

    private String getLinkName(String name, String child) {
        if (child.isBlank()) {
            return name;
        } else {
            return child + "/" + name;
        }
    }
}

