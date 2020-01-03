/**
 *  * #%L
 *  *
 *  * %%
 *  * Copyright (C) 2014-2020 Healthcare Services Platform Consortium
 *  * %%
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  * #L%
 */

package org.hspconsortium.platform.api.authorization;

public class SmartScope {

    private String scope;

    public SmartScope(String scope) {
        this.scope = scope;
    }

    public boolean isPatientScope() {
        return "patient".equalsIgnoreCase(firstPartOrNull());
    }

    public boolean isUserScope(){
        return "user".equalsIgnoreCase(firstPartOrNull());
    }

    public String getResource(){
        if(!isPatientScope() || !isUserScope())
            return null;

        int forwardSlashIndex = this.scope.indexOf("/");
        int periodIndex = this.scope.indexOf(".");

        return this.scope.substring(forwardSlashIndex + 1, periodIndex);
    }

    public String getOperation(){
        if(!isPatientScope() || !isUserScope())
            return null;

        int periodIndex = this.scope.indexOf(".");

        return this.scope.substring(periodIndex + 1);
    }

    private String firstPartOrNull() {
        if (scope == null) {
            return null;
        }

        int forwardSlashIndex = this.scope.indexOf("/");

        if (forwardSlashIndex == -1) {
            return null;
        }

        return this.scope.substring(0, forwardSlashIndex);
    }
}
