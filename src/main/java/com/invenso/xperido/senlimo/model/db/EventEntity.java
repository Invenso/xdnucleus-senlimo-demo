package com.invenso.xperido.senlimo.model.db;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.util.Collection;

/**
 * JPA Entity for the event table
 */
@Entity
@Table(name = "event", schema = "public", catalog = "senlimo")
@NamedQuery(name="EventEntity.byID", query="select e from EventEntity e where e.eventId = :id")
public class EventEntity {
	private Integer eventId;
	private String eventName;
	private Date eventDate;
	private Time eventTime;
	private String eventLocation;
	private EmployeeEntity employeeByEventOwningUser;
	private Collection<VisitorEntity> visitorsByEventId;

	@Id
	@Column(name = "`EventID`")
	public Integer getEventId() {
		return eventId;
	}

	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}

	@Basic
	@Column(name = "`EventName`")
	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	@Basic
	@Column(name = "`EventDate`")
	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	@Basic
	@Column(name = "`EventTime`")
	public Time getEventTime() {
		return eventTime;
	}

	public void setEventTime(Time eventTime) {
		this.eventTime = eventTime;
	}

	@Basic
	@Column(name = "`EventLocation`")
	public String getEventLocation() {
		return eventLocation;
	}

	public void setEventLocation(String eventLocation) {
		this.eventLocation = eventLocation;
	}

	@ManyToOne
	@JoinColumn(name = "`EventOwningUser`", referencedColumnName = "`UserID`", nullable = false)
	public EmployeeEntity getEmployeeByEventOwningUser() {
		return employeeByEventOwningUser;
	}

	public void setEmployeeByEventOwningUser(EmployeeEntity employeeByEventOwningUser) {
		this.employeeByEventOwningUser = employeeByEventOwningUser;
	}

	@OneToMany(mappedBy = "eventByVisitorEvent")
	public Collection<VisitorEntity> getVisitorsByEventId() {
		return visitorsByEventId;
	}

	public void setVisitorsByEventId(Collection<VisitorEntity> visitorsByEventId) {
		this.visitorsByEventId = visitorsByEventId;
	}
}
