package common.src.main;


public abstract class ACard implements Comparable<ACard> {
	public abstract Color getColor();
	public abstract Action getAction();
	public abstract boolean canBePlayedOn(ACard card);
	public abstract void setColor(Color color);
	public abstract boolean canChainWith(ACard other);
	public abstract void resetWildCard();

	public ACard() {
		
	}

	@Override
	public boolean equals(Object other){
		if( !(other instanceof ACard) ){
			return false;
		}
		ACard otherCard = (ACard) other;
		if (!(this.getColor() == otherCard.getColor())){
			return false;
		}
		if (!(this.getAction() == otherCard.getAction())){
			return false;
		}		
		return true;
	}
	@Override
	public int compareTo(ACard other){
		int colorCompare = this.getColor().compareTo(other.getColor());
		if (colorCompare != 0) return colorCompare;
		return this.getAction().compareTo(other.getAction());
	}

	@Override
	public String toString() {
		String color = this.getColor().toString().toLowerCase();
		String action = this.getAction().toString().toLowerCase();
		if (color == Color.BLACK.toString().toLowerCase()) {
			return action;
		}
		return color + " " + action;
	}

	public String toStringWithColor(){
		String color = this.getColor().toStringWithColor();
		String action = this.getAction().toString();
		if (color == Color.BLACK.toString().toLowerCase()) {
			return action;
		}
		return color + " " + action;
	}

	public boolean isNumberCard() {
		return switch (getAction()) {
			case ZERO,ONE,TWO,THREE,FOUR,FIVE,SIX,SEVEN,EIGHT,NINE -> true;
			//case DRAW2,REVERSE,SKIP,
			default -> false;
		};
	}
}

