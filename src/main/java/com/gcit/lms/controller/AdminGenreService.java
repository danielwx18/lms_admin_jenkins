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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.GenreDAO;
import com.gcit.lms.model.Book;
import com.gcit.lms.model.Genre;

@RestController
@RequestMapping("/admin")
public class AdminGenreService {
	
	@Autowired
	GenreDAO gdao;
	
	@Autowired
	BookDAO bdao;
	

	@GetMapping(value="/genres",produces = {"application/json","application/xml"})
	public List<Genre> readAllGenres(@RequestParam String genreName) throws SQLException {
		List<Genre> genres = new ArrayList<>();
		try {
			if(genreName!=null&&!genreName.isEmpty()) {
				genres=gdao.readAllgenresByName(genreName);
			} else {
				genres = gdao.readAllgenres();
			}			
			for (Genre g : genres) {
				g.setBooks(bdao.readBooksByGenreId(g.getGenre_id()));
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		return genres;
	}
	
	@Transactional
	@PutMapping(value="/genre",consumes = {"application/json","application/xml"})
	public Genre updateGenres(@RequestBody Genre genre) throws SQLException {
		try {
			gdao.updateGenre(genre);
			Integer genre_id = genre.getGenre_id();
			if(genre.getBooks()!=null) {
				gdao.deleteGenreBooks(genre_id);
				for(Book b:genre.getBooks()) {
					gdao.createBookgenres(b.getBookId(), genre_id);
				}
			}
			
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return genre;
	}
	
	@Transactional
	@PostMapping(value="/genre",produces = {"application/json","application/xml"},consumes = {"application/json","application/xml"})
	public Genre addGenres(@RequestBody Genre genre) throws SQLException {
		try {
			Integer genre_id = gdao.createGenreToGetPK(genre);
			if(genre.getBooks()!=null) {
				for(Book b:genre.getBooks()) {
					gdao.createBookgenres(b.getBookId(), genre_id);
				}
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return genre;
	}
	
	@Transactional
	@DeleteMapping(value="/genre/{genre-id}",consumes = {"application/json","application/xml"})
	public void deleteGenres(@PathVariable("genre-id") Integer genre_id) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		if(genre_id==null||gdao.readAllgenresByPK(genre_id)==null) {
			throw new ConstraintViolationException("error", Collections.emptySet());
		}
		try {
			gdao.deleteGenre(genre_id);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
}
