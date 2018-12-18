package tools;

public enum MachineID {
	
	M4500("4500"), M5500("5500"), M5600("5600"), M6500("6500"), GCMS("GCMS"), G2XS("G2XS"), ICP("ICP"), LUMOS("LUMOS"), MALDI("MALDI"), QE("QE"), QEHF("QEHF"), ORBI("ORBI"), TQS("TQS"), TSQALTIS("TQSALTIS"), TSQ9000("TQS8000");
	
	private String name;
	
	private MachineID(String name) {
		this.name = name;
	}
	
	
	public static MachineID getMachineID(String name) {
		for (MachineID temp : MachineID.values()) {
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
