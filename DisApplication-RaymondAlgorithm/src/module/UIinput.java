package module;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class UIinput implements Runnable {
    private Thread thread;
    private String threadName;
    private Configuration config;

    /**
     * Constructor
     * @param config configuration
     * @param threadName thread name
     */
    public UIinput(Configuration config, String threadName) {
        this.config = config;
        this.threadName = threadName;
    }

    /**
     * Send a package
     * @param pack package to send
     */
    private void sendPack(Package pack) {
        String ip = config.getIp(pack.getId());
        int port  = config.getPort(pack.getId());

        try {
            Socket socket = new Socket(ip, port);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(pack);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        String input;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Input command: ");
            while ((input = br.readLine()) != null) {
                String[] segs = input.split(" ", 4);
                if (segs.length == 1 && input.trim().equalsIgnoreCase("exit")) break;
                else if (segs.length >= 3) {
                    String id = segs[0];
                    String filename = segs[1];
                    String operation = segs[2];
                    String content = (segs.length == 4)? segs[3] : null;
                    Package pack = new Package(filename, id, operation, content);
                    sendPack(pack);
                } else {
                    System.out.println("Input format: <ID> <filename> <operation> <content>");
                }
                System.out.println("Input command: ");
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start running the UI thread
     */
    public void start() {
        if (thread == null) {
            System.out.println(threadName + " starts");
            thread = new Thread(this, threadName);
            thread.start();
        }
    }
}
