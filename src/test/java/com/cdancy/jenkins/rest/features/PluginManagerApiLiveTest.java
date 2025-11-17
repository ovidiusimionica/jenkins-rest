/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cdancy.jenkins.rest.features;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import com.cdancy.jenkins.rest.BaseJenkinsApiLiveTest;
import com.cdancy.jenkins.rest.domain.plugins.Plugins;
import com.cdancy.jenkins.rest.parsers.ResponseResult;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionFactory;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "PluginManagerApiLiveTest", singleThreaded = true)
public class PluginManagerApiLiveTest extends BaseJenkinsApiLiveTest
{

    @Test
    public void testGetPlugins()
    {
        ResponseResult<Plugins> pluginsResponse = api().plugins(3, null);
        assertTrue(pluginsResponse.isSuccess());
        Plugins plugins = pluginsResponse.getEntity();
        assertTrue(plugins.errors().isEmpty());
        assertFalse(plugins.getPlugins().isEmpty());
        assertNotNull(plugins.getPlugins().getFirst().getShortName());
    }

    @Test
    public void testInstallNecessaryPlugins()
    {
        ResponseResult<Void> status = api().installNecessaryPluginsById("workflow-scm-step@427.v4ca_6512e7df1");
        assertNotNull(status);
        assertTrue(status.isSuccess());

        assertPluginInstalled("workflow-scm-step", 1, 0);

    }

    private void assertPluginInstalled(String pluginName, int expected, int delaySeconds) {
        Awaitility.await().pollDelay(delaySeconds, SECONDS).atMost(5, SECONDS).untilAsserted(() -> {
            ResponseResult<Plugins> pluginsResponse = api().plugins(3, null);
            Plugins plugins = pluginsResponse.getEntity();
            assertEquals(plugins.getPlugins().stream().filter(plugin ->
                plugin.getShortName().equals(pluginName)).count(), expected);
        });
    }

    @Test
    public void testInstallUnknownPlugins() {
        ResponseResult<Void> status = api().installNecessaryPluginsById("dummy_unknown_plugin@0.0.1");
        assertNotNull(status);
        assertTrue(status.isSuccess());

        assertPluginInstalled("dummy_unknown_plugin", 0, 3);
    }

    private PluginManagerApi api()
    {
        return api.pluginManagerApi();
    }
}
