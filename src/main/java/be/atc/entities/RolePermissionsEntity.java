package be.atc.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "role_permissions", schema = "bookmyroom", catalog = "")
public class RolePermissionsEntity {
    private int id;
    private RoleEntity roleByRoleId;
    private PermissionEntity permissionByPermissionId;


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolePermissionsEntity that = (RolePermissionsEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    public RoleEntity getRoleByRoleId() {
        return roleByRoleId;
    }

    public void setRoleByRoleId(RoleEntity roleByRoleId) {
        this.roleByRoleId = roleByRoleId;
    }

    @ManyToOne
    @JoinColumn(name = "permission_id", referencedColumnName = "id", nullable = false)
    public PermissionEntity getPermissionByPermissionId() {
        return permissionByPermissionId;
    }

    public void setPermissionByPermissionId(PermissionEntity permissionByPermissionId) {
        this.permissionByPermissionId = permissionByPermissionId;
    }
}
