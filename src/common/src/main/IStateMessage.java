package common.src.main;

import org.jspace.FormalField;
import org.jspace.Template;

/**
 * A more advanced message that includes an arbitrary state value.
 * It is up to the implementer to specify which type this State should take.
 */
public interface IStateMessage<T> extends IMessage {
    /**
     * @return The state contained in the message
     */
    public T getState();

    /**
     * {@inheritDoc}
     * This version also provides a way to Template actual fields in the State.
     * @return a new {@code IStateMessageTemplateBuilder}
     */
    @Override
    public IStateMessageTemplateBuilder<T> getTemplateBuilder();

    public static Template getGeneralTemplate() {
        return IMessage.getGeneralTemplate();
    }

    public interface IStateMessageTemplateBuilder<T> extends IMessageTemplateBuilder {
        public IStateMessageTemplateBuilder<T> addActualState(T state);
    }
}
