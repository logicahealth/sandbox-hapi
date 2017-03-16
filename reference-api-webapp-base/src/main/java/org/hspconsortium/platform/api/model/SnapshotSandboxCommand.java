package org.hspconsortium.platform.api.model;

public class SnapshotSandboxCommand implements Command {

    public enum Action {
        Take, Restore, Delete
    }

    private Action action;

    public SnapshotSandboxCommand() {
    }

    public SnapshotSandboxCommand(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public SnapshotSandboxCommand setAction(Action action) {
        this.action = action;
        return this;
    }
}
