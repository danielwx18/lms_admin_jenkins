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

import com.gcit.lms.model.Publisher;

@Component
public class PublisherDAO extends BaseDAO<Publisher> implements ResultSetExtractor<List<Publisher>> {
	
	public Integer createPublisherToGetPK(Publisher publisher)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		template.update(connection -> {
			PreparedStatement ps = connection.prepareStatement("insert into tbl_publisher (publisherName,publisherAddress,publisherPhone) values(?,?,?) ", Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, publisher.getPublisherName()); 	
			ps.setString(2, publisher.getPublisherAddress());
			ps.setString(3, publisher.getPublisherPhone());
			return ps;
		}, keyHolder);
		return keyHolder.getKey().intValue();

	}

	public Publisher getPublisherByPK(Integer publisherId)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		List<Publisher> publishers = template.query("select * from tbl_publisher where publisherId = ?",
				new Object[] { publisherId }, this);
		if (publishers != null && publishers.size() > 0) {
			return publishers.get(0);
		}
		return null;
	}

	public void createPublisher(Publisher publisher)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		template.update("insert into tbl_publisher (publisherName, publisherAddress, publisherPhone) values(?, ?, ?)",
				new Object[] { publisher.getPublisherName(), publisher.getPublisherAddress(),
						publisher.getPublisherPhone() });
	}

	public void updatePublisher(Publisher publisher)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		template.update(
				"update tbl_publisher set publisherName = ?, publisherAddress=?, publisherPhone=? where publisherId= ?",
				new Object[] { publisher.getPublisherName(), publisher.getPublisherAddress(),
						publisher.getPublisherPhone(), publisher.getPublisherId() });
	}

	public void deletePublisher(Integer publisherId)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		template.update("delete from tbl_publisher where publisherId= ?", new Object[] { publisherId });
	}

	public List<Publisher> readAllPublishers()
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		return template.query("select * from tbl_publisher", this);
	}

	public List<Publisher> readPublishersByName(String publisherName)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		return template.query("select * from tbl_publisher where publisherName = ?", new Object[] { publisherName },
				this);
	}

	@Override
	public List<Publisher> extractData(ResultSet rs) throws SQLException {
		List<Publisher> publishers = new ArrayList<Publisher>();
		while (rs.next()) {
			Publisher publisher = new Publisher();
			publisher.setPublisherName(rs.getString("publisherName"));
			publisher.setPublisherId(rs.getInt("publisherId"));
			publisher.setPublisherAddress(rs.getString("publisherAddress"));
			publisher.setPublisherPhone(rs.getString("publisherPhone"));
			publishers.add(publisher);
		}
		return publishers;
	}

}
