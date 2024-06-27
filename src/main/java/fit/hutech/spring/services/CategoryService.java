package fit.hutech.spring.services;

import fit.hutech.spring.entities.Book;
import fit.hutech.spring.entities.Category;
import fit.hutech.spring.repositories.IBookRepository;
import fit.hutech.spring.repositories.ICategoryRepository;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE,
        rollbackFor = {Exception.class, Throwable.class})
public class CategoryService {
    private final ICategoryRepository categoryRepository;
    private final IBookRepository bookRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public void addCategory(Category category) {
        categoryRepository.save(category);
    }

    public void updateCategory(@NotNull Category category) {
        Category existingCategory = categoryRepository.findById(category.getId())
                .orElse(null);
        if (existingCategory != null) {
            existingCategory.setName(category.getName());
            categoryRepository.save(existingCategory);
        }
    }

    public void deleteById(Long id) {
        Category defaultCategory = categoryRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Default category not found"));

        List<Book> books = bookRepository.findByCategoryId(id);
        for (Book book : books) {
            book.setCategory(defaultCategory);
            bookRepository.save(book);
        }

        categoryRepository.deleteById(id);
    }
    public Category save(Category category) {
        return categoryRepository.save(category);
    }
}
