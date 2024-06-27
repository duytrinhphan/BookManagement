package fit.hutech.spring.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "invoice_date")
    private Date invoiceDate = new Date();
    @Column(name = "total")
    @Positive(message = "Total must be positive")
    private Double price;
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ItemInvoice> itemInvoices = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ToString.Exclude
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) !=
                Hibernate.getClass(o)) return false;
        Invoice invoice = (Invoice) o;
        return getId() != null && Objects.equals(getId(),
                invoice.getId());
    }
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    // Getter và Setter cho itemInvoices
    public List<ItemInvoice> getItemInvoices() {
        return itemInvoices;
    }

    public void setItemInvoices(List<ItemInvoice> itemInvoices) {
        this.itemInvoices = itemInvoices;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public @Positive(message = "Total must be positive") Double getPrice() {
        return price;
    }

    public void setPrice(@Positive(message = "Total must be positive") Double price) {
        this.price = price;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
