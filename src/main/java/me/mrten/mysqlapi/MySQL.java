package me.mrten.mysqlapi;

public class MySQL {

    private ConnectionManager connectionManager;

    public MySQL() {

    }

    /**
     * Get the connection manager.
     *
     * @return the connection manager
     */
    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    /**
     * Connect to a MySQL database.
     * @param host - the host for connecting to the database
     * @param port - the port (by default 3306)
     * @param username - username used to connect to the database
     * @param password - password used to connect to the database
     * @param database - name of the database to connect to
     */
    public void connect(String host, String port, String username, String password, String database) {
        connectionManager = new ConnectionManager(host, port, username, password, database);
    }

    /**
     * Close all connections and the data source.
     */
    public void disconnect() {
        if (connectionManager != null)
            connectionManager.close();
    }
}
