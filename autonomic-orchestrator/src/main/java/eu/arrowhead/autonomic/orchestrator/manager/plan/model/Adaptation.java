package eu.arrowhead.autonomic.orchestrator.manager.plan.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

@JsonTypeInfo(
	      use = JsonTypeInfo.Id.NAME, 
	      include = As.PROPERTY, 
	      property = "type")
	    @JsonSubTypes({
	        @JsonSubTypes.Type(value = SubstitutionAdaptation.class, name = "SubstitutionAdaptation"),
	    })
public class Adaptation {
	
	
	protected AdaptationType type;
	protected PlanStatus status;
	
	public Adaptation()
	{
		status = PlanStatus.NEW;
	}
	
	
	
	public AdaptationType getType() {
		return type;
	}
	public void setType(AdaptationType type) {
		this.type = type;
	}
	
	
	public PlanStatus getStatus() {
		return status;
	}


	public void setStatus(PlanStatus status) {
		this.status = status;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Adaptation other = (Adaptation) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}



	@Override
	public String toString() {
		return "Adaptation [type=" + type + ", status=" + status + "]";
	}
	
	

}
