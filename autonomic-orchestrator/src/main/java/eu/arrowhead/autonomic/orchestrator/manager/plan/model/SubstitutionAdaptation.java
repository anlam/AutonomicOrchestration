package eu.arrowhead.autonomic.orchestrator.manager.plan.model;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("SubstitutionAdaptation")
public class SubstitutionAdaptation extends Adaptation{
	
	
	private String fromService;
	private String fromProducer;
	private String toService;
	private String toProducer;
	
	
	public SubstitutionAdaptation()
	{
		super();
		type = AdaptationType.SubstitutionAdaptation;
	}


	
	public String getFromService() {
		return fromService;
	}



	public void setFromService(String fromService) {
		this.fromService = fromService;
	}



	public String getFromProducer() {
		return fromProducer;
	}



	public void setFromProducer(String fromProducer) {
		this.fromProducer = fromProducer;
	}



	public String getToService() {
		return toService;
	}



	public void setToService(String toService) {
		this.toService = toService;
	}



	public String getToProducer() {
		return toProducer;
	}



	public void setToProducer(String toProducer) {
		this.toProducer = toProducer;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((fromProducer == null) ? 0 : fromProducer.hashCode());
		result = prime * result + ((fromService == null) ? 0 : fromService.hashCode());
		result = prime * result + ((toProducer == null) ? 0 : toProducer.hashCode());
		result = prime * result + ((toService == null) ? 0 : toService.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubstitutionAdaptation other = (SubstitutionAdaptation) obj;
		if (fromProducer == null) {
			if (other.fromProducer != null)
				return false;
		} else if (!fromProducer.equals(other.fromProducer))
			return false;
		if (fromService == null) {
			if (other.fromService != null)
				return false;
		} else if (!fromService.equals(other.fromService))
			return false;
		if (toProducer == null) {
			if (other.toProducer != null)
				return false;
		} else if (!toProducer.equals(other.toProducer))
			return false;
		if (toService == null) {
			if (other.toService != null)
				return false;
		} else if (!toService.equals(other.toService))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SubstitutionAdaptation [fromService=" + fromService + ", fromProducer=" + fromProducer + ", toService="
				+ toService + ", toProducer=" + toProducer + ", type=" + type + ", status=" + status + "]";
	}
	
	
	
	

}
