package module;

import model.Pair;
import model.Request;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Bing on 10/15/16.
 */
public class Processor implements Runnable {
    private Thread thread;
    private String threadName;
    private Process process;
    private Package pack;

    public Processor(String threadName, Package pack, Process process) {
        this.threadName = threadName;
        this.process = process;
        this.pack = pack;
    }

    @Override
    public void run() {
        if (pack.isToken()) {
            receiveToken(pack.getFilename());
        } else {
            String operation = pack.getOperation();

            if (operation.equalsIgnoreCase("created")) {
                onCreating(pack);
            } else if (operation.equalsIgnoreCase("deleted")) {
                onDeleting(pack);
            } else if (operation.equalsIgnoreCase("appended")) {
                onAppending(pack);
            }
            else if (operation.equalsIgnoreCase("create")) {
                System.out.println("create file " + pack.getFilename());
                create(pack.getFilename());
            }
            else
            {
                receiveRequest(pack);
            }/*else if (operation.equalsIgnoreCase("delete")) {
                delete(pack);
            } else if (operation.equalsIgnoreCase("append")) {
                append(pack);
            } else if (operation.equalsIgnoreCase("read")) {
                read(pack);
            }*/
        }
    }

    /**
     * Start thread
     */
    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    /* ==========
     * Operations
     * ==========
     */

    /**
     * Send a package to the target process
     * @param pack package to send
     * @param target target process ID
     */
    private void sentInfo(Package pack, String target) {
        System.out.println("Send pack: "+" "+pack+" target: "+target);
        String ip = process.config.getIp(target);
        int port  = process.config.getPort(target);
        try {
            Socket socket = new Socket(ip, port);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(pack);
            oos.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Received a token
     * @param filename file name of the token
     */
    private void receiveToken(String filename) {
        System.out.println("Get the token: " + filename );
        process.holder.put(filename, process.id);
        assignToken(filename);
        if(process.reqQ.containsKey(filename)) {
            Queue<Package> queue = process.reqQ.get(pack.getFilename());
            if (queue.size() != 0) {
                Package newPack = process.reqQ.get(pack.getFilename()).peek();
                sendRequest(newPack);
            }
        }
    }

    private void assignToken(String filename) {
        if (process.holder.get(filename).equals(process.id)
                && !process.usingResource.get(filename)
                && (process.reqQ.get(filename).size() != 0)) {
            Package newPack = process.reqQ.get(filename).peek() ;
            process.reqQ.get(filename).poll();
            System.out.println("Operation Type: " + newPack.getOperation() );
            process.holder.put(filename, newPack.getId());
            process.asked.put(filename,false);
            if(process.holder.get(filename).equals(process.id))
            {
                if(newPack.getOperation().equals("delete"))
                {
                    System.out.println("Delete: " +newPack.getFilename());
                    delete(newPack);
                }
                else if(newPack.getOperation().equals("read"))
                {
                    System.out.println("Read " +newPack.getFilename()+ " Content:");
                    read(newPack);
                }
                else if(newPack.getOperation().equals("append"))
                {
                    System.out.println("Append "+newPack.getContent()+ " to " +newPack.getFilename()+":");
                    append(newPack);
                }
                else {
                   System.err.println("Error: Invalid Opreation Type!");
                }

            }
            else
            {
                Package tokenPack = new Package(filename);
                sentInfo(tokenPack,process.holder.get(filename));
            }
        }
    }

    /**
     * Send a request
     * @param pack package to send
     */
    private void sendRequest(Package pack) {
        if(process.holder.containsKey(pack.getFilename())) {
            if (!process.holder.get(pack.getFilename()).equals(process.id)
                    && !process.reqQ.get(pack.getFilename()).isEmpty()
                    && !process.asked.get(pack.getFilename())) {
                Package newPack = new Package(pack);
                newPack.setId(process.id);
                sentInfo(newPack, process.holder.get(newPack.getFilename()));
                process.asked.put(newPack.getFilename(), true);
            }
        }

    }

    /**
     * Create a file
     * @param filename file name
     */
    public void create(String filename) {
        ArrayList<String> lineList = new ArrayList<>();
        process.content.put(filename, lineList);

        process.usingResource.put(filename, false);
        process.asked.put(filename, false);

        process.holder.put(filename, process.id);
        Queue<Package> queue = new LinkedList<>();
        process.reqQ.put(filename, queue);

        // send notifications
        Package notifyPack = new Package(filename, process.id, "created", null);
        for (String neighbour: process.neighbourList) {
            sentInfo(notifyPack, neighbour);
        }
    }


    public void releaseResource(Package pack) {
        process.usingResource.put(pack.getFilename(), false);
        assignToken(pack.getFilename());
        sendRequest(pack);
    }

    public void receiveRequest( Package pack) {
        System.out.println("Come here");
        Queue<Package> queue = process.reqQ.get(pack.getFilename());
        queue.offer(pack);
        if(process.reqQ.get(pack.getFilename()).isEmpty())
        {
            System.out.println("Queue Empty");
        }
        assignToken(pack.getFilename());
        sendRequest(pack);
    }






    /**
     * Received a file created notification
     * @param pack
     */
    public void onCreating(Package pack) {
        ArrayList<String> lineList = new ArrayList<>();
        process.content.put(pack.getFilename(), lineList);

        process.usingResource.put(pack.getFilename(), false);
        process.holder.put(pack.getFilename(), pack.getId());

        Queue<Package> queue = new LinkedList<>();
        process.reqQ.put(pack.getFilename(), queue);
        process.asked.put(pack.getFilename(), false);

        Package newPack = new Package(pack);
        newPack.setId(process.id);
        for (String neighbour: process.neighbourList) {
            // let neighbours create file relevant, except the sender
            if (neighbour.equals(pack.getId())) {
                continue;
            }
            sentInfo(newPack, neighbour);
        }
    }

    public void delete(Package infoPackage) {
        if (process.holder.containsKey(infoPackage.getFilename())) {
            process.usingResource.remove(infoPackage.getFilename());
            process.holder.remove(infoPackage.getFilename());
            process.reqQ.remove(infoPackage.getFilename());
            process.asked.remove(infoPackage.getFilename());
            process.content.remove(infoPackage.getFilename());
            infoPackage.setId(process.id);
            // let neighbours delete if they have it
            infoPackage.setOperation("deleted");
            for (String neighbour: process.neighbourList) {
                sentInfo(infoPackage, neighbour);
            }
        }
    }

    public void onDeleting(Package infoPackage) {
        if (process.holder.containsKey(infoPackage.getFilename())) {
            process.usingResource.remove(infoPackage.getFilename());
            process.holder.remove(infoPackage.getFilename());
            process.reqQ.remove(infoPackage.getFilename());
            process.asked.remove(infoPackage.getFilename());
            process.content.remove(infoPackage.getFilename());
            Package newPackage = new Package(infoPackage);
            newPackage.setId(process.id);
            // let neighbours delete if they have it
            for (String neighbour: process.neighbourList) {
                if (neighbour.equals(infoPackage.getId()) ) {
                    continue;
                }
                sentInfo(newPackage, neighbour);
            }
        }
    }

    public void read(Package infoPackage) {
        String fileName = infoPackage.getFilename();
        for (String line: process.content.get(fileName)) {
            System.out.println(line);
        }
        Queue<Package> queue = process.reqQ.get(infoPackage.getFilename());
        if(queue.size() != 0)
        {
            Package newPack = process.reqQ.get(infoPackage.getFilename()).peek();
            releaseResource(newPack);
        }
    }

    public void append(Package infoPackage) {
        String fileName = infoPackage.getFilename();
        String newLine = infoPackage.getContent();
        System.out.println("Append Content: " + newLine);
        process.content.get(fileName).add(newLine);
        Package newPackage = new Package(infoPackage);
        newPackage.setId(process.id);
        newPackage.setOperation("appended");
        newPackage.setContent(newLine);
        // let neighbours append
        for (String neighbour: process.neighbourList) {
            sentInfo(newPackage, neighbour);
        }
        Queue<Package> queue = process.reqQ.get(infoPackage.getFilename());
        if(queue.size() != 0)
        {
            System.out.println("Release Resource");
            Package tmpPack = process.reqQ.get(infoPackage.getFilename()).peek();
            releaseResource(tmpPack);
        }

    }

    /**
     * Received a file appended notification
     * @param pack
     */
    public void onAppending(Package pack) {
        String filename = pack.getFilename();
        String newLine = pack.getContent();
        process.content.get(filename).add(newLine);
        // let neighbours append
        Package newPack = new Package(pack);
        newPack.setId(process.id);
        for (String neighbour: process.neighbourList) {

            if (neighbour.equals(pack.getId())) {
                continue;
            }
            sentInfo(newPack, neighbour);
        }
    }



}
