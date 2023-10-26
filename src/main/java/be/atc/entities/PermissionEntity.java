package be.atc.entities;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "permission", schema = "bookmyroom", catalog = "")
public class PermissionEntity {
    private int id;
    private String label;
    private Collection<RolePermissionsEntity> rolepermissionsById;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "label", nullable = false, length = 100)
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionEntity that = (PermissionEntity) o;
        return id == that.id && Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, label);
    }

    @OneToMany(mappedBy = "permissionByPermissionId")
    public Collection<RolePermissionsEntity> getRolepermissionsById() {
        return rolepermissionsById;
    }

    public void setRolepermissionsById(Collection<RolePermissionsEntity> rolepermissionsById) {
        this.rolepermissionsById = rolepermissionsById;
    }
}
