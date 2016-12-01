package module;

import model.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Bing on 10/14/16.
 */
public class Configuration {
    private String currentId;
    private Map<String, Pair<String, Integer>> addressMap;
    private List<String> neighborList;


    /**
     * Constructor
     * @param addressTableFile address table file
     * @param neighborTableFile neighbor table file
     */
    public Configuration(String addressTableFile, String neighborTableFile) {
        addressMap = new HashMap<>();
        neighborList = new ArrayList<>();

        readConfigurationFile(addressTableFile, neighborTableFile);
    }

    public Configuration(String addressTableFile, String neighborTableFile, String currentId) {
        addressMap = new HashMap<>();
        neighborList = new ArrayList<>();

        readConfigurationFile(addressTableFile, neighborTableFile, currentId);
    }

    /**
     * Read configuration file
     * @param addressTableFile address table file
     * @param neighborTableFile neighbor table file
     */
    public void readConfigurationFile(String addressTableFile, String neighborTableFile) {
        String line;

        // read address table file
        BufferedReader br1;
        try {
            br1 = new BufferedReader(new InputStreamReader(new FileInputStream(addressTableFile), "utf-8"));
            currentId = br1.readLine();
            while ((line = br1.readLine()) != null) {
                String[] segs = line.split(" ", 3);
                String id = segs[0];
                String ip = segs[1];
                int port  = Integer.parseInt(segs[2]);
                addressMap.put(id, new Pair<>(ip, port));
            }
            br1.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // read neighbor table file
        BufferedReader br2;
        try {
            Pattern pattern = Pattern.compile(" *\\( *(\\d+) *, *(\\d+) *\\)");
            br2 = new BufferedReader(new InputStreamReader(new FileInputStream(neighborTableFile), "utf-8"));
            while ((line = br2.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String id1 = matcher.group(1);
                    String id2 = matcher.group(2);
                    if (id1.equals(currentId)) neighborList.add(id2);
                    else if (id2.equals(currentId)) neighborList.add(id1);
                }
            }
            br2.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void readConfigurationFile(String addressTableFile, String neighborTableFile, String currentId) {
        String line;

        // read address table file
        BufferedReader br1;
        try {
            br1 = new BufferedReader(new InputStreamReader(new FileInputStream(addressTableFile), "utf-8"));
//            currentId = br1.readLine();
            this.currentId = currentId;
            br1.readLine();
            while ((line = br1.readLine()) != null) {
                String[] segs = line.split(" ", 3);
                String id = segs[0];
                String ip = segs[1];
                int port  = Integer.parseInt(segs[2]);
                addressMap.put(id, new Pair<>(ip, port));
            }
            br1.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // read neighbor table file
        BufferedReader br2;
        try {
            Pattern pattern = Pattern.compile(" *\\( *(\\d+) *, *(\\d+) *\\)");
            br2 = new BufferedReader(new InputStreamReader(new FileInputStream(neighborTableFile), "utf-8"));
            while ((line = br2.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String id1 = matcher.group(1);
                    String id2 = matcher.group(2);
                    if (id1.equals(currentId)) neighborList.add(id2);
                    else if (id2.equals(currentId)) neighborList.add(id1);
                }
            }
            br2.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getCurrentId() {
        return currentId;
    }

    public Map<String, Pair<String, Integer>> getAddressMap() {
        return addressMap;
    }

    public List<String> getNeighborList() {
        return neighborList;
    }

    public int getPort() {
        return addressMap.get(currentId).getValue();
    }

    public int getPort(String id) {
        return addressMap.get(id).getValue();
    }

    public String getIp() {
        return addressMap.get(currentId).getKey();
    }

    public String getIp(String id) {
        return addressMap.get(id).getKey();
    }


}
