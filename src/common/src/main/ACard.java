package common.src.main;


public abstract class ACard implements Comparable<ACard> {
	public abstract Color getColor();
	public abstract Action getAction();
	@Override
	public abstract boolean equals(Object other);
	public abstract boolean canBePlayedOn(ACard card);
	public abstract void setColor(Color color);
	public abstract boolean canChainWith(ACard other);
	public abstract void resetWildCard();
	@Override
	public abstract int compareTo(ACard other);
}

