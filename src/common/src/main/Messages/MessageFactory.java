package common.src.main.Messages;

import java.util.List;

import common.src.main.ACard;
import common.src.main.GameState;
import common.src.main.IMessage;
import common.src.main.IStateMessage;
import common.src.main.MessageType;

import static common.src.main.MessageType.*;

public class MessageFactory {
    public static IMessage create(Object[] fields) {
        return switch ((MessageType) fields[0]) {
            case NextPlayerCommand -> new NextPlayerCommand((String) fields[1], (String) fields[2]); 
            case PlayCardsCommand -> new PlayCardsCommand((String) fields[2], (List<ACard>)fields[2]); 
            case DrawCardsCommand -> new DrawCardsCommand((String) fields[2]); 
            case CallOutCommand -> new CallOutCommand((String) fields[2]); 
            case Update -> new UpdateMessage((String) fields[2]); 
            case NewGameState -> new NewGameStateMessage((GameState)fields[1], (String) fields[2]); 
            case InputRequest -> new InputRequest((IMessage)fields[1], (String) fields[2]); 
            case InputResponse -> new InputResponse((String) fields[1],(String) fields[2]);  
            case StartGame -> new GenericMessage(StartGame, (String) fields[2]);
            case UIMessage -> new UIMessage((ACard)fields[1], (String) fields[2]); 
            case GameOver -> new GenericMessage(GameOver, (String) fields[2]); 
            default -> AStateMessage.fromResponse(fields);
        };
    }
}
