public interface Responder {
    void respond(String target);

    String getResponseBodyMessage();
    String getResponseHeader();
}
