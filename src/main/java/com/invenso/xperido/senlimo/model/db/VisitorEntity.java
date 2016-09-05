package com.invenso.xperido.senlimo.model.db;

import javax.persistence.*;

/**
 * JPA Entity for the visitor table
 */
@Entity
@Table(name = "visitor", schema = "public", catalog = "senlimo")
@NamedQuery(name="VisitorEntity.byID", query="select v from VisitorEntity v where v.visitorId = :id")
public class VisitorEntity {
	private Integer visitorId;
	private EventEntity eventByVisitorEvent;
	private ContactEntity contactByVisitorContact;

	@Id
	@Column(name = "`VisitorID`")
	public Integer getVisitorId() {
		return visitorId;
	}

	public void setVisitorId(Integer visitorId) {
		this.visitorId = visitorId;
	}

	@ManyToOne
	@JoinColumn(name = "`VisitorEvent`", referencedColumnName = "`EventID`", nullable = false)
	public EventEntity getEventByVisitorEvent() {
		return eventByVisitorEvent;
	}

	public void setEventByVisitorEvent(EventEntity eventByVisitorEvent) {
		this.eventByVisitorEvent = eventByVisitorEvent;
	}

	@ManyToOne
	@JoinColumn(name = "`VisitorContact`", referencedColumnName = "`ContactID`", nullable = false)
	public ContactEntity getContactByVisitorContact() {
		return contactByVisitorContact;
	}

	public void setContactByVisitorContact(ContactEntity contactByVisitorContact) {
		this.contactByVisitorContact = contactByVisitorContact;
	}
}
