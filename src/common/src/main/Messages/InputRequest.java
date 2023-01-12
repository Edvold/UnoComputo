package common.src.main.Messages;

import static common.src.main.MessageType.InputRequest;
import static common.src.main.MessageType.InputResponse;

import org.jspace.Space;

import com.google.gson.reflect.TypeToken;

import common.src.main.IMessage;
import common.src.main.IStateMessage;

public final class InputRequest extends AStateMessage<IMessage> {
    public InputRequest(IMessage message) {
        this(message, "InputResonse-Thread:" + Thread.currentThread().threadId());
    }

    private InputRequest(IMessage message, String responseTag) {
        super(InputRequest, message, responseTag);
    }

    /**
     * Suspends the calling thread and waits for a response to the input request.
     * @param inbox the space in which a response will appear
     * @return The input collected
     * @throws InterruptedException
     */
    public String waitForResponse(Space inbox) throws InterruptedException {
        var template = new InputResponse(null, this)
            .getTemplateBuilder()
            .addActualType(InputResponse)
            .addActualMessage(message)
            .build();

        var response = inbox.get(template.getFields())[1].toString();

        return response;
    }
}

class State<T, R> {
    public final Class<? super R> returnType;
    public final IStateMessage<T> state;

    public State(Class<? super R> returnType, IStateMessage<T> state) {
        this.returnType = returnType;
        this.state = state;
    }

}