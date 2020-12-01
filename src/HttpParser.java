public class HttpParser {

    private static String serverName = "Gina's Http Server";
    String msg = "";
    public String status = "HTTP/1.1 200 OK";
    public String headerField = "Server: " + serverName + "\r\n" +
            "Content-Length: " + msg.length() + "\r\n";


    public void setStatus(String method, int code, String msg) {
        status = method + "HTTP/1.1 " + code + " " + msg + "\r\n";
    }

    public void setHeaderField(String msg, byte[] bodyBytes, String fields) {
        int contentLength = 100;
        if(msg != null)
            contentLength = msg.length();
        if(bodyBytes != null)
            contentLength = bodyBytes.length;

        headerField = "Server: " + serverName + "\r\n" +
                "Content-Length: " + contentLength + "\r\n";
        if (!fields.isBlank()) {
            String[] otherFields = fields.split(", ");
            for (String field : otherFields) {
                headerField = headerField + field + "\r\n";
            }
        }
        headerField = headerField + "\r\n";
    }

    public String getStatus() {
        return status;
    }

    public String getHeaderField() {
        return headerField;
    }

    public String wrapHeader(String msg, String fields) {
        setHeaderField(msg, null, fields);
        String response = "HTTP/1.1 200 OK\r\n" +
                headerField +
                msg;
        return response;
    }

    public String wrapBadRequest(String msg) {
        setHeaderField(msg, null, "");
        String response = "HTTP/1.1 404 " + msg + "\r\n" +
                headerField +
                msg;
        return response;
    }

    public String toHTML(String image) {
        String body = "<p><input type=\"image\" name=\"autobot.jpg\">" + image + "</input></p>";
        return body;
    }

    public String getHeader() {
        return status + headerField;
    }
}
