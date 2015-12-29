package ee.hm.dop.ehis.pojo;

import java.util.List;

public class EhisOppeasutus {

	private String id;
	private String regNr;
	private String nimetus;
	private List<EhisRoll> rollid;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRegNr() {
		return regNr;
	}
	public void setRegNr(String regNr) {
		this.regNr = regNr;
	}
	public String getNimetus() {
		return nimetus;
	}
	public void setNimetus(String nimetus) {
		this.nimetus = nimetus;
	}
	public List<EhisRoll> getRollid() {
		return rollid;
	}
	public void setRollid(List<EhisRoll> rollid) {
		this.rollid = rollid;
	}
	
}
