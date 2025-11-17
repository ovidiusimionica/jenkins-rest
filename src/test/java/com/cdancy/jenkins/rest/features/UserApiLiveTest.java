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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import com.cdancy.jenkins.rest.BaseJenkinsApiLiveTest;
import com.cdancy.jenkins.rest.domain.user.ApiToken;
import com.cdancy.jenkins.rest.domain.user.ApiTokenData;
import com.cdancy.jenkins.rest.domain.user.User;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "UserApiLiveTest", singleThreaded = true)
public class UserApiLiveTest extends BaseJenkinsApiLiveTest
{

    ApiToken token;

    @Test
    public void testGetUser()
    {
        User user = api().get().getEntity();
        assertNotNull(user);
        assertNotNull(user.getAbsoluteUrl());
        assertEquals(user.getAbsoluteUrl(), ENDPOINT + "/user/admin");
        assertTrue(user.getDescription() == null || user.getDescription().isEmpty());
        assertNotNull(user.getFullName());
        assertEquals(user.getFullName(), "admin");
        assertNotNull(user.getId());
        assertEquals(user.getId(), "admin");
    }

    @Test
    public void testGenerateNewToken()
    {
        var tokenResponse = api().generateNewToken("user-api-test-token");
        token = tokenResponse.getEntity();
        assertNotNull(token);
        assertEquals(token.getStatus(), "ok");
        ApiTokenData tokenData = token.getData();
        assertNotNull(tokenData);
        assertNotNull(tokenData.getTokenName());
        assertEquals(tokenData.getTokenName(), "user-api-test-token");
        assertNotNull(tokenData.getTokenUuid());
        assertNotNull(tokenData.getTokenValue());
    }

    @Test(dependsOnMethods = "testGenerateNewToken")
    public void testRevokeApiToken()
    {
        var status = api().revoke(token.getData().getTokenUuid());
        // Jenkins returns 200 whether the tokenUuid is correct or not.
        assertTrue(status.isSuccess());
    }

    @Test
    public void testRevokeApiTokenWithEmptyUuid()
    {
        var status = api().revoke("");
        assertFalse(status.isSuccess());
    }

    private UserApi api()
    {
        return api.userApi();
    }
}
