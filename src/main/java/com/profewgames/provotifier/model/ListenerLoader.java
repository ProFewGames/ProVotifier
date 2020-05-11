package com.profewgames.provotifier.model;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListenerLoader {
	private static final Logger LOG;

	static {
		LOG = Logger.getLogger("ProVotifier");
	}

	@SuppressWarnings("resource")
	public static List<VoteListener> load(final String directory) {
		final List<VoteListener> listeners = new ArrayList<VoteListener>();
		final File dir = new File(directory);
		if (!dir.exists()) {
			ListenerLoader.LOG.log(Level.WARNING, "No listeners loaded! Cannot find listener directory '" + dir + "' ");
			return listeners;
		}
		ClassLoader loader;
		try {
			loader = new URLClassLoader(new URL[] { dir.toURI().toURL() }, VoteListener.class.getClassLoader());
		} catch (MalformedURLException ex) {
			ListenerLoader.LOG.log(Level.SEVERE, "Error while configuring listener class loader", ex);
			return listeners;
		}
		File[] listFiles;
		for (int length = (listFiles = dir.listFiles()).length, i = 0; i < length; ++i) {
			final File file = listFiles[i];
			if (file.getName().endsWith(".class")) {
				final String name = file.getName().substring(0, file.getName().lastIndexOf("."));
				try {
					final Class<?> clazz = loader.loadClass(name);
					final Object object = clazz.newInstance();
					if (!(object instanceof VoteListener)) {
						ListenerLoader.LOG.info("Not a vote listener: " + clazz.getSimpleName());
					} else {
						final VoteListener listener = (VoteListener) object;
						listeners.add(listener);
						ListenerLoader.LOG.info("Loaded vote listener: " + listener.getClass().getSimpleName());
					}
				} catch (Exception ex2) {
					ListenerLoader.LOG.log(Level.WARNING, "Error loading '" + name + "' listener! Listener disabled.");
				} catch (Error ex3) {
					ListenerLoader.LOG.log(Level.WARNING, "Error loading '" + name + "' listener! Listener disabled.");
				}
			}
		}
		return listeners;
	}
}
