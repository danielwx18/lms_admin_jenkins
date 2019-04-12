package com.gcit.lms.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.gcit.lms.model.BookLoans;
import com.gcit.lms.model.Borrower;
import com.gcit.lms.model.LibraryBranch;

@Component
public class BookLoansDAO extends BaseDAO<BookLoans> implements ResultSetExtractor<List<BookLoans>> {

	public void createLoan(BookLoans loan)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		template.update("Insert Into tbl_book_loans (bookId, branchId, cardNo, dateOut, dueDate) value (?,?,?,?,?)",
				new Object[] { loan.getBook().getBookId(), loan.getLibraryBranch().getBranchId(),
						loan.getBorrower().getCardNo(), loan.getDateOut(), loan.getDueDate() });
	}

	public void updateLoan(BookLoans loan)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		template.update(
				"Update tbl_book_loans set dateOut = ?, dueDate = ?, returnDate = ? "
						+ "where cardNo = ? and bookId = ? and branchId = ? ",
				new Object[] { loan.getDateOut(), loan.getDueDate(), loan.getReturnDate(),
						loan.getBorrower().getCardNo(), loan.getBook().getBookId(),
						loan.getLibraryBranch().getBranchId() });
	}

	
	public List<BookLoans> getBorrowedBookFromBorrower(Borrower borrower, LibraryBranch branch)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return template.query(
				"SELECT * \n" + "FROM tbl_book_loans \n" + "Inner Join tbl_book Using(bookId)\n"
						+ "Inner Join tbl_library_branch Using(branchId)\n"
						+ "Where returnDate is Null and cardNo = ? and branchId = ?\n" + "Order by dueDate",
				new Object[] { borrower.getCardNo(), branch.getBranchId() }, this);
	}
	

	public List<BookLoans> getBorrowedListFromDueDate(LibraryBranch branch)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return template.query(
				"SELECT * \n" + "FROM tbl_book_loans \n" + "Inner Join tbl_book Using(bookId)\n"
						+ "Inner Join tbl_library_branch Using(branchId)\n"
						+ "Where returnDate is Null and branchId = ?\n" + "Order by dueDate",
				new Object[] { branch.getBranchId() }, this);
	}

	public void deleteLoan(BookLoans loan)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		template.update("delete from tbl_book_loans where where cardNo = ? and bookId = ? and branchId = ?",
				new Object[] { loan.getBorrower().getCardNo(), loan.getBook().getBookId(),
						loan.getLibraryBranch().getBranchId() });
	}

	public List<BookLoans> readAllLoans()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return template.query("select * from tbl_book_loans where returnDate is null", this);
	}
	
	public List<BookLoans> readAllLoansByBorrower(Integer cardNo)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return template.query("select * from tbl_book_loans where cardNo=? and returnDate is null", new Object[] {cardNo} ,this);
	}

	public List<BookLoans> readAllLoansWithBorrowerAndBranch()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return template
				.query("select title, branchName, name borrower, br.cardNo cardNo, dateOut, dueDate, returnDate\n"
						+ "from tbl_book_loans bl join tbl_library_branch l on bl.branchId=l.branchId \n"
						+ "join tbl_book b on b.bookId=bl.bookId \n" + "join tbl_borrower br on br.cardNo=bl.cardNo\n"
						+ "order by b.bookId", this);
	}

	public List<BookLoans> readLoansByBorrower(Integer cardNo)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return template.query(
				"select * from tbl_book_loans where cardNo = ?",
				new Object[] { cardNo }, this);
	}

	@Override
	public List<BookLoans> extractData(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		List<BookLoans> loans = new ArrayList<BookLoans>();
		while (rs.next()) {
			BookLoans loan = new BookLoans();
			loan.setBookId(rs.getInt("bookId"));
			loan.setBranchId(rs.getInt("branchId"));
			loan.setCardNo(rs.getInt("cardNo"));
			loan.setDateOut(rs.getDate("dateOut"));
			loan.setDueDate(rs.getDate("dueDate"));
			loan.setReturnDate(rs.getDate("returnDate"));
			loans.add(loan);
		}
		return loans;
	}

}