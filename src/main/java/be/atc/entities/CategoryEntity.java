package be.atc.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

@NamedQueries( value = {
        @NamedQuery(name = "Category.findOneByName", query = "SELECT c FROM CategoryEntity c WHERE LOWER(c.name) = LOWER(:name)"),
        @NamedQuery(name = "Category.findAll", query = "SELECT c FROM CategoryEntity c")
})

@Entity
@Table(name = "category", schema = "bookmyroom", catalog = "")
public class CategoryEntity implements Serializable {
    private int id;
    private String name;
    private Collection<HallCategoryEntity> hallcategoriesById;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "Name", nullable = false, length = 100)
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
        CategoryEntity that = (CategoryEntity) o;
        return id == that.id && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @OneToMany(mappedBy = "categoryByCategoryId")
    public Collection<HallCategoryEntity> getHallcategoriesById() {
        return hallcategoriesById;
    }

    public void setHallcategoriesById(Collection<HallCategoryEntity> hallcategoriesById) {
        this.hallcategoriesById = hallcategoriesById;
    }
}
