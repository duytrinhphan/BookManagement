package fit.hutech.spring.controllers;

import fit.hutech.spring.daos.Item;
import fit.hutech.spring.entities.Book;
import fit.hutech.spring.services.BookService;
import fit.hutech.spring.services.CartService;
import fit.hutech.spring.services.CategoryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CartService cartService;

    private final Path fileStorageLocation = Paths.get("uploads");

    @Autowired
    public BookController(BookService bookService, CategoryService categoryService, CartService cartService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
        this.cartService = cartService;
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create upload directory!", ex);
        }
    }


    @GetMapping("/details/{id}")
    public String showBookDetails(@PathVariable("id") Long id, Model model) {
        Book book = bookService.findById(id);
        if (book == null) {
            model.addAttribute("error", "Book not found");
            return "redirect:/books";
        }
        model.addAttribute("book", book);
        return "book/details";
    }

    @GetMapping
    public String showAllBooks(
            @NotNull Model model,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy) {
        model.addAttribute("books", bookService.getAllBooks(pageNo,
                pageSize, sortBy));
        model.addAttribute("currentPage", pageNo);model.addAttribute("categories",
                categoryService.getAllCategories());
        model.addAttribute("totalPages",
                bookService.getAllBooks(pageNo, pageSize, sortBy).size() / pageSize);
        return "book/list";
    }
    @GetMapping("/add")
    public String addBookForm(@NotNull Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories",
                categoryService.getAllCategories());
        return "book/add";
    }
    @PostMapping("/add")
    public String addBook(@ModelAttribute Book book, @RequestParam("image") MultipartFile image, RedirectAttributes attributes) {
        try {
            if (!image.isEmpty()) {
                String fileName = image.getOriginalFilename();
                Path targetLocation = this.fileStorageLocation.resolve(fileName);
                Files.copy(image.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                book.setImageUrl("/uploads/" + fileName);
            }
            bookService.save(book);
            attributes.addFlashAttribute("success", "Book added successfully!");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Error adding book!");
        }
        return "redirect:/books";
    }
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable long id) {
        bookService.getBookById(id)
                .ifPresentOrElse(
                        book -> bookService.deleteBookById(id),
                        () -> { throw new IllegalArgumentException("Book not found"); });
        return "redirect:/books";
    }
    @GetMapping("/edit/{id}")
    public String editBookForm(@NotNull Model model, @PathVariable long id)
    {
        var book = bookService.getBookById(id);
        model.addAttribute("book", book.orElseThrow(() -> new
                IllegalArgumentException("Book not found")));
        model.addAttribute("categories",
                categoryService.getAllCategories());
        return "book/edit";
    }
    @PostMapping("/edit")
    public String editBook(@ModelAttribute Book book, @RequestParam("image") MultipartFile image, RedirectAttributes attributes) {
        try {
            if (!image.isEmpty()) {
                String fileName = image.getOriginalFilename();
                Path targetLocation = this.fileStorageLocation.resolve(fileName);
                Files.copy(image.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                book.setImageUrl("/uploads/" + fileName);
            }
            bookService.save(book);
            attributes.addFlashAttribute("success", "Book updated successfully!");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Error updating book!");
        }
        return "redirect:/books";
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<String> addToCart(HttpSession session,
                                            @RequestParam long id,
                                            @RequestParam String name,
                                            @RequestParam double price,
                                            @RequestParam(defaultValue = "1") int quantity)
    {
        try {
            var cart = cartService.getCart(session);
            cart.addItems(new Item(id, quantity, price,name ));
            cartService.updateCart(session, cart);
            return ResponseEntity.ok("Book added to cart successfully");
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding book to cart");
        }

    }

    @GetMapping("/search")
    public String searchBook(
            @NotNull Model model,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy) {
        model.addAttribute("books", bookService.searchBook(keyword));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages",
                bookService
                        .getAllBooks(pageNo, pageSize, sortBy)
                        .size() / pageSize);
        model.addAttribute("categories",
                categoryService.getAllCategories());
        return "book/list";
    }

    @GetMapping("/autocomplete")
    @ResponseBody
    public List<String> autocomplete(@RequestParam("term") String term) {
        return bookService.searchBookTitles(term);
    }

    @GetMapping("/uploads/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path file = fileStorageLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
