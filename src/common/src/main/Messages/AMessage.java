package common.src.main.Messages;

import common.src.main.IMessage;
import common.src.main.MessageType;

public abstract class AMessage implements IMessage {
    private AStateMessage<Unit> actualMessage;

    public AMessage(MessageType type, String message) {
        actualMessage = new AStateMessage<AMessage.Unit>(type, Unit.getInstance(), message) { };
    }


    @Override
    public Object[] getFields() {
        return actualMessage.getFields();
    }

    @Override
    public IMessageTemplateBuilder getTemplateBuilder() {
        return actualMessage.getTemplateBuilder();
    }

    @Override
    public MessageType getMessageType() {
        return actualMessage.getMessageType();
    }

    @Override
    public String getMessageText() {
        return actualMessage.getMessageText();
    }

    @Override
    public String toString() {
        return new StringBuilder("(")
            .append(getMessageType())
            .append(", ")
            .append(getMessageText())
            .append(")")
            .toString();
    }
    
    private final static class Unit {
        private static Unit instance;
    
        private Unit() { }
        
        @Override
        public String toString() {
            return "Unit";
        }
        
        public static Unit getInstance() {
            if (instance == null) {
                instance = new Unit();
            }
            return instance;
        } 
    }
}

