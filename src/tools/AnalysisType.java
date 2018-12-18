package tools;

public enum AnalysisType {
	Protein("Protein"),Metabolite("Metabolite"),SmallMoleculer("Small_molecule");
	
	private String name;
	
	private AnalysisType(String name) {
		this.name= name;
	}
	
	public static AnalysisType getAnalysisType(String name) {
		for (AnalysisType temp:AnalysisType.values()) {
			if (temp.getName().equals(name)) {
				return temp;
			}
		}
		return null;
	}	
	public String getName() {
		return this.name;
	}
}
