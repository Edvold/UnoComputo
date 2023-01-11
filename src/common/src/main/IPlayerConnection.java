package common.src.main;

import org.jspace.Space;

public interface IPlayerConnection {
    /**
     * Returns the name of this player
     * @return the username of the player
     */
    String getPlayerName(); 

    /**
     * This may or may not be a {@code RemoteSpace}.
     * @return the space used to send messages to this player
     */
    Space getPlayerInbox();
}
