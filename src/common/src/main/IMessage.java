package common.src.main;

import org.jspace.FormalField;
import org.jspace.Template;

/**
 * A basic type of message that consists of a type/command and string message.
 */
public interface IMessage {
    /**
     * The command or type of message this represents
     * @return This message type
     */
    public MessageType getMessageType();
    /**
     * @return The text of the message
     */
    public String getMessageText();

    /**
     * Create and returns a representation of the message suitabe for sending into a tuple space. 
     * This method should be used when putting the object in all cases as this ensures proper seralization 
     * and suport for actual templating. 
     * @return an {@code Object[]} representation of the message.
     */
    public Object[] getFields();

    /**
     * Creates a builder that allows for using templates particular to the message specific.
     * @return a new {@code IMessageTemplateBuilder}
     */
    public IMessageTemplateBuilder getTemplateBuilder();

    public static Template getGeneralTemplate() {
        return new Template(
            new FormalField(MessageType.class), 
            new FormalField(Object.class), 
            new FormalField(String.class));
    }

    /**
     * An implementation of this interface provides a way to make templates specefic 
     * to this message, including providing actual fields.
     */
    public interface IMessageTemplateBuilder {
        /**
         * Only Find messages of this exact type
         * @return this instance
         */
        public IMessageTemplateBuilder addActualType();
        /**
         * Templates for the specific value provided
         * @param message The message to search for
         * @return this instance
         */
        public IMessageTemplateBuilder addActualMessage(String message);

        /**
         * Make the tamplate represented by this builder
         * @return A new Template
         */
        public Template build();
    }
}
