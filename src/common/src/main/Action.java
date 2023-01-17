package common.src.main;

public enum Action {
	ZERO("0"),
	ONE("1"),
	TWO("2"),
	THREE("3"),
	FOUR("4"),
	FIVE("5"),
	SIX("6"),
	SEVEN("7"),
	EIGHT("8"),
	NINE("9"),
	DRAW2("+2"),
	REVERSE("<->"),
	SKIP("Ø"),
	WILD("WILD"),
	WILDDRAW4("WILD+4");


	private String description;

	private Action(String description) {
		this.description = description;
	}

	public String toString(){
		return description;
	}
}