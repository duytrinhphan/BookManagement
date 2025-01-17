package fit.hutech.spring.services;

import fit.hutech.spring.daos.Cart;
import fit.hutech.spring.daos.Item;
import fit.hutech.spring.entities.Book;
import fit.hutech.spring.entities.Invoice;
import fit.hutech.spring.entities.ItemInvoice;
import fit.hutech.spring.repositories.IBookRepository;
import fit.hutech.spring.repositories.IInvoiceRepository;
import fit.hutech.spring.repositories.IItemInvoiceRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
public class CartService {
    private static final String CART_SESSION_KEY = "cart";

    private final IInvoiceRepository invoiceRepository;
    private final IItemInvoiceRepository itemInvoiceRepository;
    private final IBookRepository bookRepository;

    public Cart getCart(HttpSession session) {
        return Optional.ofNullable((Cart) session.getAttribute(CART_SESSION_KEY))
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    session.setAttribute(CART_SESSION_KEY, cart);
                    return cart;
                });
    }

    public void updateCart(HttpSession session, Cart cart) {
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void removeCart(HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }

    public int getSumQuantity(HttpSession session) {
        return getCart(session).getCartItems().stream()
                .mapToInt(Item::getQuantity)
                .sum();
    }

    public double getSumPrice(HttpSession session) {
        return getCart(session).getCartItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    public void saveCart(HttpSession session) {
        Cart cart = getCart(session);
        if (cart.getCartItems().isEmpty()) return;
        Invoice invoice = new Invoice();
        invoice.setInvoiceDate(new Date());
        invoice.setPrice(getSumPrice(session));
        invoiceRepository.save(invoice);
        cart.getCartItems().forEach(item -> {
            ItemInvoice items = new ItemInvoice();
            items.setInvoice(invoice);
            items.setQuantity(item.getQuantity());
            items.setBook(bookRepository.findById(item.getBookId()).orElseThrow());
            itemInvoiceRepository.save(items);
        });
        removeCart(session);
    }

//    public void addToCart(Book book) {
//        Cart cart = new Cart();
//        cart.setBook(book);
//        cartRepository.save(cart);
//    }
}
