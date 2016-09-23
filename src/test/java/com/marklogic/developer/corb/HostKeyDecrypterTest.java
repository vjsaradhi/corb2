/*
 * Copyright (c) 2004-2016 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The use of the Apache License does not indicate that this project is
 * affiliated with the Apache Software Foundation.
 */
package com.marklogic.developer.corb;

import com.marklogic.developer.TestHandler;
import static com.marklogic.developer.corb.TestUtils.clearSystemProperties;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Mads Hanse, MarkLogic Corporation
 */
public class HostKeyDecrypterTest {

    private final TestHandler testLogger = new TestHandler();
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private PrintStream systemOut = System.out;
    private PrintStream systemErr = System.err;
    private static final String USAGE = HostKeyDecrypter.USAGE + "\n";
    private static final Logger LOG = Logger.getLogger(HostKeyDecrypterTest.class.getName());
    private static final Logger HOST_KEY_DECRYPTER_LOG = Logger.getLogger(HostKeyDecrypter.class.getName());

    @Before
    public void setUp() {
        clearSystemProperties();
        HOST_KEY_DECRYPTER_LOG.addHandler(testLogger);
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void tearDown() {
        clearSystemProperties();
        System.setOut(systemOut);
        System.setErr(systemErr);
    }

    @Test
    public void testInitDecrypter() {
        HostKeyDecrypter instance = new HostKeyDecrypter();
        try {
            instance.init_decrypter();
            List<LogRecord> records = testLogger.getLogRecords();
            assertTrue("Initialized HostKeyDecrypter".equals(records.get(0).getMessage()));
        } catch (IOException ex) {
            //travis-ci throws an IOException
            LOG.log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }

    @Test
    public void testXor() {
        byte[] byteOne = {1, 0, 1, 0, 1};
        byte[] byteTwo = {1, 1, 0, 0};
        byte[] expResult = {0, 1, 1, 0, 1};
        byte[] result = HostKeyDecrypter.xor(byteOne, byteTwo);
        assertTrue(Arrays.equals(expResult, result));
    }

    @Test
    public void testGetSHA256Hash() {
        byte[] expected = {-75, -44, 4, 92, 63, 70, 111, -87, 31, -30, -52, 106, -66, 121, 35, 42, 26, 87, -51, -15, 4, -9, -94, 110, 113, 110, 10, 30, 39, -119, -33, 120};
        byte[] input = {'A', 'B', 'C'};
        try {
            byte[] result = HostKeyDecrypter.getSHA256Hash(input);
            System.out.println(Arrays.toString(result));
            assertTrue(Arrays.equals(expected, result));
        } catch (NoSuchAlgorithmException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testGetOperatingSystemType() {
        assertEquals(HostKeyDecrypter.OSType.MAC, HostKeyDecrypter.getOperatingSystemType("Darwin"));
        assertEquals(HostKeyDecrypter.OSType.WINDOWS, HostKeyDecrypter.getOperatingSystemType("Windows 95"));
        assertEquals(HostKeyDecrypter.OSType.WINDOWS, HostKeyDecrypter.getOperatingSystemType("windows xp"));
        assertEquals(HostKeyDecrypter.OSType.LINUX, HostKeyDecrypter.getOperatingSystemType("unix"));
        assertEquals(HostKeyDecrypter.OSType.LINUX, HostKeyDecrypter.getOperatingSystemType("Red Had Linux"));
        assertEquals(HostKeyDecrypter.OSType.LINUX, HostKeyDecrypter.getOperatingSystemType("AIX/OS2"));
        assertEquals(HostKeyDecrypter.OSType.OTHER, HostKeyDecrypter.getOperatingSystemType("ReactOS"));
        assertEquals(HostKeyDecrypter.OSType.OTHER, HostKeyDecrypter.getOperatingSystemType(null));
    }

    @Test
    public void testGetValueFollowingMarker() {
        BufferedReader br = new BufferedReader(new StringReader("Serial Number xyz-123"));
        try {
            Class<?> clazz = HostKeyDecrypter.OSType.class;
            Method method = clazz.getDeclaredMethod("getValueFollowingMarker", BufferedReader.class, String.class, String.class);
            method.setAccessible(true);
            byte[] serial = (byte[]) method.invoke(clazz, br, "Serial Number", HostKeyDecrypter.OSType.MAC.toString());
            assertTrue("xyz-123".equals(new String(serial)));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }

    @Test
    public void testGetValueFollowingMarkerNotFound() {
        BufferedReader br = new BufferedReader(new StringReader("Serial Number xyz-123"));
        byte[] result = null;
        try {
            Class<?> clazz = HostKeyDecrypter.OSType.class;
            Method method = clazz.getDeclaredMethod("getValueFollowingMarker", BufferedReader.class, String.class, String.class);
            method.setAccessible(true);
            result = (byte[]) method.invoke(clazz, br, "badMarker", HostKeyDecrypter.OSType.MAC.toString());
            fail();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        assertNull(result);
    }

    @Test
    public void testMainUsageNullArgs() {
        String[] args = null;
        try {
            HostKeyDecrypter.main(args);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        assertEquals(USAGE, outContent.toString());
    }

    @Test
    public void testMainUsageEncryptWithoutValue() {
        String[] args = {"encrypt"};
        try {
            HostKeyDecrypter.main(args);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        assertEquals(USAGE, outContent.toString());
    }

    @Test
    public void testMainUsageUnrecognizedMethod() {
        String[] args = {"foo"};
        try {
            HostKeyDecrypter.main(args);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        assertEquals(USAGE, outContent.toString());
    }

    @Test
    public void testDoDecrypt() {
        HostKeyDecrypter decrypter = new HostKeyDecrypter();
        String value = "bar";
        String result = decrypter.decrypt("foo", value);
        assertEquals(value, result);
    }

    /* Illegal key size thrown if JCE is not loaded
    
    @Test
    public void testMain() throws Exception {
        String[] args = {"encrypt", "foo"};
        HostKeyDecrypter.main(args);
    }
    
    @Test
    public void testMain_test() throws Exception {
        String[] args = {"test"};
        HostKeyDecrypter.main(args);
    }
     */
}
