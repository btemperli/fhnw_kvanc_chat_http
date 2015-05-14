/*
 * Copyright (c) 2000-2009 Fachhochschule Nordwestschweiz (FHNW)
 * All Rights Reserved. 
 */

package ch.fhnw.kvan.chat.general;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import ch.fhnw.kvan.chat.servlet.client.Client;

/**
 * Class StartClient is used to start the Client side of the chat application. As a
 * runtime parameter the name of the class which implements the
 * <code>Client</code> class has to be specified. This class is then
 * loaded and its main() method invoked, using corresponding arguments.
 * 
 * E.g. start the application with one of the following commands, which are defined in the Clients.txt file. 
 * 
 * <pre>
 * ch.fhnw.kvan.chat.servlet.client.Client localhost 1234 <user name>
 * ch.fhnw.kvan.chat.servlet.client.Client localhost:8080/chat/Server <user name>
 * </pre>
 * 
 * @see Client 
 * @author  � ibneco, Rheinfelden; based on code by Dominik Gruntz
 * @version 1.0
 */
public class StartClient {
	
	private static Logger logger;

	public static void main(String args[]) {
		// Log4J initialisation
		logger = Logger.getLogger(StartClient.class);

		if (args.length < 1) {
			logger.error("Usage: java " + StartClient.class.getName()
					+ " <class>");
			System.exit(1);
		}

        try {
        	// get class for name
			Class<?> cls = Class.forName(args[0]);
			// get its main method
			Method meth = cls.getDeclaredMethod("main", String[].class);
			// the different Client classes need different arguments
			// communication via servlet: package name contains "servlet"
			if (args[0].contains("servlet")) {
				logger.info(args[0]);
				logger.info(args[1]);
				logger.info(args[2]);
				// prepare right parameters
				String[] params = {args[2], args[1]};
				// invoke main()
				meth.invoke(null, (Object) params);
			}
		} catch (ClassNotFoundException e) {
			logger.error("class " + args[0] + " could not be found");
			System.exit(1);
		} catch (NoSuchMethodException e) {
			logger.error("class " + args[0]
					+ " does not have such method");
			System.exit(1);
		} catch (InvocationTargetException e) {
			logger.error("main method of class " + args[0]
					+ " threw an InvocationTargetException: " + e.getCause());
			System.exit(1);
		} catch (IllegalAccessException e) {
			logger.error("class " + args[0]
					+ " could not be instantiated");
			System.exit(1);
		} catch (IllegalArgumentException e) {
			logger.error("class " + args[0]
					+ " could not invoke the main method");
			System.exit(1);
		}
	}
}