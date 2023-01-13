package common.src.main.Messages;
import static common.src.main.MessageType.InputResponse;

public class InputResponse extends AStateMessage<String> {

    public InputResponse(String response, Object... fields) {
        super(InputResponse, response, (String)fields[2]);
    } 

    public InputResponse(String response, InputRequest request) {
        super(InputResponse, response, request.message);
    }
}
