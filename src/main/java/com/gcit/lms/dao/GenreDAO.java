package com.gcit.lms.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.gcit.lms.model.Genre;

@Component
public class GenreDAO extends BaseDAO<Genre> implements ResultSetExtractor<List<Genre>> {
	
	public Integer createGenreToGetPK(Genre genre)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		template.update(connection -> {
			PreparedStatement ps = connection.prepareStatement("insert into tbl_genre (genre_name) values(?) ", Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, genre.getGenre_name()); 	
			return ps;
		}, keyHolder);
		return keyHolder.getKey().intValue();

	}
	
	public Genre readAllgenresByPK(Integer genre_id)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		List<Genre> list = template.query("select * from tbl_genre where genre_id=?", new Object[] {genre_id},this);
		if(list!=null&&!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public void createGenre(Genre genre)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		template.update("insert into tbl_genre (genre_name) values(?)", new Object[] { genre.getGenre_name() });
	}

	public void createBookgenres(Integer bookId, Integer genre_id)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		template.update("insert into tbl_book_genres values(?, ?)", new Object[] { genre_id, bookId});
	}

	public void updateGenre(Genre genre)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		template.update("update tbl_genre set genre_name = ? where genre_id= ?",
				new Object[] { genre.getGenre_name(), genre.getGenre_id() });
	}

	public void deleteGenre(Integer genre_id)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		template.update("delete from tbl_genre where genre_id= ?", new Object[] { genre_id });
	}
	
	public void deleteBookGenres(Integer bookId)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		template.update("delete from tbl_book_genres where bookId= ?", new Object[] { bookId });
	}
	
	public void deleteGenreBooks(Integer genre_id)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		template.update("delete from tbl_book_genres where genre_id= ?", new Object[] { genre_id });
	}

	public List<Genre> readAllgenres()
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		return template.query("select * from tbl_genre", this);
	}

	public List<Genre> readAllgenresByName(String genreName)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		return template.query("select * from tbl_genre where genre_name = ?", new Object[] { genreName }, this);
	}

	public List<Genre> readAllgenresByBook(Integer bookId)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		return template.query("select * from tbl_genre where genre_id in \r\n"
				+ "(select bg.genre_id from tbl_book_genres bg join tbl_book b on bg.bookId=b.bookId where b.bookId = ?)",
				new Object[] { bookId }, this);
	}

	@Override
	public List<Genre> extractData(ResultSet rs) throws SQLException {
		List<Genre> genres = new ArrayList<Genre>();
		while (rs.next()) {
			Genre genre = new Genre();
			genre.setGenre_name(rs.getString("genre_name"));
			genre.setGenre_id(rs.getInt("genre_id"));
			genres.add(genre);
		}
		return genres;
	}

}
