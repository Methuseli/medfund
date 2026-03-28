package com.medfund.user.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("role_permissions")
public class RolePermission {

    @Id
    private UUID id;

    @Column("role_id")
    private UUID roleId;

    private String permission;

    @Column("access_level")
    private String accessLevel;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getRoleId() { return roleId; }
    public void setRoleId(UUID roleId) { this.roleId = roleId; }

    public String getPermission() { return permission; }
    public void setPermission(String permission) { this.permission = permission; }

    public String getAccessLevel() { return accessLevel; }
    public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }
}
