package eu.arrowhead.autonomic.orchestrator.store;

import java.io.Serializable;
import java.util.List;

public class OrchestrationStoreResponseDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1231629045930398790L;

    private int count;

    private List<OrchestrationStoreEntryDTO> data;

    public OrchestrationStoreResponseDTO() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<OrchestrationStoreEntryDTO> getData() {
        return data;
    }

    public void setData(List<OrchestrationStoreEntryDTO> data) {
        this.data = data;
    }

}
