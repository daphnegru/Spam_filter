import javax.mail.MessagingException;
import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws MessagingException, IOException {
        Filter filter = Filter.getInstance();
        filter.init(args[0]);
        HashMap<Integer,String> senders = new HashMap<>();
        getSenders(args[1], senders);
        HashMap<Integer, Pair> receivers = new HashMap<>();
        getReceivers(args[2], receivers);
        SendMail sender = SendMail.getInstance();
        sender.init(senders,receivers,args[3],args[4]);
        ReceiveMail receiver = ReceiveMail.getInstance();
        receiver.init(receivers);
        sender.sendMessages(10);
        for (Map.Entry<Integer, Pair> entry : receivers.entrySet()){
            receiver.getMessages(entry.getKey(), entry.getValue().getFirst());
        }
        createReport(receivers);
    }

    //this function gets the list of senders from a given file
    public static void getSenders(String filePath, HashMap<Integer,String> senders){
        BufferedReader buffer;
        int index = 0;
        try {
            buffer = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = buffer.readLine()) != null){
                senders.put(index, line);
                index++;
            }
        }
        catch (Exception e) {
            System.out.println("file not found");
        }
    }

    //this function gets the list of receivers from a given file
    public static void getReceivers(String filePath, HashMap<Integer, Pair> receivers){
        BufferedReader buffer;
        int index = 0;
        try {
            buffer = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = buffer.readLine()) != null){
                Pair p = new Pair(line,0);
                receivers.put(index, p);
                index++;
            }
        }
        catch (Exception e) {
            System.out.println("file not found");
        }
    }

    //this function creates the report for the admin user
    public static void createReport(HashMap<Integer, Pair> receivers) {
        File report = new File("spamReport.txt");

        try {
            FileWriter output = new FileWriter(report);
            output.write("Admin's spam report:\n");
            for (Map.Entry<Integer, Pair> entry: receivers.entrySet()){
                Pair p = entry.getValue();
                String toAdd = "user " + p.getFirst() + " got " + p.getSecond() + " spam mails\n";
                output.write(toAdd);
            }
            output.close();
        }
        catch (Exception e) {
            e.getStackTrace();
        }
    }
}
