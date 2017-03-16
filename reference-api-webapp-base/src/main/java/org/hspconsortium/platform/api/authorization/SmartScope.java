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
