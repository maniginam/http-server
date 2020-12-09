import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class GETResponder {
    private Map<String, String> contentTypes = new HashMap<String, String>();
    private String response;
    private String fields;
    private String responseBodyMessage;
    private byte[] responseBodyBytes;
    private final String root;
    private final String target;

    public GETResponder(String root, String target) {
        this.root = root;
        this.target = target;
        contentTypes.put("html", "text");
        contentTypes.put("pdf", "application");
        contentTypes.put("jpeg", "image");
        contentTypes.put("png", "image");
    }

    public String respond() throws IOException, ExceptionInfo {
        if (target.matches("HTTP/1.1") || target.matches("/")) {
            defaultRoot();
        } else if (target.contains("/listing")) {
            if (target.contains("/img"))
                getLinks("listing", "img");
            else getLinks("listing", "");
        } else if (target.contains(".")) {
            analyzeTarget(target);
        } else if (target.contains("form?")) {
            getFormResponse(target);
        } else if (target.contains("/ping")) {
            respondToPing(target);
        } else
            throw new ExceptionInfo("<h1>The page you are looking for is 93 million miles away!</h1>");
        return response;
    }

    private void respondToPing(String target) throws IOException {
        Ping ping = new Ping(target);
        ping.respond();
        responseBodyMessage = ping.getResponse();
        fields = "";
    }

    private void getFormResponse(String target) throws IOException {
        responseBodyMessage = new FormInputHandler().handle(target);
        fields = "";
    }

    private void defaultRoot() throws IOException {
        setFileMessage("index.html");
        fields = "";
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

        } else if (targetType.matches("pdf")) {
            convertFiletoBytes(name);
            fields = "Content-Type: " + contentTypes.get(targetType) + "/" + targetType + ", " +
                    "Content-Disposition: inline; name=\"" + targetName + "\"; filename=\"" + name + "\", ";
        } else {
            convertFiletoBytes("img/" + name);

            if (targetType.contains("jpg"))
                targetType = "jpeg";
            fields = "Content-Type: " + contentTypes.get(targetType) + "/" + targetType + ", " +
                    "Content-Disposition: inline; name=\"" + targetName + "\"; filename=\"" + name + "\", ";
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

    public String getFields() {
        return fields;
    }
}
