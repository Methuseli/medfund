package com.medfund.rules.model;

/**
 * POJO representing the JSON rule definition stored in the database.
 * Deserialized from tenant-configured rules and used to dynamically
 * build Drools rules at runtime.
 */
public class RuleDefinition {

    private String id;
    private String name;
    private String description;
    private String category;
    private int priority;
    private boolean enabled = true;
    private String status;
    private int version;
    private ConditionGroup conditions;
    private RuleAction action;

    public RuleDefinition() {
    }

    // --- Getters and Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public ConditionGroup getConditions() {
        return conditions;
    }

    public void setConditions(ConditionGroup conditions) {
        this.conditions = conditions;
    }

    public RuleAction getAction() {
        return action;
    }

    public void setAction(RuleAction action) {
        this.action = action;
    }
}
