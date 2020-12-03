import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostFormHandler implements FormHandler {

    private String response;
    private HashMap<String, String> entities;
    private HashMap<String, String> entityNames;

    public PostFormHandler() {
        entityNames = new HashMap<>();
        entityNames.put("content:", "file name:");
        entityNames.put("content-length:", "file size:");
        entityNames.put("content-type:", "content type:");
    }

    public String handle(String requestHeader, int multipartNumber) {
        System.out.println("*******REQUEST HEADER **********\n " + requestHeader);

        if (multipartNumber < 3) {
            setFormName("POST Form");
            return response + "</html>";
        } else {
            List<String> entityList = new ArrayList<>();

            for (String line : requestHeader.split("\r\n")) {
                entityList.add(line);
            }

            entityList.remove(0);
            entities = new HashMap<>();

            setFormName("POST Form");
            createBoxMap(entityList);
            setResponse();
        }
        return response;
    }

    public void setFormName(String name) {
        response = "<html>\r\n\r\n" +
                "<h2>" + name + "</h2>\r\n";
    }

    private void createBoxMap(List<String> entityList) {
        for (String line : entityList) {
            String[] inputArray = line.split(" ");
            String entity;
            String input = null;
            entity = line.split(" ")[0].toLowerCase();

            if (inputArray.length > 1) {
                input = inputArray[1];


                if (entity.contains("content-disposition:")) {
                    String[] dispositions = inputArray[inputArray.length - 1].split("=");

                    entity = "file name:";
                    input = dispositions[1].replaceAll("\"", "");
                } else if (entity.contains("content-type:")) {
                    input = "application/octet-stream";
                }

                if (entityNames.get(entity) != null) {
                    entity = entityNames.get(entity);
                }

            }
            if (input != null)
                entities.put(entity, input);
        }
    }


    public void setResponse() {
        for (String entity : entities.keySet()) {
            response = response + "<li>" + entity + " " +
                    entities.get(entity) + "</li>\r\n";
        }

        response = response + "</html>";
        System.out.println("*********RESPONSE HEADER*******\n" + response);
    }
}
