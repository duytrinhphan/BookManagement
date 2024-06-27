package fit.hutech.spring.daos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Sử dụng các annotation của Lombok để tự động tạo các phương thức getter, setter, constructor và toString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    // Thuộc tính ID của sách
    private Long bookId;

    // Thuộc tính tên sách
    private String bookName;

    // Thuộc tính giá của sách
    private Double price;

    // Thuộc tính số lượng của sách
    private int quantity;

    // Constructor với các tham số để khởi tạo một đối tượng Item
    public Item(Long bookId, int quantity, Double price, String bookName) {
        this.bookId = bookId;
        this.quantity = quantity;
        this.price = price;
        this.bookName = bookName;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Phương thức toString để trả về chuỗi biểu diễn của đối tượng Item
    @Override
    public String toString() {
        return "Item{" +
                "bookId=" + bookId +
                ", bookName='" + bookName + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
