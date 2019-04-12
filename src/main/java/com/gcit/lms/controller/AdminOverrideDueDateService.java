package com.gcit.lms.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.BookLoansDAO;
import com.gcit.lms.dao.BorrowerDAO;
import com.gcit.lms.dao.LibraryBranchDAO;
import com.gcit.lms.model.BookLoans;
import com.gcit.lms.model.Borrower;

@RestController
@RequestMapping("/admin")
public class AdminOverrideDueDateService {

	@Autowired
	BookLoansDAO bldao;
	
	@Autowired
	BorrowerDAO brdao;
	
	@Autowired
	LibraryBranchDAO ldao;
	
	@Autowired
	BookDAO bdao;
	
	@GetMapping(value="/loans",produces = {"application/json","application/xml"})
	public List<BookLoans> readAllLoansWithDueDateByBorrower(@RequestParam String name) throws SQLException {
		List<BookLoans> loans = new ArrayList<>();
		List<Borrower> borrowers = new ArrayList<>();
		try {
			if(name!=null&&!name.isEmpty()) {
				borrowers=brdao.readBorrowersByName(name);
				for(Borrower br:borrowers) {
					loans.addAll(bldao.readAllLoansByBorrower(br.getCardNo()));
				}
			} else {
				loans = bldao.readAllLoans();
			}
			for(BookLoans bl:loans) {
				bl.setBook(bdao.getBookByPK(bl.getBookId()));
				bl.setBorrower(brdao.getBorrowerByPK(bl.getCardNo()));
				bl.setLibraryBranch(ldao.getBranchByPK(bl.getBranchId()));
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return loans;
	}

	
	@Transactional
	@PutMapping(value="/loan",produces = {"application/json","application/xml"},consumes = {"application/json","application/xml"})
	public void overrideDueDate(@RequestBody BookLoans bl) throws SQLException {
		try {
//			ZoneId zoneId = ZoneId.of("America/New_York");
//		    LocalDate out = LocalDate.now(zoneId);
//			LocalDate due = out.plusWeeks(1);
//			bl.setDueDate(due);
			bldao.updateLoan(bl);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
}
