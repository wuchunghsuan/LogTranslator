import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.*;

class Log {
    Integer disk;
    Integer net;
    Float cpu;
    Integer time;
    String hostname;
    public Log(String h ,String t ,String d, String n, String c) {
        this.disk = Integer.valueOf(d) / 1024 / 5;
        this.net = Integer.valueOf(n) / 1024 / 5;
        this.cpu = 1 - (Integer.valueOf(c)/ 100.00f);
        this.time = Integer.valueOf(t);
        this.hostname = h;
    }
}

class Data {
    Integer disk;
    Integer net;
    Float cpu;
    Integer time;
    public Data(Integer t, Integer d, Integer n, Float c) {
        this.disk = d;
        this.net = n;
        this.cpu = c;
        this.time = t;
    }
}

class Bucket {
    public Map<String, Log> dataMap;
    public Integer time;
    public Bucket(Integer t) {
        this.time = t;
        this.dataMap = new HashMap<>();
    }
}

class Trans {
    public List<Bucket> BList;
    public Trans() {
        this.BList = new ArrayList<>();
    }
    public void add(Log log) {
        log.time -= log.time % 5;
        if(BList.size() == 0 || BList.get(BList.size() - 1).time < log.time) {
            BList.add(new Bucket(log.time));
            
        }
        (BList.get(BList.size() - 1)).dataMap.put(log.hostname, log);
        System.out.println(log.hostname + "\t" + log.time + "\t" + log.disk + "\t" + log.net + "\t" + log.cpu);
    }
    public List<Data> translate() {
        List<Data> ret = new ArrayList<>();
        for(Bucket bucket : this.BList) {
            int num = bucket.dataMap.size();
            // System.out.println(num);
            Collection<Log> vals = bucket.dataMap.values();
            int net = 0;
            Float cpu = 0.00f;
            int disk = 0;
            for(Log log : vals) {
                net += log.net;
                cpu += log.cpu;
                disk += log.disk;
            }
            // System.out.println(disk);
            ret.add(new Data(bucket.time, disk / num, net / num, cpu / num));
        }
        return ret;
    }
}

public class LogTranslator {
    public static void main(String[] args)  {
        try {
            Trans trans = new Trans();
            String filename = "32";
int i = 0;
            BufferedReader in = new BufferedReader(new FileReader(filename + ".log"));
            String line;
            while ((line = in.readLine()) != null) {
                // Pattern pattern = Pattern.compile("('disk/total': Disk\\(.*?\\)).*?('net/total': Network\\(.*?\\)).*?('timestamp': )(\\d+).*?('hostname': 'ip-)(\\d+-\\d+-\\d+-\\d+).*?('cpu/total': CPU\\(.*?\\))");
                Pattern pattern = Pattern.compile("('hostname': 'ist-slave)(\\d+).*?('timestamp': )(\\d+).*?('net/total': Network\\(.*?\\))");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String hostname, time, disk = "0", net = "0", cpu = "0";

                    // String cpuStr = matcher.group(1);
                    // Pattern cpuPat = Pattern.compile("(idle=)(\\d+)");
                    // Matcher cpuMat = cpuPat.matcher(cpuStr);
                    // if(cpuMat.find()) {
                    //     cpu = cpuMat.group(2);
                    //     System.out.println("CPU recv_bytes: " + cpuMat.group(2));
                    // }
                    
                    // String diskStr = matcher.group(2);
                    // Pattern diskPat = Pattern.compile("(bytes_write=)(\\d+)");
                    // Matcher diskMat = diskPat.matcher(diskStr);
                    // if(diskMat.find()) {
                    //     disk = diskMat.group(2);
                    //     System.out.println("Disk bytes_write: " + diskMat.group(2));
                    // }
                    
                    hostname = matcher.group(2);

                    time = matcher.group(4);
                    // System.out.println("Time: " + time);

                    String netStr = matcher.group(5);
                    Pattern netPat = Pattern.compile("(recv_bytes=)(\\d+)");
                    Matcher netMat = netPat.matcher(netStr);
                    if(netMat.find()) {
                        net = netMat.group(2);
                        // System.out.println("Net recv_bytes: " + netMat.group(2));
                    }
                    
                    
                    // hostname = matcher.group(6);

                    // System.out.println(hostname + " " + time + " " + disk + " " + net + " " + cpu);
                    trans.add(new Log(hostname, time, "0", net, "0"));
                }
                else {
                    System.out.println("Not Found.");
                }
            }
            System.out.println(i);
            in.close();

            BufferedWriter out = new BufferedWriter(new FileWriter(filename + ".out"));

            List<Data> dataList = trans.translate();
            for(Data data : dataList) {
                System.out.println(data.time + "\t" + data.disk + "\t" + data.net + "\t" + data.cpu);
                String entry = data.cpu + "\t" + data.disk + "\t" + data.net + "\n";
                out.write(entry);
            }

            out.close();
        } catch (IOException e) {
        }
    }
}