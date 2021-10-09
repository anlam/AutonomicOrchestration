package eu.arrowhead.autonomic.orchestrator.store;

import java.io.Serializable;
import java.util.Objects;

public class CloudResponseDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 3242468155016186929L;
    // =================================================================================================
    // members

    private int id;
    private String operator;
    private String name;
    private Boolean secure;
    private Boolean neighbor;
    private String authenticationInfo;
    private Boolean ownCloud;
    private String createdAt;
    private String updatedAt;

    // =================================================================================================
    // methods

    // -------------------------------------------------------------------------------------------------
    public String getOperator() {
        return operator;
    }

    public String getName() {
        return name;
    }

    public Boolean getSecure() {
        return secure;
    }

    public Boolean getNeighbor() {
        return neighbor;
    }

    public String getAuthenticationInfo() {
        return authenticationInfo;
    }

    // -------------------------------------------------------------------------------------------------
    public void setOperator(final String operator) {
        this.operator = operator;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setSecure(final Boolean secure) {
        this.secure = secure;
    }

    public void setNeighbor(final Boolean neighbor) {
        this.neighbor = neighbor;
    }

    public void setAuthenticationInfo(final String authenticationInfo) {
        this.authenticationInfo = authenticationInfo;
    }

    // -------------------------------------------------------------------------------------------------
    @Override
    public int hashCode() {
        return Objects.hash(operator, name);
    }

    // -------------------------------------------------------------------------------------------------
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final CloudResponseDTO other = (CloudResponseDTO) obj;

        return Objects.equals(name, other.name) && Objects.equals(operator, other.operator);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boolean getOwnCloud() {
        return ownCloud;
    }

    public void setOwnCloud(Boolean ownCloud) {
        this.ownCloud = ownCloud;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
