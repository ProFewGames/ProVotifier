package com.profewgames.provotifier.net;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;

import com.profewgames.provotifier.ProVotifier;
import com.profewgames.provotifier.UUIDFetcher;
import com.profewgames.provotifier.crypto.RSA;
import com.profewgames.provotifier.model.Vote;
import com.profewgames.provotifier.model.VoteListener;
import com.profewgames.provotifier.repository.Repository;

public class VoteReceiver extends Thread {
	private static final Logger LOG;
	private final ProVotifier votifier;
	private final String host;
	private final int port;
	private ServerSocket server;
	private boolean running;
	private final Repository repository;

	static {
		LOG = Logger.getLogger("ProVotifier");
	}

	public VoteReceiver(final ProVotifier votifier, final String host, final int port) throws Exception {
		this.running = true;
		this.votifier = votifier;
		this.host = host;
		this.port = port;
		this.repository = new Repository(votifier.isBungeecord());
		this.initialize();
	}

	private void initialize() throws Exception {
		try {
			(this.server = new ServerSocket()).bind(new InetSocketAddress(this.host, this.port));
		} catch (Exception ex) {
			VoteReceiver.LOG.log(Level.SEVERE, "Error initializing vote receiver. Please verify that the configured");
			VoteReceiver.LOG.log(Level.SEVERE, "IP address and port are not already in use. This is a common problem");
			VoteReceiver.LOG.log(Level.SEVERE,
					"with hosting services and, if so, you should check with your hosting provider.", ex);
			throw new Exception(ex);
		}
	}

	public void shutdown() {
		this.running = false;
		if (this.server == null) {
			return;
		}
		try {
			this.server.close();
		} catch (Exception ex) {
			VoteReceiver.LOG.log(Level.WARNING, "Unable to shut down vote receiver cleanly.");
		}
	}

	@Override
	public void run() {
		while (this.running) {
			try {
				final Socket socket = this.server.accept();
				socket.setSoTimeout(5000);
				final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				final InputStream in = socket.getInputStream();
				writer.write("VOTIFIER 1");
				writer.newLine();
				writer.flush();
				byte[] block = new byte[256];
				in.read(block, 0, block.length);
				block = RSA.decrypt(block,this.votifier.getKeyPair().getPrivate());
				int position = 0;
				final String opcode = this.readString(block, position);
				position += opcode.length() + 1;
				if (!opcode.equals("VOTE")) {
					throw new Exception("Unable to decode RSA");
				}
				final String serviceName = this.readString(block, position);
				position += serviceName.length() + 1;
				final String username = this.readString(block, position);
				position += username.length() + 1;
				final String address = this.readString(block, position);
				position += address.length() + 1;
				final String timeStamp = this.readString(block, position);
				position += timeStamp.length() + 1;
				sendVote(serviceName, username, address, timeStamp);
				writer.close();
				in.close();
				socket.close();
			} catch (SocketException ex2) {
				VoteReceiver.LOG.log(Level.WARNING, "Protocol error. Ignoring packet - " + ex2.getLocalizedMessage());
				ex2.printStackTrace();
			} catch (BadPaddingException ex3) {
				VoteReceiver.LOG.log(Level.WARNING,
						"Unable to decrypt vote record. Make sure that that your public key");
				VoteReceiver.LOG.log(Level.WARNING, "matches the one you gave the server list.", ex3);
			} catch (Exception ex4) {
				VoteReceiver.LOG.log(Level.WARNING, "Exception caught while receiving a vote notification", ex4);
			}
		}
	}

	public void sendVote(String serviceName, String username, String address, String timeStamp) {
		final Vote vote = new Vote();
		vote.setServiceName(serviceName);
		vote.setUsername(username);
		vote.setAddress(address);
		vote.setTimeStamp(timeStamp);
		if (this.votifier.isDebug()) {
			VoteReceiver.LOG.info("Received vote record -> " + vote);
		}
		for (final VoteListener listener : this.votifier.getListeners()) {
			try {
				listener.voteMade(vote);
			} catch (Exception ex) {
				final String vlName = listener.getClass().getSimpleName();
				VoteReceiver.LOG.log(Level.WARNING,
						"Exception caught while sending the vote notification to the '" + vlName + "' listener", ex);
			}
		}
		if(this.votifier.isBungeecord()) {
			net.md_5.bungee.api.ProxyServer.getInstance().getScheduler().runAsync(net.md_5.bungee.api.ProxyServer.getInstance().getPluginManager().getPlugin("ProVotifier"), () -> {
				try {
					UUID uuid = UUIDFetcher.getUUID(username);
				repository.updateVotes(uuid, username, 1);
					System.out.println("Vote added for " + username);
				} catch (Exception e) {
					System.out.println("Could not fetch " + username + "'s UUID");
				}
			});
		} else {
			org.bukkit.Bukkit.getScheduler().runTaskAsynchronously(org.bukkit.Bukkit.getPluginManager().getPlugin("ProVotifier"), () -> {
				try {
					UUID uuid = UUIDFetcher.getUUID(username);
					repository.updateVotes(uuid, username, 1);
					System.out.println("Vote added for " + username);
				}
				catch(Exception e) {
					System.out.println("Could not fetch " + username + "'s UUID");
				}
			});
		}
	}

	private String readString(final byte[] data, final int offset) {
		final StringBuilder builder = new StringBuilder();
		for (int i = offset; i < data.length && data[i] != 10; ++i) {
			builder.append((char) data[i]);
		}
		return builder.toString();
	}
}
