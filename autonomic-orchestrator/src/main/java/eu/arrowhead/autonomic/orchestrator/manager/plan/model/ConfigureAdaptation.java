package eu.arrowhead.autonomic.orchestrator.manager.plan.model;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("ConfigureAdaptation")
public class ConfigureAdaptation extends Adaptation{
	
	
	private String attribute;
	private String value;
	
	public ConfigureAdaptation()
	{
		super();
		type = AdaptationType.ConfigureAdaptation;
	}
	
	
	public String getAttribute() {
		return attribute;
	}



	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}



	public String getValue() {
		return value;
	}



	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		ConfigureAdaptation other = (ConfigureAdaptation) obj;
		if (attribute == null) {
			if (other.attribute != null)
				return false;
		} else if (!attribute.equals(other.attribute))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ConfigureAdaptation [attribute=" + attribute + ", value=" + value + ", type=" + type + ", status="
				+ status + "]";
	}
	
	
	
	

}
