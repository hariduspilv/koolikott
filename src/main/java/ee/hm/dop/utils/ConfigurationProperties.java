package ee.hm.dop.utils;

public interface ConfigurationProperties {

    // Database
    String DATABASE_URL = "db.url";
    String DATABASE_USERNAME = "db.username";
    String DATABASE_PASSWORD = "db.password";

    // Server
    String SERVER_PORT = "server.port";
    String COMMAND_LISTENER_PORT = "command.listener.port";
}
