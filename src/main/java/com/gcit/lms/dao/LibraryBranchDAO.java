package com.gcit.lms.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.gcit.lms.model.Borrower;
import com.gcit.lms.model.LibraryBranch;

@Component
public class LibraryBranchDAO extends BaseDAO<LibraryBranch> implements ResultSetExtractor<List<LibraryBranch>> {

	public LibraryBranch getBranchByPK(Integer branchId)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		List<LibraryBranch> branches = template.query("select * from tbl_library_branch where branchId = ?",
				new Object[] { branchId }, this);
		if (branches != null && branches.size() > 0) {
			return branches.get(0);
		}
		return null;
	}

	public List<LibraryBranch> getReturnBookBranchFromBorrower(Borrower borrower)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return template.query(
				"SELECT distinct(branchId), branchName, branchAddress"
						+ " FROM tbl_book_loans Inner Join tbl_library_branch Using(branchId)"
						+ "Where returnDate is Null and cardNo = ? Order By branchId",
				new Object[] { borrower.getCardNo() }, this);
	}

	public void createBranch(LibraryBranch branch)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		template.update("Insert Into tbl_library_branch (branchName, branchAddress) value (?,?)",
				new Object[] { branch.getBranchName(), branch.getBranchAddress() });
	}

	public void updateBranch(LibraryBranch branch)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		template.update("Update tbl_library_branch set branchName = ?, branchAddress = ? where branchId = ?",
				new Object[] { branch.getBranchName(), branch.getBranchAddress(), branch.getBranchId() });
	}

	public void deleteBranch(Integer branchId)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		template.update("delete from tbl_library_branch where branchId= ?", new Object[] { branchId });
	}

	public List<LibraryBranch> readAllBranches()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return template.query("select * from tbl_library_branch", this);
	}

	public List<LibraryBranch> readBranchsByName(String branchName)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return template.query("select * from tbl_library_branch where branchName = ?", new Object[] { branchName },
				this);
	}

	@Override
	public List<LibraryBranch> extractData(ResultSet rs) throws SQLException {
		List<LibraryBranch> branchs = new ArrayList<LibraryBranch>();
		while (rs.next()) {
			LibraryBranch branch = new LibraryBranch();
			branch.setBranchId(rs.getInt("branchId"));
			branch.setBranchName(rs.getString("branchName"));
			branch.setBranchAddress(rs.getString("branchAddress"));
			branchs.add(branch);
		}
		return branchs;
	}

}
