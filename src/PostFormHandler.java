import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostFormHandler implements FormHandler {

    private String response;
    private HashMap<String, String> entities;
    private HashMap<String, String> entityNames;

    public PostFormHandler() {
        entityNames = new HashMap<>();
        entityNames.put("Name:", "file name:");
        entityNames.put("Content-Length:", "file size:");
    }

    public String handle(String requestHeader, byte[] requestBody) {
        List<String> entityList = new ArrayList<>();

        for (String line : requestHeader.split("\r\n")) {
            entityList.add(line);
        }
        entityList.remove(0);

        entities = new HashMap<>();


        setFormName("POST Form");
        createBoxMap(entityList);
        setResponse();

        return response;
    }

    public void setFormName(String name) {
        response = "<html>\r\n\r\n" +
                "<h2>" + name + "</h2>\r\n";
    }

    private void createBoxMap(List<String> entityList) {
        String entity;
        for (String line : entityList) {

            String input = line.split(" ")[1];
            entity = line.split(" ")[0];

            if(entityNames.get(entity) != null) {
                entity = entityNames.get(entity);
            }

            entities.put(entity, input);
        }
    }

    public void setResponse() {
        for (String entity : entities.keySet()) {
            response = response + "<li>" + entity + " " +
                    entities.get(entity) + "</li>\r\n";
        }

        response = response + "</html>";
        System.out.println("response = " + response);
    }
}
