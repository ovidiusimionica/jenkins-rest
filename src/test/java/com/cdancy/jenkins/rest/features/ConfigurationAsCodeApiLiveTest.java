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

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import com.cdancy.jenkins.rest.parsers.ResponseResult;
import org.testng.annotations.Test;

import com.cdancy.jenkins.rest.BaseJenkinsApiLiveTest;

@Test(groups = "live", testName = "ConfigurationAsCodeApiLiveTest", singleThreaded = true)
public class ConfigurationAsCodeApiLiveTest extends BaseJenkinsApiLiveTest {

    @Test
    public void testCascCheck() {
        String config = payloadFromResource("/casc.yml");
        ResponseResult<Void> success = api().check(config);
        assertTrue(success.isSuccess());
    }

    @Test
    public void testCascApply() {
        String config = payloadFromResource("/casc.yml");
        ResponseResult<Void> success = api().apply(config);
        assertTrue(success.isSuccess());
    }

    @Test
    public void testBadCascCheck() {
        String config = payloadFromResource("/casc-bad.yml");
        ResponseResult<Void> success = api().apply(config);
        assertFalse(success.isSuccess());
    }

    @Test
    public void testBadCascApply() {
        String config = payloadFromResource("/casc-bad.yml");
        ResponseResult<Void> success = api().apply(config);
        assertFalse(success.isSuccess());
    }

    private ConfigurationAsCodeApi api() {
        return api.configurationAsCodeApi();
    }
}
