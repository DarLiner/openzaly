package com.akaxin.site.boot.utils;

import org.apache.commons.cli.*;

import javax.sound.midi.Soundbank;
import java.io.*;

public class Helper {
    public static boolean startHelper(String[] args) {
        BufferedReader buffer = null;
        Options options = new Options();
        options.addOption("h", false, "help message list");
        options.addOption("help", false, "help message list");
        PosixParser posixParser = new PosixParser();
        CommandLine commandLine = null;
        try {
            commandLine = posixParser.parse(options, args);

            if (commandLine.hasOption("h") || commandLine.hasOption("help")) {
                System.out.println();
                //读取文件打印logo
                File file = new File("classes/logo.txt");
                try {
                    buffer = new BufferedReader(new FileReader(file));
                    String line = null;
                    while ((line = buffer.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (Exception e) {
                }
                System.out.println();
                System.out.println("Akaxin is an open source and free proprietary IM software，you can build private openzaly-server for everyone in any server.");
                System.out.println();
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("java [<name>=value] -jar openzaly-server.jar [-h|-help]", options, false);
                System.out.println();
                System.out.println("example:java -Dsite.port=2021 -jar openzaly-server.jar ");
                System.out.println();
                System.out.println("\t-Dsite.project.env \topenzaly server environment default:ONLINE");
                System.out.println("\t-Dsite.version \t\topenzaly server version default:0.3.2");
                System.out.println("\t-Dsite.address \t\topenzaly Netty address default:0.0.0.0");
                System.out.println("\t-Dsite.port \t\topenzaly Netty port default:2021");
                System.out.println("\t-Dhttp.address \t\topenzaly Http address default:0.0.0.0");
                System.out.println("\t-Dhttp.port \t\topenzaly Http port default:8080");
                System.out.println("\t-Dsite.admin.address \topenzaly AdminSystem address default:127.0.0.1");
                System.out.println("\t-Dsite.admin.port \topenzaly AdminSystem port default:8081");
                System.out.println("\t-Dsite.admin.uic \topenzaly first uic for admin port default:000000");
                System.out.println("\t-Dsite.baseDir \t\topenzaly openzaly-server root dir default:./");
                System.out.println("\t-Dgroup.members.count \topenzaly Max group member size default:100");
                System.out.println();
                return false;
            }
            return true;
        } catch (ParseException e) {
            System.out.println("input error See '-h or -help' for more help.");
            return false;
        } finally {
            //关闭buffer
            try {
                buffer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
