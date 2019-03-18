package ee.hm.dop.utils;

public interface ConfigurationProperties {

    // Database
    String DATABASE_URL = "db.url";
    String DATABASE_USERNAME = "db.username";
    String DATABASE_PASSWORD = "db.password";

    // Server
    String SERVER_PORT = "server.port";
    String SERVER_ADDRESS = "server.addressurl";
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

    // EHIS V5
    String XTEE_NAMESPACE_PREFIX = "xtee.namespace.prefix";
    String XTEE_NAMESPACE_URI = "xtee.namespace.uri";
    String EHIS_ENDPOINT = "ehis.endpoint";
    String EHIS_INSTITUTION = "ehis.institution";
    String EHIS_SYSTEM_NAME = "ehis.system.name";
    String EHIS_SERVICE_NAME = "ehis.service.name";

    // EHIS V6
    String XROAD_EHIS_USE_V6 = "xroad.ehis.use.v6";
    String XROAD_EHIS_V6_NAMESPACE_XTEE_URI = "xroad.ehis.v6.namespace.xtee.uri";
    String XROAD_EHIS_V6_NAMESPACE_XTEE_PREFIX = "xroad.ehis.v6.namespace.xtee.prefix";
    String XROAD_EHIS_V6_NAMESPACE_XRO_URI = "xroad.ehis.v6.namespace.xro.uri";
    String XROAD_EHIS_V6_NAMESPACE_XRO_PREFIX = "xroad.ehis.v6.namespace.xro.prefix";
    String XROAD_EHIS_V6_NAMESPACE_IDEN_URI = "xroad.ehis.v6.namespace.iden.uri";
    String XROAD_EHIS_V6_NAMESPACE_IDEN_PREFIX = "xroad.ehis.v6.namespace.iden.prefix";
    String XROAD_EHIS_V6_NAMESPACE_EHIS_URI = "xroad.ehis.v6.namespace.ehis.uri";
    String XROAD_EHIS_V6_NAMESPACE_EHIS_PREFIX = "xroad.ehis.v6.namespace.ehis.prefix";
    String XROAD_EHIS_V6_ENDPOINT = "xroad.ehis.v6.endpoint";
    String XROAD_EHIS_V6_HEADER_PROTOCOL = "xroad.ehis.v6.header.protocol";
    String XROAD_EHIS_V6_HEADER_ISSUE = "xroad.ehis.v6.header.issue";
    String XROAD_EHIS_V6_SERVICE_INSTACE = "xroad.ehis.v6.service.instance";
    String XROAD_EHIS_V6_SERVICE_MEMBER_CLASS = "xroad.ehis.v6.service.member.class";
    String XROAD_EHIS_V6_SERVICE_MEMBER_CODE = "xroad.ehis.v6.service.member.code";
    String XROAD_EHIS_V6_SERVICE_SUBSYSTEM_CODE = "xroad.ehis.v6.service.subsystem.code";
    String XROAD_EHIS_V6_SERVICE_SERVICE_NAME = "xroad.ehis.v6.service.service.name";
    String XROAD_EHIS_V6_SERVICE_SERVICE_VERSION = "xroad.ehis.v6.service.version";
    String XROAD_EHIS_V6_SUBSYSTEM_INSTANCE = "xroad.ehis.v6.subsystem.instance";
    String XROAD_EHIS_V6_SUBSYSTEM_MEMBER_CLASS = "xroad.ehis.v6.subsystem.member.class";
    String XROAD_EHIS_V6_SUBSYSTEM_MEMBER_CODE = "xroad.ehis.v6.subsystem.member.code";
    String XROAD_EHIS_V6_SUBSYSTEM_SUBSYSTEM_CODE = "xroad.ehis.v6.subsystem.subsystem.code";
    String XROAD_EHIS_TIMEOUT_CONNECT = "xroad.ehis.timeout.connect";
    String XROAD_EHIS_TIMEOUT_READ = "xroad.ehis.timeout.read";

    String XROAD_EHIS_INSTITUTIONS_LIST = "xroad.ehis.institutions.list";

    // EKool
    String EKOOL_EXTRA_LOGGING = "ekool.extra.logging";
    String EKOOL_CLIENT_ID = "ekool.client.id";
    String EKOOL_CLIENT_SECRET = "ekool.client.secret";
    String EKOOL_URL_AUTHORIZE = "ekool.url.authorize";
    String EKOOL_URL_TOKEN = "ekool.url.token";
    String EKOOL_URL_GENERALDATA = "ekool.url.generaldata";

    // Stuudium
    String STUUDIUM_EXTRA_LOGGING = "stuudium.extra.logging";
    String STUUDIUM_CLIENT_ID = "stuudium.client.id";
    String STUUDIUM_CLIENT_SECRET = "stuudium.client.secret";
    String STUUDIUM_URL_AUTHORIZE = "stuudium.url.authorize";
    String STUUDIUM_URL_GENERALDATA = "stuudium.url.generaldata";

    String MAX_FILE_SIZE = "file.upload.max.size";
    String FILE_UPLOAD_DIRECTORY = "file.upload.directory";
    String DOCUMENT_MAX_FILE_SIZE = "file.document.upload.max.size";
    String FILE_REVIEW_DIRECTORY = "file.review.directory";

    String REQUEST_EXTRA_LOGGING = "request.extra.logging";
    String MAX_FEED_ITEMS = "feed.items.max";
    String AUTOMATICALLY_ACCEPT_REVIEWABLE_CHANGES = "automatically.accept.reviewableChanges.days";
    String SESSION_DURATION_MINS = "session.duration.mins";
    String SESSION_ALERT_MINS = "session.alert.mins";

    // E-mail
    String EMAIL_ADDRESS = "email.address";
    String EMAIL_NO_REPLY_ADDRESS = "email.noreply.address";
    String EMAIL_HOST = "email.host";
    String EMAIL_PORT = "email.port";
    String EMAIL_USERNAME = "email.username";
    String EMAIL_PASSWORD = "email.password";
    String EMAIL_TRANSPORT_STRATEGY = "email.transport.strategy";

    //Captcha
    String CAPTCHA_KEY = "captcha.key";

    String EXECUTOR_corePoolSize = "cron.executor.corePoolSize";
    String EXECUTOR_maxPoolSize = "cron.executor.maxPoolSize";
    String EXECUTOR_queueCapacity = "cron.executor.queueCapacity";
    String EXECUTOR_threadNamePrefix = "cron.executor.threadNamePrefix";

}
