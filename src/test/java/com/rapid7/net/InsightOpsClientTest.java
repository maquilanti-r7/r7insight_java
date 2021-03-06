package com.rapid7.net;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InsightOpsClientTest {

    private static final String API_TOKEN_SERVER = "%s.data.logs.insight.rapid7.com";
    private static final String DATAHUB_IP = "127.0.0.1";
    private static final int IOPS_SSL_PORT = 443;
    private static final int IOPS_PORT = 80;
    private static final int DATAHUB_PORT = 10000;
    private static final boolean USE_HTTP_PUT = true;
    private static final boolean NOT_USE_HTTP_PUT = false;
    private static final boolean USE_SSL = true;
    private static final boolean NOT_USE_SSL = false;
    private static final boolean USE_DATAHUB = true;
    private static final boolean NOT_USE_DATAHUB = false;

    @Test
    public void testGetAddress() {
        String region = "eu";
        InsightOpsClient client = new InsightOpsClient(USE_HTTP_PUT, NOT_USE_SSL, NOT_USE_DATAHUB, "", 0, region);
        String expectedApiServer = String.format(API_TOKEN_SERVER, region);
        assertEquals(expectedApiServer  + " should be used for HTTP PUT", client.getAddress(), expectedApiServer);

        String expectedDataServer = String.format(API_TOKEN_SERVER, region);
        InsightOpsClient client2 = new InsightOpsClient(NOT_USE_HTTP_PUT, NOT_USE_SSL, NOT_USE_DATAHUB, "", 0, region);
        assertEquals(expectedDataServer + " should be used for Token TCP", client2.getAddress(), expectedDataServer);

        InsightOpsClient client3 = new InsightOpsClient(USE_HTTP_PUT, USE_SSL, USE_DATAHUB, "127.0.0.1", 10000, region);
        assertEquals("Address 127.0.0.1 should be used over " + expectedApiServer, client3.getAddress(), DATAHUB_IP);

        InsightOpsClient client4 = new InsightOpsClient(USE_HTTP_PUT, NOT_USE_SSL, USE_DATAHUB, "127.0.0.1", 10000, region);
        assertEquals("Address 127.0.0.1 should be used over " + expectedApiServer, client4.getAddress(), DATAHUB_IP);

        InsightOpsClient client5 = new InsightOpsClient(NOT_USE_HTTP_PUT, USE_SSL, USE_DATAHUB, "127.0.0.1", 10000, region);
        assertEquals("Address 127.0.0.1 should be used over " + expectedApiServer, client5.getAddress(), DATAHUB_IP);

        InsightOpsClient client6 = new InsightOpsClient(NOT_USE_HTTP_PUT, NOT_USE_SSL, USE_DATAHUB, "127.0.0.1", 10000, region);
        assertEquals("Address 127.0.0.1 should be used over " + expectedApiServer, client6.getAddress(), DATAHUB_IP);

        InsightOpsClient client7 = new InsightOpsClient(NOT_USE_HTTP_PUT, USE_SSL, NOT_USE_DATAHUB, "127.0.0.1", 10000, region);
        assertEquals("Address 127.0.0.1 should be used over " + expectedApiServer, client5.getAddress(), DATAHUB_IP);

        InsightOpsClient client8 = new InsightOpsClient(NOT_USE_HTTP_PUT, NOT_USE_SSL, NOT_USE_DATAHUB, "127.0.0.1", 10000, region);
        assertEquals("Address 127.0.0.1 should be used over " + expectedApiServer, client6.getAddress(), DATAHUB_IP);
    }

    @Test
    public void testGetPort() {
        InsightOpsClient client = new InsightOpsClient(USE_HTTP_PUT, USE_SSL, USE_DATAHUB, "127.0.0.1", 10000, "eu");
        assertEquals("Port 10000 should be used over 443", client.getPort(), DATAHUB_PORT);

        InsightOpsClient client2 = new InsightOpsClient(NOT_USE_HTTP_PUT, NOT_USE_SSL, USE_DATAHUB, "127.0.0.1", 10000, "eu");
        assertEquals("Port 10000 should be used over 80", client2.getPort(), DATAHUB_PORT);

        InsightOpsClient client3 = new InsightOpsClient(USE_HTTP_PUT, USE_SSL, NOT_USE_DATAHUB, "", 0, "");
        assertEquals("Port 443 should be used for SSL over HTTP", client3.getPort(), IOPS_SSL_PORT);

        InsightOpsClient client4 = new InsightOpsClient(USE_HTTP_PUT, NOT_USE_SSL, NOT_USE_DATAHUB, "", 0, "");
        assertEquals("Port 80 should be used for HTTP PUT", client4.getPort(), IOPS_PORT);

        InsightOpsClient client5 = new InsightOpsClient(NOT_USE_HTTP_PUT, USE_SSL, NOT_USE_DATAHUB, "", 0, "");
        assertEquals("Port 443 should be used for SSL over Token TCP", client5.getPort(), IOPS_SSL_PORT);

        InsightOpsClient client6 = new InsightOpsClient(NOT_USE_HTTP_PUT, NOT_USE_SSL, NOT_USE_DATAHUB, "", 0, "");
        assertEquals("Port 80 should be used for Token TCP", client6.getPort(), IOPS_PORT);

        InsightOpsClient client7 = new InsightOpsClient(NOT_USE_HTTP_PUT, NOT_USE_SSL, NOT_USE_DATAHUB, "", 10000, "");
        assertEquals("Port 10000 should be used because specified in the configuration", client7.getPort(), 10000);

        InsightOpsClient client8 = new InsightOpsClient(NOT_USE_HTTP_PUT, USE_SSL, NOT_USE_DATAHUB, "", 10000, "");
        assertEquals("Port 10000 should be used because specified in the configuration", client8.getPort(), 10000);
    }

}
