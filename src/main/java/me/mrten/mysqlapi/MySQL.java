package me.mrten.mysqlapi;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MySQL {

    private ConnectionManager connectionManager;
    private ExecutorService threadPool;

    /**
     * Create a new MySQL object with a default of 10 maximum threads.
     */
    public MySQL() {
        threadPool = Executors.newFixedThreadPool(10);
    }

    /**
     * Create a new MySQL object.
     *
     * @param maxThreads maximum number of threads to be created
     */
    public MySQL(int maxThreads) {
        threadPool = Executors.newFixedThreadPool(maxThreads);
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
     * Get the thread pool.
     *
     * @return the thread pool
     */
    public ExecutorService getThreadPool() {
        return threadPool;
    }

    /**
     * Connect to a MySQL database.
     *
     * @param host     the host for connecting to the database
     * @param port     the port (by default 3306)
     * @param username username used to connect to the database
     * @param password password used to connect to the database
     * @param database name of the database to connect to
     * @return TRUE if connection was successful, FALSE if an error occurred
     */
    public boolean connect(String host, String port, String username, String password, String database) {
        connectionManager = new ConnectionManager(host, port, username, password, database);
        return connectionManager.open();
    }

    /**
     * Close all connections and the data source.
     */
    public void disconnect() {
        if (connectionManager != null)
            connectionManager.close();

        if (threadPool != null)
            threadPool.shutdown();
    }
}
