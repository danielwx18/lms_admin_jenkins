package com.gcit.lms.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.gcit.lms.model.BookCopies;
import com.gcit.lms.model.LibraryBranch;

@Component
public class BookCopiesDAO extends BaseDAO<BookCopies> implements ResultSetExtractor<List<BookCopies>> {

	public void createBookCopies(BookCopies bookCopies)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		template.update("insert into tbl_book_copies (bookId, branchId, noOfCopies) values(?,?,?)",
				new Object[] { bookCopies.getBook().getBookId(), bookCopies.getLibraryBranch().getBranchId(),
						bookCopies.getNoOfCopies() });
	}

	public void updateBookCopies(BookCopies bookCopies)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		template.update("update tbl_book_copies set noOfCopies = ? where branchId=? and bookId=?",
				new Object[] { bookCopies.getNoOfCopies(), bookCopies.getBranchId(), bookCopies.getBookId() });
	}

	public List<BookCopies> readAllBookCopies()
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		return template.query("select * from tbl_book_copies", this);
	}

	public List<BookCopies> readAllBookCopiesByBranch(LibraryBranch branch)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		return template.query(
				"select * from tbl_book_copies where branchId =?",
				new Object[] { branch.getBranchId() }, this);
	}

	@Override
	public List<BookCopies> extractData(ResultSet rs) throws SQLException {
		List<BookCopies> copies = new ArrayList<BookCopies>();
		while (rs.next()) {
			BookCopies copy = new BookCopies();
			copy.setNoOfCopies(rs.getInt("noOfCopies"));
			copy.setBookId(rs.getInt("bookId"));
			copy.setBranchId(rs.getInt("branchId"));
			copies.add(copy);
		}
		return copies;
	}

}
