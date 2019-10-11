package com.rapid7.logback;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.pattern.SyslogStartConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.net.SyslogConstants;
import com.rapid7.net.AsyncLogger;

/**
 * Logentries appender for logback.
 *
 * @author Mark Lacomber
 * @author Ben McCann
 * @author Chris Mowforth
 */
public class LogentriesAppender extends AppenderBase<ILoggingEvent> {

    /**
     * Default Suffix Pattern
     */
    public static final String DEFAULT_SUFFIX_PATTERN = "[%thread] %logger %msg";
    /**
     * Asynchronous Background logger
     */
    private final AsyncLogger iopsAsync;
    /**
     * Layout
     */
    private Layout<ILoggingEvent> layout;
    /**
     * Facility String
     */
    private String facilityStr;
    private String suffixPattern;

    /**
     * Creates a new Logentries appender.
     */
    public LogentriesAppender() {
        this.iopsAsync = new AsyncLogger();
    }

    /**
     * Creates a new Logentries appender.
     * <p>Used for unit testing.</p>
     *
     * @param logger the {@link AsyncLogger} to dispatch events
     */
    public LogentriesAppender(AsyncLogger logger) {
        this.iopsAsync = logger;
    }

	/*
     * Public methods to send logback parameters to AsyncLogger
	 */

    /**
     * Sets the token.
     *
     * @param token
     */
    public void setToken(String token) {
        this.iopsAsync.setToken(token);
    }

    /**
     * Sets the region.
     *
     * @param region
     */
    public void setRegion(String region) {
        this.iopsAsync.setRegion(region);
    }

    /**
     * Sets the HTTP PUTflag. <p>Send logs via HTTP PUT instead of default Token
     * TCP.</p>
     *
     * @param HttpPut true to use HTTP PUT API
     */
    public void setHttpPut(boolean HttpPut) {
        this.iopsAsync.setHttpPut(HttpPut);
    }

    /**
     * Sets the ACCOUNT KEY value for HTTP PUT.
     *
     * @param account_key
     */
    public void setKey(String account_key) {
        this.iopsAsync.setKey(account_key);
    }

    /**
     * Sets the LOCATION value for HTTP PUT.
     *
     * @param log_location
     */
    public void setLocation(String log_location) {
        this.iopsAsync.setLocation(log_location);
    }

    /**
     * Sets the SSL boolean flag
     *
     * @param ssl
     */
    public void setSsl(boolean ssl) {
        this.iopsAsync.setSsl(ssl);
    }

    /**
     * Sets the debug flag.
     * <p>
     * <p>Appender in debug mode will print error messages on error console.</p>
     *
     * @param debug debug flag to set
     */
    public void setDebug(boolean debug) {
        this.iopsAsync.setDebug(debug);
    }

    /**
     * Sets the flag which determines if DataHub instance is used instead of Logentries service.
     *
     * @param useDataHub set to true to send log messaged to a DataHub instance.
     */
    public void setIsUsingDataHub(boolean useDataHub) {
        this.iopsAsync.setUseDataHub(useDataHub);
    }

    /**
     * Sets the address where DataHub server resides.
     *
     * @param dataHubAddr address like "127.0.0.1"
     */
    public void setDataHubAddr(String dataHubAddr) {
        this.iopsAsync.setDataHubAddr(dataHubAddr);
    }

    /**
     * Sets the port number on which DataHub instance waits for log messages.
     *
     * @param dataHubPort
     */
    public void setDataHubPort(int dataHubPort) {
        this.iopsAsync.setDataHubPort(dataHubPort);
    }

    /**
     * Determines whether to send HostName alongside with the log message
     *
     * @param logHostName
     */
    public void setLogHostName(boolean logHostName) {
        this.iopsAsync.setLogHostName(logHostName);
    }

    /**
     * Sets the HostName from the configuration
     *
     * @param hostName
     */
    public void setHostName(String hostName) {
        this.iopsAsync.setHostName(hostName);
    }

    /**
     * Sets LogID parameter from the configuration
     *
     * @param logID
     */
    public void setLogID(String logID) {
        this.iopsAsync.setLogID(logID);
    }

    /**
     * Sets the suffixPattern to be the <pattern> field in the .xml configuration file
     *
     * @param encoder
     */
    public void setEncoder(PatternLayoutEncoder encoder) {
        this.suffixPattern = encoder.getPattern();
    }

    @Override
    public void start() {
        if (layout == null) {
            layout = buildLayout();
        }
        super.start();
    }

    String getPrefixPattern() {
        return "%syslogStart{" + getFacility() + "}%nopex";
    }

    /**
     * Returns the string value of the <b>Facility</b> option.
     * <p>
     * See {@link #setFacility} for the set of allowed values.
     */
    public String getFacility() {
        return facilityStr;
    }

    /**
     * The <b>Facility</b> option must be set one of the strings KERN, USER,
     * MAIL, DAEMON, AUTH, SYSLOG, LPR, NEWS, UUCP, CRON, AUTHPRIV, FTP, NTP,
     * AUDIT, ALERT, CLOCK, LOCAL0, LOCAL1, LOCAL2, LOCAL3, LOCAL4, LOCAL5,
     * LOCAL6, LOCAL7. Case is not important.
     * <p>
     * <p>See {@link SyslogConstants} and RFC 3164 for more information about
     * the <b>Facility</b> option.
     */
    public void setFacility(String facilityStr) {
        if (facilityStr != null) {
            facilityStr = facilityStr.trim();
        }
        this.facilityStr = facilityStr;
    }

    public Layout<ILoggingEvent> getLayout() {
        return layout;
    }

    /**
     * Sets the layout for the Appender
     */
    public void setLayout(Layout<ILoggingEvent> layout) {
        this.layout = layout;
    }

    /**
     * Implements AppenderSkeleton Append method, handles time and format
     *
     * @event event to log
     */
    @Override
    protected void append(ILoggingEvent event) {
        // Render the event according to layout
        String formattedEvent = layout.doLayout(event);
        // Prepare to be queued
        this.iopsAsync.addLineToQueue(formattedEvent);
    }

    /**
     * Closes all connections to Logentries
     */
    @Override
    public void stop() {
        super.stop();
        this.iopsAsync.close();
    }

    public Layout<ILoggingEvent> buildLayout() {
        PatternLayout l = new PatternLayout();
        l.getInstanceConverterMap().put("syslogStart", SyslogStartConverter.class.getName());
        if (suffixPattern == null) {
            suffixPattern = DEFAULT_SUFFIX_PATTERN;
        }
        l.setPattern(getPrefixPattern() + suffixPattern);
        l.setContext(getContext());
        l.start();
        return l;
    }

    /**
     * See {@link #setSuffixPattern(String).
     *
     * @return
     */
    public String getSuffixPattern() {
        return suffixPattern;
    }

    /**
     * The <b>suffixPattern</b> option specifies the format of the
     * non-standardized part of the message sent to the syslog server.
     *
     * @param suffixPattern
     */
    public void setSuffixPattern(String suffixPattern) {
        this.suffixPattern = suffixPattern;
    }
}
