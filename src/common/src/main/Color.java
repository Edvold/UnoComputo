package common.src.main;

public enum Color {
	RED("Red"),
	BLUE("Blue"),
	GREEN("Green"),
	YELLOW("Yellow"),
	BLACK("");

	private String description;

	private Color(String description) {
		this.description = description;
	}

	public String toString() {
		return description;
	}
}


