import java.util.HashMap;


public class FormInputHandler implements FormHandler {
    private HashMap<String, String> boxMap;
    private String response;


    public String handle(String request) {
        String[] requestArgs = request.split("[?=&]");
        boxMap = new HashMap<>();

        setFormName("GET Form");
        System.out.println("response = " + response);
        createBoxMap(requestArgs);
        setResponse();

        return response;
    }

   public void setFormName(String name) {
//        Path path = Path.of(root);
//        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
//        for(String line : lines) {
//            if (line.contains("<h2>"))
//                response = line;
//        }
        response = "<html>\r\n" +
                "<h2>" + name + "</h2>\r\n";
    }

    public void createBoxMap(String[] requestArgs) {
        for (int i = 1; i < requestArgs.length; i = i + 2) {
            String name = requestArgs[i];
            String input = requestArgs[i + 1];
            boxMap.put(name, input);
        }
    }

    public void setResponse() {
        for (String box : boxMap.keySet()) {
            response = response + "<li>" + box + ": " +
                    boxMap.get(box) + "</li>\r\n" +
                    "</html>";
        }
    }
}
