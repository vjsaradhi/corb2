/*
 * * Copyright (c) 2004-2016 MarkLogic Corporation
 * *
 * * Licensed under the Apache License, Version 2.0 (the "License");
 * * you may not use this file except in compliance with the License.
 * * You may obtain a copy of the License at
 * *
 * * http://www.apache.org/licenses/LICENSE-2.0
 * *
 * * Unless required by applicable law or agreed to in writing, software
 * * distributed under the License is distributed on an "AS IS" BASIS,
 * * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * * See the License for the specific language governing permissions and
 * * limitations under the License.
 * *
 * * The use of the Apache License does not indicate that this project is
 * * affiliated with the Apache Software Foundation.
 */
package com.marklogic.developer.corb;

import javax.net.ssl.SSLContext;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Mads Hansen, MarkLogic Corporation
 */
public class TrustAnyoneSSLConfigTest {

    /**
     * Test of getSSLContext method, of class TrustAnyoneSSLConfig.
     */
    @Test
    public void testGetSSLContext() throws Exception {
        TrustAnyoneSSLConfig instance = new TrustAnyoneSSLConfig();
        SSLContext result = instance.getSSLContext();
        assertNotNull(result);
    }

    /**
     * Test of getEnabledCipherSuites method, of class TrustAnyoneSSLConfig.
     */
    @Test
    public void testGetEnabledCipherSuites() {
        TrustAnyoneSSLConfig instance = new TrustAnyoneSSLConfig();
        String[] result = instance.getEnabledCipherSuites();
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    /**
     * Test of getEnabledProtocols method, of class TrustAnyoneSSLConfig.
     */
    @Test
    public void testGetEnabledProtocols() {
        TrustAnyoneSSLConfig instance = new TrustAnyoneSSLConfig();
        String[] result = instance.getEnabledProtocols();
        assertNotNull(result);
        assertEquals(0, result.length);
    }

}
