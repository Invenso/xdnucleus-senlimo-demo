package com.invenso.xperido.senlimo.resources.exceptions;

/**
 * Base exception for this demo
 */
public class XDNucleusDemoException extends RuntimeException {

    public XDNucleusDemoException(String message) {
        super(message);
    }

    public XDNucleusDemoException(String message, Throwable cause) {
        super(message, cause);
    }

}
