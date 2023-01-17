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

	public String toStringWithColor() {
		switch (description){
			case "Yellow":
				return "\u001B[33m" + description + "\u001B[0m";
			case "Red":
				return "\u001B[31m" + description + "\u001B[0m";
			case "Blue":
				return "\u001B[34m" + description + "\u001B[0m";
			case "Green":
				return "\u001B[32m" + description + "\u001B[0m";	
		}
		return description;
	}

	public String toString(){
		return description;
	}

}


