package com.akaxin.site.storage.mongodb;

public class MongodbManager {
//	public static void main(String[] args) {
//		try {
//			// 连接到MongoDB服务 如果是远程连接可以替换“localhost”为服务器所在IP地址
//			// ServerAddress()两个参数分别为 服务器地址 和 端口
//			ServerAddress serverAddress = new ServerAddress("localhost", 27017);
//			List<ServerAddress> addrs = new ArrayList<ServerAddress>();
//			addrs.add(serverAddress);
//
//			// MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
//			MongoCredential credential = MongoCredential.createScramSha1Credential("username", "databaseName",
//					"passwd".toCharArray());
//			List<MongoCredential> credentials = new ArrayList<MongoCredential>();
//			credentials.add(credential);
//
//			// 通过连接认证获取MongoDB连接
//			MongoClient mongoClient = new MongoClient(addrs, credentials);
//
//			// 连接到数据库
//			MongoDatabase mongoDatabase = mongoClient.getDatabase("databaseName");
//			
//			mongoDatabase.getCollection("");
//			
//			System.out.println("Connect to database successfully");
//		} catch (Exception e) {
//			System.err.println(e.getClass().getName() + ": " + e.getMessage());
//		}
//	}
}
