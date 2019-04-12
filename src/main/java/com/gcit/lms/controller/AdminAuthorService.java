package com.gcit.lms.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.gcit.lms.dao.AuthorDAO;
import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.model.Author;
import com.gcit.lms.model.Book;

@RestController
@RequestMapping("/admin")
public class AdminAuthorService {
	
	@Autowired
	AuthorDAO adao;
	
	@Autowired
	BookDAO bdao;


	@GetMapping(value="/authors",produces = {"application/json","application/xml"})
	public List<Author> readAllAuthors() throws SQLException {
		List<Author> authors = new ArrayList<>();
		try {
			authors = adao.readAllAuthors();
			for (Author a : authors) {
				a.setBooks(bdao.readBooksByAuthorId(a.getAuthorId()));
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return authors;
	}
	
	
	
	@Transactional
	@PutMapping(value="/author",consumes = {"application/json","application/xml"})
	public void updateAuthors(@RequestBody Author author) throws SQLException {
		try {
			adao.updateAuthor(author);
			Integer authorId = author.getAuthorId();
			if(author.getBooks()!=null) {
				adao.deleteAuthorBooks(authorId);
				for(Book b:author.getBooks()) {
					adao.createBookAuthors(b.getBookId(), authorId);
				}
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Transactional
	@PostMapping(value="/author",produces = {"application/json","application/xml"},consumes = {"application/json","application/xml"})
	public void addAuthors(@RequestBody Author author) throws SQLException {
		try {
			Integer authorId = adao.createAuthorToGetPK(author);
			if(author.getBooks()!=null) {
				for(Book b:author.getBooks()) {
					adao.createBookAuthors(b.getBookId(), authorId);
				}
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	@DeleteMapping(value="/author/{author_id}")
	public Integer deleteAuthors(@PathVariable("author_id") Integer authorId) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		if(authorId==null||adao.getAuthorByPK(authorId)==null) {
//			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request!");
			return null;
		}
		try {
			adao.deleteAuthor(authorId);
			return authorId;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
