package ee.hm.dop.ehis.pojo;

import java.util.List;

public class EhisIsik {

	private String isikukood;
	private String eesnimi;
	private String perenimi;
	private List<EhisOppeasutus> oppeasutused;
	
	public String getIsikukood() {
		return isikukood;
	}
	public void setIsikukood(String isikukood) {
		this.isikukood = isikukood;
	}
	public String getEesnimi() {
		return eesnimi;
	}
	public void setEesnimi(String eesnimi) {
		this.eesnimi = eesnimi;
	}
	public String getPerenimi() {
		return perenimi;
	}
	public void setPerenimi(String perenimi) {
		this.perenimi = perenimi;
	}
	public List<EhisOppeasutus> getOppeasutused() {
		return oppeasutused;
	}
	public void setOppeasutused(List<EhisOppeasutus> oppeasutused) {
		this.oppeasutused = oppeasutused;
	}
	
}
