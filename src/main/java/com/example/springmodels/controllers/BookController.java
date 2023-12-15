package com.example.springmodels.controllers;

import com.example.springmodels.models.BookDetailModel;
import com.example.springmodels.models.BookModel;
import com.example.springmodels.repositories.BookCategoryRepository;
import com.example.springmodels.repositories.BookDetailRepository;
import com.example.springmodels.repositories.BookRepository;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/books")
@PreAuthorize("hasAnyAuthority('MODER', 'USER')")
public class BookController {

    private final BookRepository bookRepository;
    private final BookCategoryRepository categoryRepository;
    private final BookDetailRepository bookDetailRepository;

    public BookController(BookRepository bookRepository, BookCategoryRepository categoryRepository, BookDetailRepository bookDetailRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.bookDetailRepository = bookDetailRepository;
    }

    @GetMapping()
    public String getAll(Model model) {
        List<BookModel> books = bookRepository.findAll();
        model.addAttribute("books", books);
        return "books/index";
    }

    @GetMapping("/new")
    public String newBookForm(Model model) {
        model.addAttribute("book", new BookModel());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("bookDetails", bookDetailRepository.findAll());
        return "books/new";
    }

    @GetMapping("/{id}")
    public String showBook(@PathVariable Long id, Model model) {
        Optional<BookModel> book = bookRepository.findById(id);
        model.addAttribute("book", book.orElse(null));
        return "books/show";
    }

    @PostMapping
    public String createBook(@Valid @ModelAttribute("book") BookModel book,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "books/new";
        }
        String resultSave = saveDataBook(book);
        if (resultSave.equals("Успешно")) return "redirect:/books";
        else {
            model.addAttribute("errorMessage", resultSave);
            return "books/index";
        }
    }
    @PostMapping("/update/{id}")
    public String updateBook(@PathVariable Long id, @Valid @ModelAttribute("book") BookModel book, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "books/edit";
        }
        String resultSave = saveDataBook(book);
        if (resultSave.equals("Успешно")) return "redirect:/books";
            else {
                model.addAttribute("errorMessage", resultSave);
                return "books/index";
            }
    }

    private String saveDataBook(BookModel book){
        BookDetailModel bookDetail = book.getBookDetail();
        if(bookDetail!=null){
            bookDetail.setBook(book);
            bookRepository.save(book);
            return "Успешно";
        }else{
            return "Не удалось сохранить книгу";
        }
    }

    @GetMapping("/{id}/edit")
    public String editBookForm(@PathVariable Long id, Model model) {
        Optional<BookModel> book = bookRepository.findById(id);
        model.addAttribute("book", book.orElse(null));
        model.addAttribute("categories", categoryRepository.findAll());
        return "books/edit";
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
        return "redirect:/books";
    }

    @GetMapping("/search")
    public String searchByAuthor(@RequestParam("author") String author, Model model) {
        List<BookModel> booksByAuthor = bookRepository.findByAuthor(author);

        model.addAttribute("books", booksByAuthor);
        return "books/search_results";
    }
}
