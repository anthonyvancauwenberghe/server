package org.hyperion.rs2.model.content.clan;

public class ClanMember {
	private String name;
	private int rank;

	public ClanMember(String name, int rank) {
		this.setName(name);
		this.setRank(rank);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getRank() {
		return rank;
	}
}
