package com.invenso.xperido.senlimo.resources.exceptions;

import com.invenso.xdws.exception.ServiceInvocationException;

/**
 * Signals there was an error on the server
 */
public class ServerSideException extends XDNucleusDemoException {
	public ServerSideException(ServiceInvocationException e) {
		super(e.getMessage(), e.getCause());
	}

	/**
	 * The original exception as sent by the SDK
	 *
	 * @return the original exception
	 */
	public ServiceInvocationException getServiceInvocationException() {
		return (ServiceInvocationException) getCause();
	}
}
