package btill.terminal.bluetooth;

public class BtillResponse {
    private String body;
    private Status status;

    public BtillResponse(Status status, String body) {
        this.status = status;
        this.body = body;
    }

    public String status() {
        return status.toString();
    }

    public String body() {
        return body;
    }
}
