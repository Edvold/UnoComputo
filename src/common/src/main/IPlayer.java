package common.src.main;

public interface IPlayer {

    boolean verify();

    ACard[] getPlayableCards();

    String computeReturnToken(); //can maybe be omitted
    
}
