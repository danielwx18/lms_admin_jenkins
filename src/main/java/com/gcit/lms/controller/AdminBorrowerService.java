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
import com.gcit.lms.dao.BookLoansDAO;
import com.gcit.lms.dao.BorrowerDAO;
import com.gcit.lms.model.Borrower;

@RestController
@RequestMapping("/admin")
public class AdminBorrowerService {
	@Autowired
	BorrowerDAO brdao;
	
	@Autowired
	BookDAO bdao;
	
	@Autowired
	BookLoansDAO bldao;

	
	@GetMapping(value="/borrowers",produces = {"application/json","application/xml"})
	public List<Borrower> readAllBorrowersByAd(@RequestParam String name) throws SQLException {
		List<Borrower> borrowers = new ArrayList<>();
		try {
			if(name!=null&&!name.isEmpty()) {
				borrowers = brdao.readBorrowersByName(name);
			} else {
				borrowers = brdao.readAllBorrowers();
			}
			for(Borrower br:borrowers) {
				br.setBooks(bdao.readAllBooksByBorrower(br.getCardNo()));
				br.setBookLoans(bldao.readLoansByBorrower(br.getCardNo()));
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return borrowers;
	}
	
	@Transactional
	@PutMapping(value="/borrower",consumes = {"application/json","application/xml"})
	public Borrower updateBorrowers(@RequestBody Borrower borrower) throws SQLException {
		try {
			brdao.updateBorrower(borrower);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return borrower;
	}
	
	@Transactional
	@PostMapping(value="/borrower",produces = {"application/json","application/xml"},consumes = {"application/json","application/xml"})
	public Borrower addBorrowers(@RequestBody Borrower borrower) throws SQLException {
		try {
			brdao.createBorrower(borrower);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return borrower;
	}
	
	@Transactional
	@DeleteMapping(value="/borrower/{card-no}",consumes = {"application/json","application/xml"})
	public void deleteBorrowers(@PathVariable("card-no") Integer cardNo) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		if(cardNo==null||brdao.getBorrowerByPK(cardNo)==null) {
			throw new ConstraintViolationException("Bad Request", Collections.emptySet());
		}
		try {
			brdao.deleteBorrower(cardNo);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
}
