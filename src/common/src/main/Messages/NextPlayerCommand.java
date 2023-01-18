package common.src.main.Messages;

import static common.src.main.MessageType.NextPlayerCommand;;

public final class NextPlayerCommand extends AStateMessage<String> {
    public NextPlayerCommand() {
        this("");
    }

    public NextPlayerCommand(String turnToken) {
        super(NextPlayerCommand, turnToken, "");
    }
}
