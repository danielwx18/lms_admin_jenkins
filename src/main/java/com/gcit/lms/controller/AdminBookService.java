package com.gcit.lms.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gcit.lms.dao.AuthorDAO;
import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.GenreDAO;
import com.gcit.lms.dao.PublisherDAO;
import com.gcit.lms.model.Author;
import com.gcit.lms.model.Book;
import com.gcit.lms.model.Genre;

@RestController
@RequestMapping("/admin")
public class AdminBookService {

	@Autowired
	AuthorDAO adao;

	@Autowired
	BookDAO bdao;

	@Autowired
	PublisherDAO pdao;

	@Autowired
	GenreDAO gdao;

	@Transactional
	@PostMapping(value="/book",consumes = {"application/json","application/xml"})
	public void addBook(@RequestBody Book book) throws SQLException {
		try {
			bdao.createBook(book);
			Integer bookId = book.getBookId();
			if (book.getAuthors() != null) {
				for (Author a : book.getAuthors()) {
					adao.createBookAuthors(bookId, a.getAuthorId());
				}
			}
			if (book.getGenre() != null) {
				for (Genre g : book.getGenre()) {
					gdao.createBookgenres(bookId, g.getGenre_id());
				}
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			System.out.println("Something went wrong, sorry!");
			e.printStackTrace();
		}
	}
		

	@Transactional
	@PutMapping(value="/book",consumes = {"application/json","application/xml"})
	public Book updateBook(@RequestBody Book book) throws SQLException {
		try {
			bdao.updateBook(book);
			Integer bookId = book.getBookId();
			if (book.getAuthors() != null) {
				adao.deleteBookAuthors(bookId);
				for (Author a : book.getAuthors()) {
					adao.createBookAuthors(bookId, a.getAuthorId());
				}
			}
			if (book.getGenre() != null) {
				gdao.deleteBookGenres(bookId);
				for (Genre g : book.getGenre()) {
					gdao.createBookgenres(bookId, g.getGenre_id());
				}
			}			
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			System.out.println("Something went wrong, sorry!");
			e.printStackTrace();
		}
		return book;
	}

	@GetMapping(value="/books",produces = {"application/json","application/xml"})
	public List<Book> readAllBooksByName() throws SQLException {
		List<Book> books = new ArrayList<>();
		try {			
			books=bdao.readAllBooks();
			for(Book b: books) {
				b.setAuthors(adao.readAllAuthorsByBook(b.getBookId()));
				b.setGenre(gdao.readAllgenresByBook(b.getBookId()));
				b.setPublisher(pdao.getPublisherByPK(b.getPubId()));
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			System.out.println("Something went wrong, sorry!");
			e.printStackTrace();
		}
		return books;
	}
	
	
	@GetMapping(value="/authors/{author-id}/books",produces = {"application/json","application/xml"})
	public List<Book> readAllBooksByAuthor(@PathVariable("author-id") Integer authorId) throws SQLException {
		List<Book> books = new ArrayList<>();
		try {
			books =bdao.readBooksByAuthorId(authorId);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			System.out.println("Something went wrong, sorry!");
			e.printStackTrace();
		}
		return books;
	}
	
	@GetMapping(value="/genres/{genre-id}/books",produces = {"application/json","application/xml"})
	public List<Book> readAllBooksByGenre(@PathVariable("genre-id") Integer genre_id) throws SQLException {
		List<Book> books = new ArrayList<>();
		try {
			books =bdao.readBooksByGenreId(genre_id);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			System.out.println("Something went wrong, sorry!");
			e.printStackTrace();
		}
		return books;
	}

	@Transactional
	@DeleteMapping(value="/book/{book-id}")
	public void deleteBooks(@PathVariable("book-id") Integer bookId) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		if(bookId==null||bdao.getBookByPK(bookId)==null) {
			throw new ConstraintViolationException("Bad Request", Collections.emptySet());
		}
		try {
			bdao.deleteBook(bookId);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			System.out.println("Something went wrong, sorry!");
			e.printStackTrace();
		}
	}
}
