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
import com.gcit.lms.dao.PublisherDAO;
import com.gcit.lms.model.Publisher;

@RestController
@RequestMapping("/admin")
public class AdminPublisherService {

	@Autowired
	PublisherDAO pdao;
	
	@Autowired
	BookDAO bdao;

	@Transactional
	@PostMapping(value="/publisher",produces = {"application/json","application/xml"},consumes = {"application/json","application/xml"})
	public Publisher addPublisher(@RequestBody Publisher publisher) throws SQLException {
		try {
			pdao.createPublisher(publisher);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			System.out.println("Something went wrong, sorry!");
			e.printStackTrace();
		}
		return publisher;
	}

	@Transactional
	@PutMapping(value="/publisher",consumes = {"application/json","application/xml"})
	public Publisher updatePublisher(@RequestBody Publisher publisher) throws SQLException{
		try {
			pdao.updatePublisher(publisher);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			System.out.println("Something went wrong, sorry!");
			e.printStackTrace();
		}
		return publisher;
	}

	
	@GetMapping(value="/publishers",produces = {"application/json","application/xml"})
	public List<Publisher> readAllPublishers(@RequestParam String publisherName) throws SQLException {
		List<Publisher> publishers = new ArrayList<>();
		try {
			if(publisherName!=null&&!publisherName.isEmpty()) {
				publishers = pdao.readPublishersByName(publisherName);
			} else {
				publishers = pdao.readAllPublishers();
			}
			for(Publisher p:publishers) {
				p.setBooks(bdao.readBooksByPubId(p.getPublisherId()));
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			System.out.println("Something went wrong, sorry!");
			e.printStackTrace();
		}
		return publishers;
	}

	@GetMapping(value="/publishers/{publisher-id}",produces = {"application/json","application/xml"})
	public Publisher readPublisherByPK(@PathVariable("publisher-id") Integer publisherId) throws SQLException {
		Publisher publisher = new Publisher();
		try {
			publisher = pdao.getPublisherByPK(publisherId);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			System.out.println("Something went wrong, sorry!");
			e.printStackTrace();
		}
		return publisher;
	}

	@Transactional
	@DeleteMapping(value="/publisher/{publisher-id}",consumes = {"application/json","application/xml"})
	public void deletePublisher(@PathVariable("publisher-id") Integer publisherId) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		if(publisherId==null||pdao.getPublisherByPK(publisherId)==null) {
			throw new ConstraintViolationException("Bad Request", Collections.emptySet());
		}
		try {
			pdao.deletePublisher(publisherId);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			System.out.println("Something went wrong, sorry!");
			e.printStackTrace();
		}
	}
}
