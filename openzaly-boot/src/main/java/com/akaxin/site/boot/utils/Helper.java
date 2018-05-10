package com.akaxin.site.boot.utils;

import org.apache.commons.cli.*;

public class Helper {
    public static boolean startHelper(String[] args) {

        Options options = new Options();
        OptionGroup nonArg = new OptionGroup();
        nonArg.addOption(new Option("h", false, "Lists short help"));
        options.addOptionGroup(nonArg);
        OptionGroup withArg = new OptionGroup();
        withArg.addOption(new Option("Dsite.project.env", true, "set OpenZayl Netty Port, default:2021"));
        withArg.addOption(new Option("http_port", true, "set OpenZayl Http Port, default:8080"));
        withArg.addOption(new Option("site_address", true, "set OpenZayl Netty Address, default:0.0.0.0"));
        withArg.addOption(new Option("http_address", true, "set OpenZayl Http Address, default:0.0.0.0"));
        withArg.addOption(new Option("admin_port", true, "set OpenZayl Admin System Port, default:8081"));
        withArg.addOption(new Option("first_uic", true, "set OpenZayl First Uic, default:000000"));
        withArg.addOption(new Option("base_dir", true, "set OpenZayl Root Dir, default:./"));
        withArg.addOption(new Option("max_Gmember", true, "set OpenZayl Max Group Member Size, default:100"));
        options.addOptionGroup(withArg);
        PosixParser posixParser = new PosixParser();
        CommandLine commandLine = null;
        try {
            commandLine = posixParser.parse(options, args);

            if (commandLine.hasOption("h")) {
                System.out.println("Akaxin is an open source and free proprietary IM software，you can build private openzaly-server for everyone in any server.");
                System.out.println("    _      _  __     _     __  __  ___   _   _ \n" +
                        "   / \\    | |/ /    / \\    \\ \\/ / |_ _| | \\ | |\n" +
                        "  / _ \\   | ' /    / _ \\    \\  /   | |  |  \\| |\n" +
                        " / ___ \\  | . \\   / ___ \\   /  \\   | |  | |\\  |\n" +
                        "/_/   \\_\\ |_|\\_\\ /_/   \\_\\ /_/\\_\\ |___| |_| \\_|\n");
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("OptionsTip", options, true);
                return false;
            }
            if (commandLine.hasOption("site_port")) {
                String site_port = commandLine.getOptionValue("site_port");

            }


            return true;
        } catch (ParseException e) {
            System.out.println("input error See '-h' for more help.");
            return false;
        }
    }
}
