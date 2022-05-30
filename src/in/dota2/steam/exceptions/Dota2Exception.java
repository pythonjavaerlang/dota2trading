/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2008-2011, Sebastian Staudt
 */

package in.dota2.steam.exceptions;

/**
 * This exception class is used as a base class for all exceptions related to
 * Dota2.in operation
 *
 */
public class Dota2Exception extends Exception {

    /**
     * Creates a new <code>SteamCondenserException</code> instance
     */
    public Dota2Exception() {}

    /**
     * Creates a new <code>SteamCondenserException</code> instance
     *
     * @param message The message to attach to the exception
     */
    public Dota2Exception(String message) {
        super(message);
    }

    /**
     * Creates a new <code>SteamCondenserException</code> instance
     *
     * @param message The message to attach to the exception
     * @param cause The initial error that caused this exception
     */
    public Dota2Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
