package eu.arrowhead.autonomic.orchestrator.manager.plan.model;

public class SubstitutionAdaptation extends Adaptation{
	
	
	private String FromService;
	private String FromProducer;
	private String ToService;
	private String ToProducer;
	
	public SubstitutionAdaptation()
	{
		type = "Substitution";
	}

	public String getFromService() {
		return FromService;
	}

	public void setFromService(String fromService) {
		FromService = fromService;
	}

	public String getFromProducer() {
		return FromProducer;
	}

	public void setFromProducer(String fromProducer) {
		FromProducer = fromProducer;
	}

	public String getToService() {
		return ToService;
	}

	public void setToService(String toService) {
		ToService = toService;
	}

	public String getToProducer() {
		return ToProducer;
	}

	public void setToProducer(String toProducer) {
		ToProducer = toProducer;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((FromProducer == null) ? 0 : FromProducer.hashCode());
		result = prime * result + ((FromService == null) ? 0 : FromService.hashCode());
		result = prime * result + ((ToProducer == null) ? 0 : ToProducer.hashCode());
		result = prime * result + ((ToService == null) ? 0 : ToService.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubstitutionAdaptation other = (SubstitutionAdaptation) obj;
		if (FromProducer == null) {
			if (other.FromProducer != null)
				return false;
		} else if (!FromProducer.equals(other.FromProducer))
			return false;
		if (FromService == null) {
			if (other.FromService != null)
				return false;
		} else if (!FromService.equals(other.FromService))
			return false;
		if (ToProducer == null) {
			if (other.ToProducer != null)
				return false;
		} else if (!ToProducer.equals(other.ToProducer))
			return false;
		if (ToService == null) {
			if (other.ToService != null)
				return false;
		} else if (!ToService.equals(other.ToService))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SubstitutionAdaptation [FromService=" + FromService + ", FromProducer=" + FromProducer + ", ToService="
				+ ToService + ", ToProducer=" + ToProducer + ", type=" + type + "]";
	}
	
	
	
	

}
