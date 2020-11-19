import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class MyHttpServer extends HttpURLConnection {


    /**
     * Constructor for the HttpURLConnection.
     *
     * @param u the URL
     */
    protected MyHttpServer(URI uri) {
        URL url = uri;
        super(uri.toURL(uri));
    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean usingProxy() {
        return false;
    }

    @Override
    public void connect() throws IOException {

    }
}
