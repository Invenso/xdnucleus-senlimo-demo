package com.invenso.xperido.senlimo.model;

import com.invenso.xperido.senlimo.model.db.*;
import com.invenso.xperido.senlimo.model.xml.visitor.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Maps database entities to the objects generated from the VisitorData.xsd schema
 */
@Mapper(uses=IntegerMapper.class)
public interface VisitorMapper {
	VisitorMapper INSTANCE = Mappers.getMapper( VisitorMapper.class );

	@Mapping(source="visitorId", target = "visitorID")
	@Mapping(source="eventByVisitorEvent", target = "visitorEvent")
	@Mapping(source="contactByVisitorContact", target = "visitorContact")
	Visitor dbVisitorToXmlVisitor(VisitorEntity entity);

	@Mapping(source="contactId", target = "contactID")
	@Mapping(source="contactVisitorBarcode", target = "contactVsitorBarcode")
	@Mapping(source="accountByContactCompanyAccount.accountId", target = "contactCompanyAccount")
	Contact dbContactToXmlContact(ContactEntity entity);

	@Mapping(source="eventId", target="eventID")
	@Mapping(source="employeeByEventOwningUser.userId", target="eventOwningUser")
	Event dbEventToXmlEvent(EventEntity entity);
}
