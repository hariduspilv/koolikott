package ee.hm.dop.utils;

public interface ConfigurationProperties {

    // Database
    String DATABASE_URL = "db.url";
    String DATABASE_USERNAME = "db.username";
    String DATABASE_PASSWORD = "db.password";

    // Server
    String SERVER_PORT = "server.port";
    String SERVER_ADDRESS = "server.address";
    String COMMAND_LISTENER_PORT = "command.listener.port";

    // Search
    String SEARCH_SERVER = "search.server";

    // TAAT
    String TAAT_SSO = "taat.sso";
    String TAAT_CONNECTION_ID = "taat.connectionId";
    String TAAT_ASSERTION_CONSUMER_SERVICE_INDEX = "taat.assertionConsumerServiceIndex";

    String TAAT_METADATA_FILEPATH = "taat.metadata.filepath";
    String TAAT_METADATA_ENTITY_ID = "taat.metadata.entityID";

    // Keystore for TAAT and signing user data for estonian publisher
    // repositories
    String KEYSTORE_FILENAME = "keystore.filename";
    String KEYSTORE_PASSWORD = "keystore.password";
    String KEYSTORE_SIGNING_ENTITY_ID = "keystore.signingEntityID";
    String KEYSTORE_SIGNING_ENTITY_PASSWORD = "keystore.signingEntityPassword";

    // Mobile ID
    String MOBILEID_ENDPOINT = "mobileID.endpoint";
    String MOBILEID_SERVICENAME = "mobileID.serviceName";
    String MOBILEID_NAMESPACE_PREFIX = "mobileID.namespace.prefix";
    String MOBILEID_NAMESPACE_URI = "mobileID.namespace.uri";
    String MOBILEID_MESSAGE_TO_DISPLAY = "mobileID.messageToDisplay";

    // EHIS
    String XTEE_NAMESPACE_PREFIX = "xtee.namespace.prefix";
    String XTEE_NAMESPACE_URI = "xtee.namespace.uri";
    String EHIS_ENDPOINT = "ehis.endpoint";
    String EHIS_INSTITUTION = "ehis.institution";
    String EHIS_SYSTEM_NAME = "ehis.system.name";
    String EHIS_SERVICE_NAME = "ehis.service.name";
    String EHIS_REQUESTER_ID_CODE = "ehis.requester.idCode";

    // EKool
    String EKOOL_CLIENT_ID = "ekool.client.id";
    String EKOOL_CLIENT_SECRET = "ekool.client.secret";
    String EKOOL_URL_AUTHORIZE = "ekool.url.authorize";
    String EKOOL_URL_TOKEN = "ekool.url.token";
    String EKOOL_URL_GENERALDATA = "ekool.url.generaldata";

    // Stuudium
    String STUUDIUM_CLIENT_ID = "stuudium.client.id";
    String STUUDIUM_URL_AUTHORIZE = "stuudium.url.authorize";

    String MAX_FILE_SIZE = "file.upload.max.size";
}
