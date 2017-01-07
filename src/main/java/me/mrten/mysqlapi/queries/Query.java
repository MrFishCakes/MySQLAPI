package me.mrten.mysqlapi.queries;

import com.sun.rowset.CachedRowSetImpl;
import me.mrten.mysqlapi.MySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Query {

    private MySQL mysql;
    private Connection connection;
    private PreparedStatement statement;

    public Query(MySQL mysql, String sql) throws SQLException {
        this.mysql = mysql;
        connection = mysql.getConnectionManager().getConnection();
        statement = connection.prepareStatement(sql);
    }

    /**
     * Set a parameter of the prepared statement.
     * <p>
     * Parameters are defined using ? in the SQL query.
     *
     * @param index the index of the parameter to set (starts with 1)
     * @param value the value to set the parameter to
     */
    public void setParameter(int index, Object value) throws SQLException {
        statement.setObject(index, value);
    }

    /**
     * Add the current statement to the batch.
     */
    public void addBatch() throws SQLException {
        if (connection.getAutoCommit()) {
            connection.setAutoCommit(false);
        }
        statement.addBatch();
    }

    /**
     * Execute a SQL query that does not return a ResultSet.
     *
     * @return number of rows changed
     */
    public int executeUpdate() throws SQLException {
        try {
            return statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }

            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * Execute a SQL query that does return a ResultSet.
     * <p>
     * Uses a CachedRowSetImpl that is not connected to the database.
     *
     * @return the ResultSet
     */
    public ResultSet executeQuery() throws SQLException {
        CachedRowSetImpl rowSet = new CachedRowSetImpl();
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery();
            rowSet.populate(resultSet);
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connection != null) {
                connection.close();
            }
        }
        return rowSet;
    }

    /**
     * Execute a batch that does not return a ResultSet.
     *
     * @return an array with updates rows
     */
    public int[] executeBatch() throws SQLException {
        try {
            return statement.executeBatch();
        } finally {
            if (statement != null) {
                statement.close();
            }

            if (connection != null) {
                connection.commit();
                connection.close();
            }
        }
    }

    /**
     * Execute a SQL query that does not return a ResultSet asynchronously.
     * <p>
     * The query will be run in a seperate thread.
     *
     * @param callback the callback to be executed once the query is done
     */
    public void executeUpdateAsync(final Callback<Integer, SQLException> callback) {
        mysql.getThreadPool().submit(new Runnable() {

            public void run() {
                try {
                    int rowsChanged = executeUpdate();
                    if (callback != null) {
                        callback.call(rowsChanged, null);
                    }
                } catch (SQLException e) {
                    if (callback != null) {
                        callback.call(0, e);
                    } else {
                        e.printStackTrace();
                    }
                }
            }

        });
    }

    /**
     * Execute a SQL query that does not return a ResultSet asynchronously.
     * <p>
     * The query will be run in a separate thread.
     */
    public void executeUpdateAsync() {
        executeUpdateAsync(null);
    }

    /**
     * Execute a SQL query that does return a ResultSet asynchronously.
     * <p>
     * The query will be run in a separate thread.
     *
     * @param callback the callback to be executed once the query is done
     */
    public void executeQueryAsync(final Callback<ResultSet, SQLException> callback) {
        mysql.getThreadPool().submit(new Runnable() {

            public void run() {
                try {
                    ResultSet rs = executeQuery();
                    callback.call(rs, null);
                } catch (SQLException e) {
                    callback.call(null, e);
                }
            }

        });
    }

    /**
     * Execute a batch that does not return a ResultSet asynchronously.
     * <p>
     * The query will be run in a separate thread.
     */
    public void executeBatchAsync() {
        executeBatchAsync(null);
    }

    /**
     * Execute a batch that does not return a ResultSet asynchronously.
     * <p>
     * The query will be run in a separate thread.
     *
     * @param callback the callback to be executed once the query is done
     */
    public void executeBatchAsync(final Callback<int[], SQLException> callback) {
        mysql.getThreadPool().submit(new Runnable() {

            public void run() {
                try {
                    int[] rowsChanged = executeBatch();
                    if (callback != null) {
                        callback.call(rowsChanged, null);
                    }
                } catch (SQLException e) {
                    if (callback != null) {
                        callback.call(null, e);
                    }
                }
            }

        });
    }

    /**
     * Rollback the transaction.
     */
    public void rollback() throws SQLException {
        if (connection != null) {
            connection.rollback();
        }
    }

}
