/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.atlas;

import java.io.InputStream;

import org.apache.commons.configuration.Configuration;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Unit test for {@link ApplicationProperties}
 *
 */
public class ApplicationPropertiesTest {

    @Test
    public void testGetFileAsInputStream() throws Exception {
        Configuration props = ApplicationProperties.get("test.properties");

        // configured file as class loader resource
        InputStream inStr = ApplicationProperties.getFileAsInputStream(props, "jaas.properties.file", null);
        assertNotNull(inStr);

        // configured file from file system path
        props.setProperty("jaas.properties.file", "src/test/resources/atlas-jaas.properties");
        inStr = ApplicationProperties.getFileAsInputStream(props, "jaas.properties.file", null);
        assertNotNull(inStr);

        // default file as class loader resource
        inStr = ApplicationProperties.getFileAsInputStream(props, "property.not.specified.in.config", "atlas-jaas.properties");
        assertNotNull(inStr);

        // default file relative to working directory
        inStr = ApplicationProperties.getFileAsInputStream(props, "property.not.specified.in.config", "src/test/resources/atlas-jaas.properties");
        assertNotNull(inStr);

        // default file relative to atlas configuration directory
        String originalConfDirSetting = System.setProperty(ApplicationProperties.ATLAS_CONFIGURATION_DIRECTORY_PROPERTY, "src/test/resources");
        try {
            inStr = ApplicationProperties.getFileAsInputStream(props, "property.not.specified.in.config", "atlas-jaas.properties");
            assertNotNull(inStr);
        }
        finally {
            if (originalConfDirSetting != null) {
                System.setProperty(ApplicationProperties.ATLAS_CONFIGURATION_DIRECTORY_PROPERTY, originalConfDirSetting);
            }
            else {
                System.clearProperty(ApplicationProperties.ATLAS_CONFIGURATION_DIRECTORY_PROPERTY);
            }
        }

        // non-existent property and no default file
        try {
            ApplicationProperties.getFileAsInputStream(props, "property.not.specified.in.config", null);
            fail("Expected " + AtlasException.class.getSimpleName() + " but none thrown");
        }
        catch (AtlasException e) {
            // good
        }

        // configured file not found in file system or classpath
        props.setProperty("jaas.properties.file", "does_not_exist.txt");
        try {
            ApplicationProperties.getFileAsInputStream(props, "jaas.properties.file", null);
            fail("Expected " + AtlasException.class.getSimpleName() + " but none thrown");
        }
        catch (AtlasException e) {
            // good
        }
    }
}
