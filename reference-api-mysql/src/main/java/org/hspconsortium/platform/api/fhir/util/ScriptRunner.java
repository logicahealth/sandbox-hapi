package org.hspconsortium.platform.api.fhir.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tool to run database scripts
 */
public class ScriptRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptRunner.class);

    private static final String DEFAULT_DELIMITER = ";";
    /**
     * regex to detect delimiter.
     * ignores spaces, allows delimiter in comment, allows an equals-sign
     */
    public static final Pattern delimP = Pattern.compile("^\\s*(--)?\\s*delimiter\\s*=?\\s*([^\\s]+)+\\s*.*$", Pattern.CASE_INSENSITIVE);

    private final Connection connection;

    private final boolean stopOnError;

    private final boolean autoCommit;

    private final boolean logCommands;

    private final boolean abbreviated = true;

    private String delimiter = DEFAULT_DELIMITER;

    private boolean fullLineDelimiter = false;

    /**
     * Default constructor
     */
    public ScriptRunner(Connection connection, boolean autoCommit,
                        boolean stopOnError, boolean logCommands) {
        this.connection = connection;
        this.autoCommit = autoCommit;
        this.stopOnError = stopOnError;
        this.logCommands = logCommands;
    }

    public void setDelimiter(String delimiter, boolean fullLineDelimiter) {
        this.delimiter = delimiter;
        this.fullLineDelimiter = fullLineDelimiter;
    }

    /**
     * Runs an SQL script (read in using the Reader parameter)
     *
     * @param reader - the source of the script
     */
    public void runScript(Reader reader) throws IOException, SQLException {
        try {
            boolean originalAutoCommit = connection.getAutoCommit();
            try {
                if (originalAutoCommit != this.autoCommit) {
                    connection.setAutoCommit(this.autoCommit);
                }
                runScript(connection, reader);
            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }
        } catch (IOException | SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error running script.  Cause: " + e, e);
        }
    }

    /**
     * Runs an SQL script (read in using the Reader parameter) using the
     * connection passed in
     *
     * @param conn   - the connection to use for the script
     * @param reader - the source of the script
     * @throws SQLException if any SQL errors occur
     * @throws IOException  if there is an error reading from the Reader
     */
    private void runScript(Connection conn, Reader reader) throws IOException,
            SQLException {
        StringBuffer command = null;
        try {
            LineNumberReader lineReader = new LineNumberReader(reader);
            String line;
            while ((line = lineReader.readLine()) != null) {
                if (command == null) {
                    command = new StringBuffer();
                }
                String trimmedLine = line.trim();
                final Matcher delimMatch = delimP.matcher(trimmedLine);
                if (trimmedLine.length() < 1
                        || trimmedLine.startsWith("//")) {
                    // Do nothing
                } else if (delimMatch.matches()) {
                    setDelimiter(delimMatch.group(2), false);
                } else if (trimmedLine.startsWith("--")) {
                    log(trimmedLine);
                } else if (trimmedLine.length() < 1
                        || trimmedLine.startsWith("--")) {
                    // Do nothing
                } else if (!fullLineDelimiter
                        && trimmedLine.endsWith(getDelimiter())
                        || fullLineDelimiter
                        && trimmedLine.equals(getDelimiter())) {
                    command.append(line.substring(0, line
                            .lastIndexOf(getDelimiter())));
                    command.append(" ");
                    this.execCommand(conn, command, lineReader);
                    command = null;
                } else {
                    command.append(line);
                    command.append("\n");
                }
            }
            if (command != null) {
                this.execCommand(conn, command, lineReader);
            }
            if (!autoCommit) {
                conn.commit();
            }
        } catch (Exception e) {
            throw new IOException(String.format("Error executing '%s': %s", command, e.getMessage()), e);
        }
    }

    private void execCommand(Connection conn, StringBuffer command,
                             LineNumberReader lineReader) throws SQLException {
        if (command.length() < 1) {
            return;
        }

        Statement statement = conn.createStatement();

        log(command.toString());

        boolean hasResults = false;
        try {
            hasResults = statement.execute(command.toString());
        } catch (SQLException e) {
            final String errText = String.format("Error executing '%s' (line %d): %s", command, lineReader.getLineNumber(), e.getMessage());
            if (stopOnError) {
                throw new SQLException(errText, e);
            } else {
                error(errText);
            }
        }

        if (autoCommit && !conn.getAutoCommit()) {
            conn.commit();
        }

        ResultSet rs = statement.getResultSet();
        if (hasResults && rs != null) {
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 1; i <= cols; i++) {
                String name = md.getColumnLabel(i);
                stringBuffer.append(name);
                stringBuffer.append("\t");
            }
            log(stringBuffer.toString());
            while (rs.next()) {
                stringBuffer.setLength(0);
                for (int i = 1; i <= cols; i++) {
                    stringBuffer.append(rs.getString(i));
                    stringBuffer.append("\t");
                }
                log(stringBuffer.toString());
            }
        }

        try {
            statement.close();
        } catch (Exception e) {
            LOGGER.error("Error closing statement", e);
            // Ignore to workaround a bug in Jakarta DBCP
        }
    }

    private String getDelimiter() {
        return delimiter;
    }

    private void log(String message) {
        if (logCommands) {
            if (LOGGER.isInfoEnabled()) {
                if (abbreviated && message.length() > 30) {
                    LOGGER.info(message.substring(0, 30) + "...");
                } else {
                    LOGGER.info(message);
                }
            }
        }
    }

    private void error(String message) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn(message);
        }
    }

}
