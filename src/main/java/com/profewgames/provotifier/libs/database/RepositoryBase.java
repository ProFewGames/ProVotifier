package com.profewgames.provotifier.libs.database;

import com.profewgames.provotifier.libs.NautHashMap;
import com.profewgames.provotifier.libs.database.column.Column;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public abstract class RepositoryBase {

    // Queue for failed processes
    private static Object _queueLock = new Object();
    private NautHashMap<DatabaseRunnable, String> _failedQueue = new NautHashMap<DatabaseRunnable, String>();

    private DataSource _dataSource;    // Connection pool

    /**
     * Constructor
     *
     * @param bungee     - the {@link Boolean} is this repository going to be running under a bungeecord software?
     * @param dataSource - the {@link DataSource} responsible for providing the connection pool to this repository.
     */
    public RepositoryBase(boolean bungee, DataSource dataSource) {
        _dataSource = dataSource;

        if (bungee) {
            net.md_5.bungee.api.ProxyServer.getInstance().getScheduler().runAsync(net.md_5.bungee.api.ProxyServer.getInstance().getPluginManager().getPlugin("ProVotifier"), () -> {
                initialize();
                update();
            });
            net.md_5.bungee.api.ProxyServer.getInstance().getScheduler().schedule(net.md_5.bungee.api.ProxyServer.getInstance().getPluginManager().getPlugin("ProVotifier"), this::processDatabaseQueue, 1, TimeUnit.MINUTES);
        } else {
            org.bukkit.Bukkit.getScheduler().runTaskAsynchronously(org.bukkit.Bukkit.getPluginManager().getPlugin("ProVotifier"), () -> {
                initialize();
                update();
            });
            org.bukkit.Bukkit.getScheduler().scheduleSyncRepeatingTask(org.bukkit.Bukkit.getPluginManager().getPlugin("ProVotifier"), this::processDatabaseQueue, 1200, 1200);
        }
    }

    protected abstract void initialize();

    protected abstract void update();

    /**
     * @return the {@link DataSource} used by the repository for connection pooling.
     */
    protected DataSource getConnectionPool() {
        return _dataSource;
    }

    /**
     * Requirements: {@link Connection}s must be closed after usage so they may be returned to the pool!
     *
     * @return a newly fetched {@link Connection} from the connection pool, if a connection can be made, null otherwise.
     * @see Connection#close()
     */
    protected Connection getConnection() {
        try {
            return _dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO: Log connection failures?
            return null;
        }
    }

    /**
     * Execute a query against the repository.
     *
     * @param query   - the concatenated query to execute in string form.
     * @param columns - the column data values used for insertion into the query.
     * @return the number of rows affected by this query in the repository.
     */
    protected int executeUpdate(String query, Column<?>... columns) {
        return executeInsert(query, null, columns);
    }

    protected int executeInsert(String query, ResultSetCallable callable, Column<?>... columns) {
        int affectedRows = 0;

        // Automatic resource management for handling/closing objects.
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
        ) {
            for (int i = 0; i < columns.length; i++) {
                columns[i].setValue(preparedStatement, i + 1);
            }

            affectedRows = preparedStatement.executeUpdate();

            if (callable != null) {
                callable.processResultSet(preparedStatement.getGeneratedKeys());
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return affectedRows;
    }

    protected void executeQuery(PreparedStatement statement, ResultSetCallable callable, Column<?>... columns) {
        try {
            for (int i = 0; i < columns.length; i++) {
                columns[i].setValue(statement, i + 1);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                callable.processResultSet(resultSet);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected void executeQuery(String query, ResultSetCallable callable, Column<?>... columns) {
        // Automatic resource management for handling/closing objects.
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            executeQuery(preparedStatement, callable, columns);
        } catch (SQLException exception) {
            exception.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected void handleDatabaseCall(final DatabaseRunnable databaseRunnable, final String errorMessage) {
        Thread asyncThread = new Thread(new Runnable() {
            public void run() {
                try {
                    databaseRunnable.run();
                } catch (Exception exception) {
                    processFailedDatabaseCall(databaseRunnable, exception.getMessage(), errorMessage);
                }
            }
        });

        asyncThread.start();
    }

    protected void processFailedDatabaseCall(DatabaseRunnable databaseRunnable, String errorPreMessage, String runnableMessage) {
        if (databaseRunnable.getFailedCounts() < 4) {
            databaseRunnable.incrementFailCount();

            synchronized (_queueLock) {
                _failedQueue.put(databaseRunnable, runnableMessage);
            }
        }
    }

    public void processDatabaseQueue() {
        processFailedQueue();
    }

    private void processFailedQueue() {
        synchronized (_queueLock) {
            for (Iterator<DatabaseRunnable> runnablesIterator = _failedQueue.keySet().iterator(); runnablesIterator.hasNext(); ) {
                DatabaseRunnable databaseRunnable = runnablesIterator.next();
                handleDatabaseCall(databaseRunnable, _failedQueue.get(databaseRunnable));
            }
        }
    }
}