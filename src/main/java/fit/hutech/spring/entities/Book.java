package fit.hutech.spring.entities;

import fit.hutech.spring.validators.annotations.ValidCategoryId;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
@Builder
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 50, nullable = false)
    @Size(min = 1, max = 50, message = "Tiêu đề phải có từ 1 đến 50 ký tự")
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @Column(name = "author", length = 50, nullable = false)
    @Size(min = 1,max = 50, message = "Tác giả phải có từ 1 đến 50 ký tự")
    @NotBlank(message = "Tác giả không được để trống")
    private String author;

    @Column(name = "price")
    @Positive(message = "\n" + "Giá phải lớn hơn 0")
    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    @ValidCategoryId
    @ToString.Exclude
    private Category category;

    @NotBlank(message = "Hình ảnh không được để trống")
    private String imageUrl;

    @Column(length = 2000)
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public @NotBlank(message = "Hình ảnh không được để trống") String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@NotBlank(message = "Hình ảnh không được để trống") String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Book(String author, Category category, String description, Long id, String imageUrl, List<ItemInvoice> itemInvoices, Double price, String title) {
        this.author = author;
        this.category = category;
        this.description = description;
        this.id = id;
        this.imageUrl = imageUrl;
        this.itemInvoices = itemInvoices;
        this.price = price;
        this.title = title;
    }

    public Book() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @Size(min = 1, max = 50, message = "Tiêu đề phải có từ 1 đến 50 ký tự") @NotBlank(message = "Tiêu đề không được để trống") String getTitle() {
        return title;
    }

    public void setTitle(@Size(min = 1, max = 50, message = "Tiêu đề phải có từ 1 đến 50 ký tự") @NotBlank(message = "Tiêu đề không được để trống") String title) {
        this.title = title;
    }

    public @Size(min = 1, max = 50, message = "Tác giả phải có từ 1 đến 50 ký tự") @NotBlank(message = "Tác giả không được để trống") String getAuthor() {
        return author;
    }

    public void setAuthor(@Size(min = 1, max = 50, message = "Tác giả phải có từ 1 đến 50 ký tự") @NotBlank(message = "Tác giả không được để trống") String author) {
        this.author = author;
    }

    public @Positive(message = "\n" + "Giá phải lớn hơn 0") Double getPrice() {
        return price;
    }

    public void setPrice(@Positive(message = "\n" + "Giá phải lớn hơn 0") Double price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ItemInvoice> itemInvoices = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) !=
                Hibernate.getClass(o)) return false;
        Book book = (Book) o;
        return getId() != null && Objects.equals(getId(),
                book.getId());
    }
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public List<ItemInvoice> getItemInvoices() {
        return itemInvoices;
    }

    public void setItemInvoices(List<ItemInvoice> itemInvoices) {
        this.itemInvoices = itemInvoices;
    }
}
