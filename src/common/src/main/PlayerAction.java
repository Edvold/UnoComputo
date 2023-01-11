package common.src.main;

public enum PlayerAction {
    PLAY("Play a card"),
    DRAW("Draw a card"),
    UNO("Say \"uno!\""),
    OBJECT("Object"),
    ENDTURN("End your turn")

    private String description;

    private PlayerAction(String desc) {
        description = desc;
    }

    @Override
    public String toString() {
        return description;
    }
}
