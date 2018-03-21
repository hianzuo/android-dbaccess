package com.hianzuo.dbaccess.model;

/**
 * Created by Ryan
 * On 2017/4/6.
 */

public class ExecUpgradeSQLConfig {
    private int fromVersion;
    private int state;

    public ExecUpgradeSQLConfig(int fromVersion, int state) {
        this.fromVersion = fromVersion;
        this.state = state;
    }

    public int getFromVersion() {
        return fromVersion;
    }

    public boolean isUpgraded() {
        return ExecUpgradeSQLConfigState.STATE_DONE == state;
    }

    @Override
    public String toString() {
        return toString(fromVersion, state);
    }

    public static String toString(int fromVersion, int state) {
        return fromVersion + "|" + state;
    }

    public static ExecUpgradeSQLConfig fromString(String value) {
        try {
            String[] split = value.split("\\|");
            int oldVersion = Integer.valueOf(split[0]);
            int state = Integer.valueOf(split[1]);
            return new ExecUpgradeSQLConfig(oldVersion, state);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isWaitUpdate() {
        return ExecUpgradeSQLConfigState.STATE_WAITING == state;
    }
}
