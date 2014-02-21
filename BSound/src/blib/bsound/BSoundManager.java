/**
 * BSoundManager.java
 * Created on 2006/06/16
 * Copyright(c) Yoshiaki Matsuzawa at CreW Project
 */
package blib.bsound;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import blib.bsound.components.BByteArrayInputStreamFactory;
import blib.bsound.components.BFileInputStreamFactory;
import blib.bsound.framework.BInputStreamFactory;


/**
 * Class BSoundManager.
 * 
 * @author macchan
 */
public class BSoundManager {

	private Map<URL, BInputStreamFactory> data = new HashMap<URL, BInputStreamFactory>();

	/**
	 * Constructor.
	 */
	protected BSoundManager() {
		super();
	}

	protected void loadSound(URL file) throws Exception {
		if (!isLoaded(file)) {
			load(file);
		}
	}

	protected BInputStreamFactory getInputStreamFactory(URL file) {
		if (isLoaded(file)) {
			return data.get(file);
		} else {
			return createFileInputStreamFactory(file);
		}
	}

	private boolean isLoaded(URL file) {
		return data.containsKey(file);
	}

	private void load(URL file) throws Exception {
		data.put(file, createByteArrayInputStreamFactory(file));
	}

	private BByteArrayInputStreamFactory createByteArrayInputStreamFactory(
			URL file) throws Exception {
		// FileInputStream stream = new FileInputStream(file);
		InputStream stream = file.openStream();
		int size = stream.available();
		byte[] buf = new byte[size];
		stream.read(buf);
		return new BByteArrayInputStreamFactory(buf);
	}

	private BFileInputStreamFactory createFileInputStreamFactory(URL file) {
		return new BFileInputStreamFactory(file);
	}

}
