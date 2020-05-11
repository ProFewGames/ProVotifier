package com.profewgames.provotifier.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.profewgames.provotifier.VotingUser;
import com.profewgames.provotifier.libs.database.DBPool;
import com.profewgames.provotifier.libs.database.RepositoryBase;
import com.profewgames.provotifier.libs.database.column.ColumnInt;
import com.profewgames.provotifier.libs.database.column.ColumnVarChar;

public class Repository extends RepositoryBase {

	private final String CREATE_VOTES_TABLE = "CREATE TABLE IF NOT EXISTS `votes` (`id` INT AUTO_INCREMENT NOT NULL, `username` VARCHAR(16), `uuid` VARCHAR(50), `total` INT, `usable` INT, PRIMARY KEY(id));";
	private final String RETRIEVE_VOTES = "SELECT * FROM `votes` WHERE 1;";
	private final String RETRIEVE_VOTE = "SELECT * FROM `votes` WHERE `uuid`=?;";
	private final String UPDATE_VOTES = "UPDATE `votes` SET `username`=?, `total`=?, `usable`=? WHERE `uuid`=?;";
	private final String INSERT_VOTES = "INSERT INTO `votes` (`username`, `uuid`, `total`, `usable`) VALUES (?, ?, 1, ?);";

	public Repository(boolean bungee) {
		super(bungee, DBPool.getDatasource(bungee));
	}

	public final void updateVotes(UUID uuid, String username, int votes) {
		VotingUser user = loadVote(uuid);
		if (user == null) {
			executeUpdate(INSERT_VOTES, new ColumnVarChar("username", 16, username),
					new ColumnVarChar("uuid", 50, uuid.toString()), new ColumnInt("usable", votes));
		} else {
			int total = votes;
			if (total < 0) {
				total = 0;
			}
			executeUpdate(UPDATE_VOTES, new ColumnVarChar("username", 16, username),
					new ColumnInt("total", Integer.valueOf(user.total + total)),
					new ColumnInt("usable", Integer.valueOf(user.usable + votes)),
					new ColumnVarChar("uuid", 50, uuid.toString()));
		}
	}

	public final VotingUser loadVote(UUID uuid) {
		List<VotingUser> list = new ArrayList<>();
		executeQuery(RETRIEVE_VOTE, resultSet -> {
			while (resultSet.next()) {
				VotingUser user = new VotingUser();
				user.uuid = UUID.fromString(resultSet.getString(3));
				user.total = resultSet.getInt(4);
				user.usable = resultSet.getInt(5);
				list.add(user);
			}
		}, new ColumnVarChar("uuid", 50, uuid.toString()));
		return list.isEmpty() ? null : list.get(0);
	}

	public final List<VotingUser> loadVotes() {
		List<VotingUser> list = new ArrayList<>();
		executeQuery(RETRIEVE_VOTES, resultSet -> {
			while (resultSet.next()) {
				VotingUser user = new VotingUser();
				user.uuid = UUID.fromString(resultSet.getString(3));
				user.total = resultSet.getInt(4);
				user.usable = resultSet.getInt(5);
				list.add(user);
			}
		});
		return list;
	}

	@Override
	protected void initialize() {
		executeUpdate(CREATE_VOTES_TABLE);
	}

	protected void update() {
	}
}