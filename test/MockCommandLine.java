public class MockCommandLine implements CommandLine {

    private final String rex = "rex";
    private final String leo = "leo";

    public MockCommandLine() {
    }

    @Override
    public String getMessage() {
        String msg = "<3 " + rex + " & " + leo + " <3";
        return msg;
    }

    @Override
    public void submitEntry(String msg) {

    }

    @Override
    public void getUsage() {
        ;
    }

}
