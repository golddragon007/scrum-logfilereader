package hu.bme.tmit.agile.logfilereader.model;

public class VerdictOperation extends TtcnEvent {
	private String miscText;
	private String verdict;
	private String componentName;
	private int portNumber;
	
	public String getMiscText(){
		return miscText;
	}
	public void setMiscText(String text){
		this.miscText=text;
	}
	public String getVerdict(){
		return this.verdict;
	}
	public void setVerdict(String verdict){
		this.verdict=verdict;
	}
	public String getComponentName(){
		return componentName;
	}
	public void setComponentName(String component){
		this.componentName=component;
	}
	public int getPortNumber(){
		return portNumber;
	}
	public void setPortNumber(int port){
		this.portNumber=port;
	}
	
}
