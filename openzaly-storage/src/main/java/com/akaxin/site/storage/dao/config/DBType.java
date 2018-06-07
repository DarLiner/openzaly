package com.akaxin.site.storage.dao.config;

public enum DBType {
	SQLITE(1, "sqlite"), // sqlite
	MYSQL(2, "mysql");

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

		return SQLITE;
	}
}
