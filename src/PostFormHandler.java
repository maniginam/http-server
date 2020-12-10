import java.util.HashMap;
import java.util.Map;

public class PostFormHandler implements FormHandler {
    private String formName;
    private HashMap<String, String> entities;
    private HashMap<String, String> entityNames;
    private String fileName;
    private String responseBody;

    public PostFormHandler() {
        entityNames = new HashMap<>();
        entityNames.put("Content:", "file name:");
        entityNames.put("Content-Length:", "file size:");
        entityNames.put("Content-Type:", "content type:");
        entities = new HashMap<String, String>();
    }

    public void respond (Map<String, String> requestHeader, byte[] body) {
        setFormName("POST Form");
        prepareBodyMessage(requestHeader);
        setResponseBody();
    }

    public void setFormName(String name) {
        formName = "<html>\r\n" +
                "<h2>" + name + "</h2>\r\n";
    }

    private void prepareBodyMessage(Map<String, String> requestHeader) {
        String disposition = requestHeader.get("Content-Disposition");
        String fileName = disposition.split("filename=")[1].replaceAll("\"", "");
        entities.put("file name:", fileName);
        entities.put("file size:", requestHeader.get("bodySize"));
        entities.put("content type:", "application/octet-stream");
    }

    public void setResponseBody() {
        responseBody = formName;
        for (String entity : entities.keySet()) {
            responseBody = responseBody + "<li>" + entity + " " +
                    entities.get(entity) + "</li>\r\n";
        }
        responseBody = responseBody + "</html>\r\n";
    }

    public String getFileName() {
        return fileName;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
