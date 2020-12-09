import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    }

    private void resetAll() {
        formName = null;
        entities = null;
        fileName = null;
        responseBody = null;
    }

    public void handle(String requestHeader, byte[] body) {
        resetAll();
        List<String> entityList = new ArrayList<>();

        for (String line : requestHeader.split("\r\n")) {
            entityList.add(line);
        }

        entityList.remove(0);
        entities = new HashMap<>();

        setFormName("POST Form");
        createBoxMap(entityList, body);
        setResponseBody();
    }

    public void setFormName(String name) {
        formName = "<html>\r\n" +
                "<h2>" + name + "</h2>\r\n";
    }

    private void createBoxMap(List<String> entityList, byte[] body) {
        for (String line : entityList) {
            String[] inputArray = line.split(" ");
            String entity;
            String input = null;
            entity = line.split(" ")[0];

            if (entity.contains("Content-Disposition:")) {
                String[] dispositions = inputArray[inputArray.length - 1].split("=");
                entity = "file name:";
                input = dispositions[1].replaceAll("\"", "");
                entities.put(entity, input);
                setFileName(input);
            } else if (inputArray.length > 1) {
                input = inputArray[1];

                if (entity.contains("Content: ")) {
                    entity = "file name:";
                    entities.put(entity, input);
                } else if (entity.contains("Content-Type:")) {
                    input = "application/octet-stream";
                    entities.put(entityNames.get(entity), input);
                }
                if (entityNames.get(entity) != null) {
                    if (entity.contains("Content-Length:")) {
                        input = String.valueOf(body.length);
                    }
                    System.out.println("entities.get(\"file name:\") = " + entities.get("file name:"));
                    entities.put(entityNames.get(entity), input);
                }
                if (fileName != null)
                    entities.put("file name:", fileName);


            }
        }
    }

    public void setResponseBody() {
        responseBody = formName;
        for (String entity : entities.keySet()) {
            responseBody = responseBody + "<li>" + entity + " " +
                    entities.get(entity) + "</li>\r\n";
        }
        responseBody = responseBody + "</html>\r\n";
//        System.out.println("*********RESPONSE HEADER*******\n" + formName);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
