package common.src.main.Messages;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Template;
import org.jspace.TemplateField;
import org.jspace.Tuple;

import com.google.gson.reflect.TypeToken;

import common.src.main.IStateMessage;
import common.src.main.MessageType;

public abstract class AStateMessage<T> implements IStateMessage<T> {
    protected MessageType type;
    protected T state;
    protected String message;

    private Class<? super T> stateType;

    public AStateMessage(MessageType type, T state, String message) {
        this.type = type;
        this.state = state;
        this.message = message;

        stateType = new TypeToken<T>(){}.getRawType();
    }

    public AStateMessage(Object[] fields) {
        if (!verifySpaceResult(fields)) throw new IllegalArgumentException();

        type = (MessageType) fields[0];
        state = (T) fields[1];
        message = (String) fields[2];
    }

    public IStateMessageTemplateBuilder<T> getTemplateBuilder() {
        return new MessageTemplateBuilder(type, stateType);
    }

    public Object[] getFields() {
        return new Object[] {type, state, message};
    }

    protected boolean verifySpaceResult(Object... fields) throws IllegalArgumentException {
        Template template = getTemplateBuilder().build();
        Tuple tuple = new Tuple(fields);
        boolean doesMatch = template.match(tuple);
        return doesMatch;
    }


    public MessageType getMessageType() {
        return type;
    }

    public T getState() {
        return state;
    }

    public String getMessageText() {
        return message;
    }

    @Override
    public String toString() {
        return new StringBuilder("(")
            .append(type)
            .append(", ")
            .append(state)
            .append(", ")
            .append(message)
            .append(")")
            .toString();
    }

    public class MessageTemplateBuilder implements IStateMessageTemplateBuilder<T> {
        protected MessageType messageType;

        protected TemplateField type;
        protected TemplateField state;
        protected TemplateField message;
        
        MessageTemplateBuilder(MessageType messageType, Class<? super T> stateType) {
            this.messageType = messageType;
            
            type = new FormalField(MessageType.class);
            state = new FormalField(stateType);
            message = new FormalField(String.class);
        }

        public MessageTemplateBuilder addActualType() {
            this.type = new ActualField(type);
            return this;
        }

        public MessageTemplateBuilder addActualType(MessageType type) {
            this.type = new ActualField(type);
            return this;
        }

        public MessageTemplateBuilder addActualState(T state) {
            this.state = new ActualField(state);
            return this;
        }

        public MessageTemplateBuilder addActualMessage(String message) {
            this.message = new ActualField(message);
            return this;
        }

        public Template build() {
            return new Template(type, state, message);
        }
    }
}
