package com.profewgames.provotifier;

import java.util.Comparator;

public class VotingSorter implements Comparator<VotingUser> {

    public int compare(VotingUser a, VotingUser b) {
        return a.total > b.total ? -1 : 0;
    }
}