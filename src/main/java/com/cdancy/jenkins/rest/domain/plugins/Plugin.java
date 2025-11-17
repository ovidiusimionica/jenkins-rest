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

package com.cdancy.jenkins.rest.domain.plugins;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Plugin {

    private final Boolean active;
    private final String backupVersion;
    private final Boolean bundled;
    private final Boolean deleted;
    private final Boolean downgradable;
    private final Boolean enabled;
    private final Boolean hasUpdate;
    private final String longName;
    private final Boolean pinned;
    private final String requiredCoreVersion;
    private final String shortName;
    private final String supportsDynamicLoad;
    private final String url;
    private final String version;

    @JsonCreator
    public Plugin(
        @JsonProperty("active") Boolean active,
        @JsonProperty("backupVersion") String backupVersion,
        @JsonProperty("bundled") Boolean bundled,
        @JsonProperty("deleted") Boolean deleted,
        @JsonProperty("downgradable") Boolean downgradable,
        @JsonProperty("enabled") Boolean enabled,
        @JsonProperty("hasUpdate") Boolean hasUpdate,
        @JsonProperty("longName") String longName,
        @JsonProperty("pinned") Boolean pinned,
        @JsonProperty("requiredCoreVersion") String requiredCoreVersion,
        @JsonProperty("shortName") String shortName,
        @JsonProperty("supportsDynamicLoad") String supportsDynamicLoad,
        @JsonProperty("url") String url,
        @JsonProperty("version") String version
    ) {
        this.active = active;
        this.backupVersion = backupVersion;
        this.bundled = bundled;
        this.deleted = deleted;
        this.downgradable = downgradable;
        this.enabled = enabled;
        this.hasUpdate = hasUpdate;
        this.longName = longName;
        this.pinned = pinned;
        this.requiredCoreVersion = requiredCoreVersion;
        this.shortName = shortName;
        this.supportsDynamicLoad = supportsDynamicLoad;
        this.url = url;
        this.version = version;
    }

    public Boolean getActive() {
        return active;
    }

    public String getBackupVersion() {
        return backupVersion;
    }

    public Boolean getBundled() {
        return bundled;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public Boolean getDowngradable() {
        return downgradable;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Boolean getHasUpdate() {
        return hasUpdate;
    }

    public String getLongName() {
        return longName;
    }

    public Boolean getPinned() {
        return pinned;
    }

    public String getRequiredCoreVersion() {
        return requiredCoreVersion;
    }

    public String getShortName() {
        return shortName;
    }

    public String getSupportsDynamicLoad() {
        return supportsDynamicLoad;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }
}
