package obpro.common;
/*
 * BReflection.java
 * Copyright(c) 2005 CreW Project. All rights reserved.
 */

/**
 * Class BReflection
 * 
 * @author macchan
 * @version $Id: BReflection.java,v 1.1 2007/06/13 07:45:05 macchan Exp $
 */
public class BReflection {

	public static Object createInstanceByName(String className) {
		try {
			return Class.forName(className).newInstance();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

	}
}