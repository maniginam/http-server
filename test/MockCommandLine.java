public class MockCommandLine implements CommandLine {

    private final String rex;
    private final String leo;

    public MockCommandLine(String rex, String leo) {
        this.rex = rex;
        this.leo = leo;
    }

    @Override
    public String getMessage() {
        String msg = "<3 " + rex + " & " + leo + " <3";
        return msg;
    }
}
