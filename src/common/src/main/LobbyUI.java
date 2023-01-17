package common.src.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.jspace.SequentialSpace;
import org.jspace.Space;

import common.src.main.Messages.InputRequest;
import common.src.main.Messages.InputResponse;
import common.src.main.Messages.UpdateMessage;

public class LobbyUI {
    private static LobbyUI instance;
    private final Space inbox;
    private final Space outbox;
    private final BufferedReader input;
    private boolean isRunning;

    private LobbyUI() {
        inbox = new SequentialSpace();
        outbox = new SequentialSpace();
        input = new BufferedReader(new InputStreamReader(System.in));
        isRunning = false;
    }

    public static synchronized LobbyUI getInstance() {
        if (instance == null) {
            instance = new LobbyUI();
        }
        return instance;
    }
    
    public void showMessage(String s) throws InterruptedException {
        var message = new UpdateMessage(s);
        showMessage(message);
    }

    public void showMessage(IMessage message) throws InterruptedException {
        inbox.put(message.getFields());
    }

    public String getInput(String s) throws InterruptedException {
        var message = new UpdateMessage(s);
        return getInput(message);
    }

    public String getInput(IMessage message) throws InterruptedException {
        final var inputReq =  new InputRequest(message);
        inbox.put(inputReq.getFields());
        var response = inputReq.waitForResponse(outbox);
        return response;
    }

    public void start() {
        System.out.println("Ui Started");
        isRunning = true;
        while (isRunning) {
            try {
                var message = inbox.get(IStateMessage.getGeneralTemplate().getFields());
                var type = (MessageType)message[0];
                if (type == MessageType.InputRequest) {
                    System.out.println(message[1].toString()); 
                    var response = input.readLine();
                    var output = new InputResponse(response, message);
                    outbox.put(output.getFields());
                } else if (type == MessageType.Update) {
                    System.out.println(message[2]);
                } else {
                    System.out.println(Arrays.toString(message));
                }
            } catch (InterruptedException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            input.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void stop() throws InterruptedException {
        isRunning = false;
        inbox.put(new UpdateMessage("Closing LobbyUI...").getFields());
    }
}
