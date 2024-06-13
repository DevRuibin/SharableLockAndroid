package com.example.shareablelock;

public enum EventType {
    CREATE_LOCK("create_lock"),
    ADD_USER("add_user"),
    REMOVE_USER("remove_user"),
    ADD_MANAGER("add_manager"),
    REMOVE_MANAGER("remove_manager"),
    DELETE_LOCK("delete_lock"),
    CHANGE_PICTURE("change_picture"),
    CHANGE_NAME("change_name"),
    CHANGE_POWER("change_power"),
    CHANGE_ONLINE("change_online"),
    CHANGE_LOCKED("change_locked"),
    CHANGE_LOCATION("change_location"),
    CHANGE_REPORT_BATTERY("change_report_battery"),
    CHANGE_REPORT_LOCATION("change_report_location");

    private final String value;

    EventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
