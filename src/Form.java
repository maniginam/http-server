public class FormManager {

    private final String tailResponse;
    private String response;
    private String[] inputBoxes;
    private String[] boxText;

    public FormManager() {
        String[] inputBoxes = new String[2];
        inputBoxes[0] = "Foo";
        inputBoxes[1] = "Bar";
        response = "<html>\r\n" +
                "\r\n" +
                "<h2>Get Form</h2>\r\n" +
                "<form method=\"get\" action=\"/form\">\r\n";

        tailResponse = "<input type=\"submit\" value=\"Submit\"/>\r\n" +
                "</form>\r\n";

        boxText = new String[inputBoxes.length];
        for (String box : inputBoxes) {
            String line = "<label>" + box + "</label>\r\n" +
                    "<input type=\"text\" name=\"" + box.toLowerCase() + "\"/>\r\n";
            response = response + line;
        }
        response = response + tailResponse;

    }

    public String getInstructions() {
        return response;
    }
}
