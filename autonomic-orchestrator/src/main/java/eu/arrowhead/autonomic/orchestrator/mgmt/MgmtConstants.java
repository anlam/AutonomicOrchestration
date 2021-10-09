package eu.arrowhead.autonomic.orchestrator.mgmt;

public class MgmtConstants {

    // =================================================================================================
    // members

    public static final String MGMT_KEYSTORE_PATH = "cloud.key-store";
    public static final String $MGMT_KEYSTORE_PATH = "${" + MGMT_KEYSTORE_PATH + "}";
    public static final String MGMT_KEYSTORE_PASSWORD = "cloud.ssl.key-store-password"; // NOSONAR it is not a password
    public static final String $MGMT_KEYSTORE_PASSWORD = "${" + MGMT_KEYSTORE_PASSWORD + "}";

    // =================================================================================================
    // assistant methods

    // -------------------------------------------------------------------------------------------------
    private MgmtConstants() {
        throw new UnsupportedOperationException();
    }
}
