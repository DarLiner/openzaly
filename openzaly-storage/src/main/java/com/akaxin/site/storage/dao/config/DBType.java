package com.akaxin.site.storage.dao.config;

public enum DBType {
	PERSONAL(1, "personal"), // sqlite
	TEAM(2, "team");

	private int index;
	private String name;

	DBType(int index, String name) {
		this.index = index;
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public static DBType getDBType(String name) {
		for (DBType db : DBType.values()) {
			if (db.getName().equalsIgnoreCase(name)) {
				return db;
			}
		}
		return PERSONAL;
	}

}
