package eu.arrowhead.autonomic.orchestrator.manager.plan.model;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("SubstitutionAdaptation")
public class SubstitutionAdaptation extends Adaptation {

    private String fromService;
    private String toService;

    public SubstitutionAdaptation() {
        super();
        type = AdaptationType.SubstitutionAdaptation;
    }

    public String getFromService() {
        return fromService;
    }

    public void setFromService(String fromService) {
        this.fromService = fromService;
    }

    public String getToService() {
        return toService;
    }

    public void setToService(String toService) {
        this.toService = toService;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((fromService == null) ? 0 : fromService.hashCode());
        result = prime * result + ((toService == null) ? 0 : toService.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SubstitutionAdaptation other = (SubstitutionAdaptation) obj;
        if (fromService == null) {
            if (other.fromService != null) {
                return false;
            }
        } else if (!fromService.equals(other.fromService)) {
            return false;
        }
        if (toService == null) {
            if (other.toService != null) {
                return false;
            }
        } else if (!toService.equals(other.toService)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SubstitutionAdaptation [fromService=" + fromService + ", toService=" + toService + ", type=" + type
                + ", status=" + status + "]";
    }

}
