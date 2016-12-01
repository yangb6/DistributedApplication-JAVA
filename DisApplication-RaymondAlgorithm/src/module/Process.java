package module;

import model.Pair;
import model.Request;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Created by Bing on 10/15/16.
 */
public class Process implements Runnable{
    // String indicate fileName
    private Thread thread;
    private String threadName;
    public Configuration config;
    public HashMap<String, Boolean> usingResource;
    public HashMap<String, String> holder;
    public HashMap<String, Queue<Package>> reqQ;
    public HashMap<String, Boolean> asked;
    public HashMap<String, ArrayList<String>> content;
    public Map<String, Pair<String, Integer>> addressMap;
    public List<String> neighbourList;
    public String id;

    public Process(Configuration config, String threadName) {
        this.threadName = threadName;
        this.config = config;
        this.id = config.getCurrentId();
        this.usingResource = new HashMap<>();
        this.holder = new HashMap<>();
        this.reqQ = new HashMap<>();
        this.asked = new HashMap<>();
        this.content = new HashMap<>();
        this.neighbourList = config.getNeighborList();
        this.addressMap = config.getAddressMap();
    }

    @Override
    public void run() {
        int port = config.getPort();
        try {
            System.out.println("port: " + port);
            ServerSocket ss = new ServerSocket(port);
            while(true) {
                Socket socket = ss.accept();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Package pack = (Package) ois.readObject();
                System.out.println("Receive pack: "+ threadName + "\t" + pack);
                processRequest(pack);
            }

//            System.out.println(threadName + " exits");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void start() {
        if (thread == null) {
            System.out.println(threadName + " starts");
            thread = new Thread(this, threadName);
            thread.start();
        }
    }

    public void processRequest(Package pack) {
        Processor processor = new Processor("", pack, this);
        processor.start();
    }
}
