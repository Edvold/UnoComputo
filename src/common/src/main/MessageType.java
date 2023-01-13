package common.src.main;

/**
 * The different types of messages that can be sent between Players, Game and UI
 */
public enum MessageType {
    /**
     * Command sent by Game to Players, informing them of who now has their turn.
     * The command also delivers the turn token to the appropriate Player.
     */
    NextPlayerCommand,
    /**
     * Command sent by Player to Game, informing game of which cards the player plays 
     * as well as wether they say Uno or not.
     */
    PlayCardsCommand,
    /**
     * Command sent by Player to Game, informing game that the player wishes to draw cards.
     * This can be due to having no cards to play, not wishing to play cards or being unable 
     * to continue a chain of draw-x cards.
     */
    DrawCardsCommand,
    /**
     * Command sent by Player to Game, objecting to a user forgetting to say Uno.
     */
    CallOutCommand,
    /**
     * Message sent by Game to one or more Players with some information for the UI.
     */
    Update,
    NewGameState,
    InputRequest,
    InputResponse,
    JoinGameRequest, 
    StartGame,
}