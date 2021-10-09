package eu.arrowhead.autonomic.orchestrator.store;

import java.io.Serializable;

public class AttributeDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 3687579466955093758L;

    private String additionalProp1;
    private String additionalProp2;
    private String additionalProp3;

    public AttributeDTO() {
    }

    public String getAdditionalProp1() {
        return additionalProp1;
    }

    public void setAdditionalProp1(String additionalProp1) {
        this.additionalProp1 = additionalProp1;
    }

    public String getAdditionalProp2() {
        return additionalProp2;
    }

    public void setAdditionalProp2(String additionalProp2) {
        this.additionalProp2 = additionalProp2;
    }

    public String getAdditionalProp3() {
        return additionalProp3;
    }

    public void setAdditionalProp3(String additionalProp3) {
        this.additionalProp3 = additionalProp3;
    }
}
