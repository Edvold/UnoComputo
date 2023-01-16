package common.src.main.Messages;

import static common.src.main.MessageType.NextPlayerCommand;;

public final class NextPlayerCommand extends AStateMessage<String> {
    public NextPlayerCommand(String player) {
        this("", player);
    }

    public NextPlayerCommand(String turnToken, String player) {
        super(NextPlayerCommand, turnToken, player);
    }
}
