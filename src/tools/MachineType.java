package tools;

public enum MachineType {
	/**
	 * TypeI : The file path contain "DATA", contain project id, but without year and season. "D\Analyst Data\Projects\20181101_F16ZQSBBSY2948_005_MRM\DATA"
	 * TypeII : The file path contain "DATA", without project id, without year and season. "D\GCMS\DATA\2018\201811\DATA"
	 * TypeIII : The file path contain "Data", but without year and season. "D\MassLynx\Vitamin.PRO\Data"
	 * TypeIV : The file path contain project id, and with year and season. "D\project\data\2018\season4\20181101_F16ZQSBBSY2948_005_DIA"
	 * TypeI: "5300", "5600", "6500"
	 * TypeII: "G2XS", "TSQALTIS", "GCMS"
	 * TypeIII: "TQS"
	 * TypeIV: "MALDI", "ORBI", "QE", "TSQ9000", "ICP", "QEHF", "LUMOS"
	 */
	
	TypeI,TypeII,TypeIII,TypeIV

}
