package com.akaxin.site.boot.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Helper {
	private static final Logger logger = LoggerFactory.getLogger(Helper.class);

	/**
	 * 
	 * @param args
	 * @return true:only print helper message false:start openzaly-server
	 */
	public static boolean startHelper(String[] args) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(System.out);
			Options options = new Options();
			options.addOption("h", false, "help message list");
			options.addOption("help", false, "help message list");
			DefaultParser posixParser = new DefaultParser();
			CommandLine commandLine = posixParser.parse(options, args);

			if (commandLine.hasOption("h") || commandLine.hasOption("help")) {
				// 1.print logo
				printLogoToSystemOut(pw);
				// 2.print Userage
				HelpFormatter helpFormatter = new HelpFormatter();
				helpFormatter.printHelp("java [<name>=value] -jar openzaly-server.jar [-h|-help]", options, false);
				// 3.print helper message
				printHelperMessage(pw);
				return true;
			}
			return false;
		} catch (ParseException e) {
			logger.error("print helper with startHelper error", e);
		} finally {
			if (pw != null) {
				pw.close();
			}
		}

		return true;
	}

	private static void printLogoToSystemOut(PrintWriter pw) {
		BufferedReader buffer = null;
		try {
			File file = new File("./logo.txt");
			buffer = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = buffer.readLine()) != null) {
				pw.println(line);
			}
		} catch (Exception e) {
			logger.error("read helper message from file error", e);
		} finally {
			// 关闭buffer
			try {
				if (buffer != null) {
					buffer.close();
				}
			} catch (IOException e) {
				logger.error("buffer close error with IOException");
			}
		}
	}

	private static void printHelperMessage(PrintWriter pw) {
		pw.println();
		pw.println("example:java -Dsite.port=2021 -jar openzaly-server.jar ");
		pw.println();
		pw.println("\t-Dsite.project.env \topenzaly server environment default:ONLINE");
		pw.println("\t-Dsite.version \t\topenzaly server version default:0.3.2");
		pw.println("\t-Dsite.address \t\topenzaly Netty address default:0.0.0.0");
		pw.println("\t-Dsite.port \t\topenzaly Netty port default:2021");
		pw.println("\t-Dhttp.address \t\topenzaly Http address default:0.0.0.0");
		pw.println("\t-Dhttp.port \t\topenzaly Http port default:8080");
		pw.println("\t-Dsite.admin.address \topenzaly AdminSystem address default:127.0.0.1");
		pw.println("\t-Dsite.admin.port \topenzaly AdminSystem port default:8081");
		pw.println("\t-Dsite.admin.uic \topenzaly first uic for admin port default:000000");
		pw.println("\t-Dsite.baseDir \t\topenzaly openzaly-server root dir default:./");
		pw.println("\t-Dgroup.members.count \topenzaly Max group member size default:100");
		pw.println();
		pw.flush();
	}
}
