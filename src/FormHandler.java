import java.util.HashMap;

public interface FormHandler {
    HashMap<String, String> boxMap = null;
    String response = null;


    void setFormName(String name);

    void setResponseBody();

}
