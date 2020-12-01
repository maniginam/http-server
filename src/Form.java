public class Form {

    private final String tailResponse;
    private String response;
    private String[] boxes;
    private String[] boxText;

    public Form(String[] boxes) {
        this.boxes = boxes;
        response = "<html>\r\n" +
                "\r\n" +
                "<h2>Get Form</h2>\r\n" +
                "<form method=\"get\" action=\"/form\">\r\n";

        tailResponse = "<input type=\"submit\" value=\"Submit\"/>\r\n" +
                "</form>\r\n" +
                "\r\n" +
                "</html>\r\n";

        boxText = new String[boxes.length];
        for (String box : boxes) {
            String line = "<label>" + box + ":</label>\r\n" +
                    "<input type=\"text\" name=\"" + box.toLowerCase() + "\"/>\r\n";
            response = response + line;
        }
        response = response + tailResponse;

    }

    public String getInstructions() {
        return response;
    }
}
