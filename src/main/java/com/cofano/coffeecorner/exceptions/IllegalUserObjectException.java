package com.cofano.coffeecorner.exceptions;

/**
 * This class is called upon an error parsing or reading a {@link User} object
 * 
 * @author Jasper van Amerongen
 * @author Nidanur Gunay
 * @author Adamo Mariani
 * @author Albina Shynkar
 * @author Eda Yardim
 * @author Lola Solovyeva
 *
 */
@SuppressWarnings("serial")
public class IllegalUserObjectException extends UserException {

	public IllegalUserObjectException() {
		super("The provided User id is not found within the system.", 1);
	}

}
