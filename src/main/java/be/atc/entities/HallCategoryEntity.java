package be.atc.entities;

import javax.persistence.*;
import java.util.Objects;

@NamedQueries({
        @NamedQuery(name = "HallCategory.findByHallAndCategory",
                query = "SELECT hc FROM HallCategoryEntity hc WHERE hc.hallByHallId = :hall AND hc.categoryByCategoryId = :category"),
        @NamedQuery(name = "HallCategory.findByHall",
                query = "SELECT hc FROM HallCategoryEntity hc WHERE hc.hallByHallId = :hall"),
        @NamedQuery(name = "HallCategory.countByCategory",
                query = "SELECT COUNT (hc) FROM HallCategoryEntity hc WHERE hc.categoryByCategoryId = :category")
})

@Entity
@Table(name = "hall_category", schema = "bookmyroom", catalog = "")
public class HallCategoryEntity {
    private int id;
    private HallEntity hallByHallId;
    private CategoryEntity categoryByCategoryId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
        HallCategoryEntity that = (HallCategoryEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @ManyToOne
    @JoinColumn(name = "hall_id", referencedColumnName = "id", nullable = false)
    public HallEntity getHallByHallId() {
        return hallByHallId;
    }

    public void setHallByHallId(HallEntity hallByHallId) {
        this.hallByHallId = hallByHallId;
    }

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    public CategoryEntity getCategoryByCategoryId() {
        return categoryByCategoryId;
    }

    public void setCategoryByCategoryId(CategoryEntity categoryByCategoryId) {
        this.categoryByCategoryId = categoryByCategoryId;
    }
}
