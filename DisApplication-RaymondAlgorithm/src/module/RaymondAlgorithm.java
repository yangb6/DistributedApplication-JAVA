package module;
import model.Pair;
import model.Request;

import java.util.*;
import java.net.*;
import java.io.*;




public class RaymondAlgorithm {

	public static void main(String[] args) {
        // Input the configuration file name
        Configuration config = new Configuration("2.txt","tree.txt");
        Process pros = new Process(config, "process_" + config.getCurrentId());
        pros.start();
        UIinput ui = new UIinput(config, "ui_thread");
        ui.start();
	}
}
