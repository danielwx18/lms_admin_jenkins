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

import com.gcit.lms.model.Author;


@Component
public class AuthorDAO extends BaseDAO<Author> implements ResultSetExtractor<List<Author>> {
	
	public Integer createAuthorToGetPK(Author author)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		template.update(connection -> {
			PreparedStatement ps = connection.prepareStatement("insert into tbl_author (authorName) values(?) ", Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, author.getAuthorName()); 	
			return ps;
		}, keyHolder);
		return keyHolder.getKey().intValue();

	}
	
	public Author getAuthorByPK(Integer authorId)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		List<Author> authors = template.query("select * from tbl_author where authorId = ?", new Object[] { authorId }, this);
		if (authors != null && authors.size() > 0) {
			return authors.get(0);
		}
		return null;
	}

	public void createAuthor(Author author)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		template.update("insert into tbl_author (authorName) values(?)", new Object[] { author.getAuthorName() });
	}

	public void createBookAuthors(Integer bookId, Integer authorId)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		template.update("insert into tbl_book_authors values(?, ?)", new Object[] { bookId, authorId });
	}

	public void updateAuthor(Author author)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		template.update("update tbl_author set authorName = ? where authorId= ?",
				new Object[] { author.getAuthorName(), author.getAuthorId() });
	}

	public void deleteAuthor(Integer authorId)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		template.update("delete from tbl_author where authorId= ?", new Object[] { authorId });
	}

	public void deleteBookAuthors(Integer bookId)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		template.update("delete from tbl_book_authors where bookId= ?", new Object[] { bookId });
	}
	
	public void deleteAuthorBooks(Integer authorId)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		template.update("delete from tbl_book_authors where authorId= ?", new Object[] { authorId });
	}
	
	public List<Author> readAllAuthors()
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		return template.query("select * from tbl_author", this);
	}

	public List<Author> readAuthorsByName(String authorName)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		return template.query("select * from tbl_author where authorName = ?", new Object[] { authorName }, this);
	}

	public List<Author> readAllAuthorsByBook(Integer bookId)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		return template.query("select * from tbl_author where authorId in \n"
				+ "(select ba.authorId from tbl_book_authors ba join tbl_book b on ba.bookId=b.bookId where b.bookId = ?)",
				new Object[] { bookId }, this);
	}

	@Override
	public List<Author> extractData(ResultSet rs) throws SQLException {
		List<Author> authors = new ArrayList<Author>();
		while (rs.next()) {
			Author author = new Author();
			author.setAuthorName(rs.getString("authorName"));
			author.setAuthorId(rs.getInt("authorId"));
			authors.add(author);
		}
		return authors;
	}

}