package pipeline;

import module.Configuration;
import module.Process;
import module.UIinput;
public class Process2 {
    public static void main(String[] args) {
        Configuration config = new Configuration("2.txt","tree.txt");
        Process pros = new Process(config, "process_" + config.getCurrentId());
        pros.start();
        UIinput ui = new UIinput(config, "ui_thread");
        ui.start();

    }
}
