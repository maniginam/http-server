import java.text.SimpleDateFormat;
import java.util.Date;

public class Ping {
    private final String target;
    private final SimpleDateFormat formatter;
    private final Date nowMillis;
    private final Date laterMillies;
    private String response;
    private Long seconds;
    private int wait;

    public Ping(String target) {
        this.target = target;
        formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        nowMillis = new Date();
        String[] pingArgs = target.split("/");
        if (pingArgs.length > 2) {
            wait = Integer.parseInt(pingArgs[2]);
        }
        else {
            wait = 0;
        }
        laterMillies = new Date(System.currentTimeMillis() + wait * 1000);
        setSeconds();
    }

    public void respond() {
        String now = formatter.format(nowMillis);
        String later = formatter.format(laterMillies);

        response = "<html>\r\n" +
                "<h2>Ping</h2>\r\n" +
                "<li>start time: " + now + "</li>\r\n" +
                "<li>end time: " + later + "</li>\r\n" +
                "<li>sleep seconds: " + seconds + "</li>\r\n" +
                "</html>\r\n" +
                "\r\n";
    }

    private void setSeconds() {
        seconds = Math.abs(laterMillies.getTime() - nowMillis.getTime())/ 1000;
    }

    public String getResponse() {
        return response;
    }
}
