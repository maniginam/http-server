import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class FormHandler {
    private HashMap<String, String> boxMap;
    private String response;
    private String root;

    public String handle(String request, String root) throws IOException {
        this.root = root;
        String[] requestArgs = request.split("[?=&]");
        boxMap = new HashMap<>();

        getFormName(root);
        createBoxMap(requestArgs);
        setResponse();

        return response;
    }

    private void getFormName(String root) throws IOException {
//        Path path = Path.of(root);
//        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
//        for(String line : lines) {
//            if (line.contains("<h2>"))
//                response = line;
//        }
        if()
        response = "<html>\r\n" +
                "<h2>GET Form</h2>\r\n";
    }

    private void setResponse() {
        for (String box : boxMap.keySet()) {
            response = response + "<li>" + box + ": " +
                    boxMap.get(box) + "</li>\r\n" +
                    "</html>";
        }
    }

    private void createBoxMap(String[] requestArgs) {
        for (int i = 1; i < requestArgs.length; i = i + 2) {
            String name = requestArgs[i];
            String input = requestArgs[i + 1];
            boxMap.put(name, input);
        }
    }
}
