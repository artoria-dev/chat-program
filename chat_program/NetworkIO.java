package chat_program;

import java.util.ArrayList;

public class NetworkIO {

    String usr_name;
    String message;
    ArrayList<String> currentLog = new ArrayList<String>();

    public NetworkIO(String usr_name, String message) {
        this.usr_name = usr_name;
        this.message = message;
    }

    public void sendMessage() {
        System.out.println("user: " + usr_name + ", message: " + message);
    }

    // returns updated log to user
    public ArrayList<String> getUpdatedLog() {
        return currentLog;
    }
}
