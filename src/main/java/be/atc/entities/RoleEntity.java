package be.atc.entities;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@NamedQueries({
        @NamedQuery(name = "Role.findRoleByName", query = "SELECT r FROM RoleEntity r WHERE LOWER(r.name) = LOWER(:name)"),
        @NamedQuery(name = "Role.findAllRoles", query = "SELECT r FROM RoleEntity r")
})
@Entity
@Table(name = "role", schema = "bookmyroom", catalog = "")
public class RoleEntity {
    private int id;
    private String name;
    private Collection<RolePermissionsEntity> rolepermissionsById;
    private Collection<UserEntity> usersById;

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
    @Column(name = "name", nullable = false, length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleEntity that = (RoleEntity) o;
        return id == that.id && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @OneToMany(mappedBy = "roleByRoleId")
    public Collection<RolePermissionsEntity> getRolepermissionsById() {
        return rolepermissionsById;
    }

    public void setRolepermissionsById(Collection<RolePermissionsEntity> rolepermissionsById) {
        this.rolepermissionsById = rolepermissionsById;
    }

    @OneToMany(mappedBy = "roleByRoleId")
    public Collection<UserEntity> getUsersById() {
        return usersById;
    }

    public void setUsersById(Collection<UserEntity> usersById) {
        this.usersById = usersById;
    }
}
