package com.invenso.xperido.senlimo.model;

import com.invenso.xperido.senlimo.model.db.ContactEntity;
import com.invenso.xperido.senlimo.model.db.EmployeeEntity;
import com.invenso.xperido.senlimo.model.db.EventEntity;
import com.invenso.xperido.senlimo.model.db.VisitorEntity;
import com.invenso.xperido.senlimo.model.xml.event.Contact;
import com.invenso.xperido.senlimo.model.xml.event.Employee;
import com.invenso.xperido.senlimo.model.xml.event.Event;
import com.invenso.xperido.senlimo.model.xml.event.Visitor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

/**
 * Maps database entities to the objects generated from the EventVisitorData.xsd schema
 */
@Mapper(uses=IntegerMapper.class)
public abstract class EventMapper {
	public final static EventMapper INSTANCE = Mappers.getMapper( EventMapper.class );

	@Mapping(source="eventId", target="eventID")
	@Mapping(source="employeeByEventOwningUser", target="eventOwner")
	@Mapping(source="visitorsByEventId", target="visitorEvent")
	public abstract Event dbEventToXmlEvent(EventEntity entity);

	public Event.VisitorEvent dbVisitorsToXmlVisitorEvent(Collection<VisitorEntity> visitors) {
		Event.VisitorEvent contract = new Event.VisitorEvent();
		contract.getVisitor().addAll(dbVisitorsToXmlVisitors(visitors));
		return contract;
	}

	public abstract List<Visitor> dbVisitorsToXmlVisitors(Collection<VisitorEntity> visitors);

	@Mapping(source="visitorId", target = "visitorID")
	@Mapping(source="eventByVisitorEvent.eventId", target = "visitorEvent")
	@Mapping(source="contactByVisitorContact", target = "visitorContact")
	public abstract Visitor dbVisitorToXmlVisitor(VisitorEntity entity);

	@Mapping(source="contactId", target = "contactID")
	@Mapping(source="contactVisitorBarcode", target = "contactVsitorBarcode")
	@Mapping(source="accountByContactCompanyAccount.accountId", target = "contactCompanyAccount")
	public abstract Contact dbContactToXmlContact(ContactEntity entity);

	@Mapping(source="userId", target = "userID")
	public abstract Employee dbEmployeeToXmlEmployee(EmployeeEntity employee);
}
