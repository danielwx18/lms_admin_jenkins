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

import com.gcit.lms.dao.BookCopiesDAO;
import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.LibraryBranchDAO;
import com.gcit.lms.model.LibraryBranch;

@RestController
@RequestMapping("/admin")
public class AdminLibraryBranchService {

	@Autowired
	LibraryBranchDAO ldao;
	
	@Autowired
	BookDAO bdao;
	
	@Autowired
	BookCopiesDAO cdao;

	
	@GetMapping(value="/branches",produces = {"application/json","application/xml"})
	public List<LibraryBranch> readAllLibraryBranchesByAd(@RequestParam String branchName) throws SQLException {
		List<LibraryBranch> branches = new ArrayList<>();
		try {
			if(branchName!=null&&!branchName.isEmpty()) {
				branches = ldao.readBranchsByName(branchName);
			} else {
				branches = ldao.readAllBranches();
			}
			for(LibraryBranch lb:branches) {
				lb.setBookCopies(cdao.readAllBookCopiesByBranch(lb));
				lb.setBooks(bdao.readAllBooksByBranch(lb.getBranchId()));
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return branches;
	}
	
	@Transactional
	@PutMapping(value="/branch",consumes = {"application/json","application/xml"})
	public LibraryBranch updateLibraryBranches(@RequestBody LibraryBranch branch) throws SQLException {
		try {
			ldao.updateBranch(branch);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return branch;
	}
	
	@Transactional
	@PostMapping(value="/branch",produces = {"application/json","application/xml"},consumes = {"application/json","application/xml"})
	public LibraryBranch addLibraryBranches(@RequestBody LibraryBranch branch) throws SQLException {
		try {
			ldao.createBranch(branch);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return branch;
	}
	
	@Transactional
	@DeleteMapping(value="/branch/{branch-id}",consumes = {"application/json","application/xml"})
	public void deleteLibraryBranches(@PathVariable("branch-id") Integer branchId) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		if(branchId==null||ldao.getBranchByPK(branchId)==null) {
			throw new ConstraintViolationException("Bad Request", Collections.emptySet());
		}
		try {
			ldao.deleteBranch(branchId);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
}
