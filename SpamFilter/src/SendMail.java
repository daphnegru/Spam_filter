import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.*;


public class SendMail {
    private Map<Integer,String> senders;
    private Map<Integer, Pair> receivers;
    private static Session mailSession;
    private final Filter filter;
    private static final String password = "spamFilterPassword1!";
    private final StringBuilder subject;
    private final StringBuilder text;

    private SendMail(){
        senders = new HashMap<>();
        receivers = new HashMap<>();
        filter = Filter.getInstance();
        subject = new StringBuilder();
        text = new StringBuilder();
    }

    private static class SingletonHolder{
        private static final SendMail sender = new SendMail();
    }

    public static SendMail getInstance() {
        return SingletonHolder.sender;
    }

    public void init(HashMap<Integer,String> sends, HashMap<Integer, Pair> receives, String subjectFile, String textFile) throws FileNotFoundException {
        senders = sends;
        receivers = receives;
        Properties serverProperties;
        serverProperties= System.getProperties();
        serverProperties.put("mail.smtp.port", "587");
        serverProperties.put("mail.smtp.auth", "true");
        serverProperties.put("mail.smtp.starttls.enable", "true");
        mailSession = Session.getDefaultInstance(serverProperties);
        mailSession.getProperties().put("mail.smtp.ssl.trust", "smtp.gmail.com");
        getSubject(subjectFile);
        getText(textFile);
    }

    public void getSubject(String filePath) throws FileNotFoundException {
        File subFile = new File(filePath);
        Scanner subSC = new Scanner(subFile);
        while (subSC.hasNextLine()) {
            subject.append(subSC.nextLine()).append(" ");
        }
        subject.deleteCharAt(subject.lastIndexOf(" "));
    }

    public void getText(String filePath) throws FileNotFoundException {
        File textFile = new File(filePath);
        Scanner textSC = new Scanner(textFile);
        while (textSC.hasNextLine()) {
            text.append(textSC.nextLine()).append(" ");
        }
        text.deleteCharAt(text.lastIndexOf(" "));
    }


    public void sendMessages(int numOfMessages) throws MessagingException {
        int addedAt = -1;
        for (int i = 0; i < numOfMessages; i++) {
            String sender = getSender();
            String receiver = getReceiver();
            int phrase = spamPhraseToAdd();
            int place;
            if (phrase < 0) {
                place = -1;
            } else {
                place = whereToAddPhrase();
            }
            if (place == 0){
                addedAt = text.length();
                text.append(" ").append(filter.getPhrase(phrase));
            }
            else if (place == 1){
                addedAt = subject.length();
                subject.append(" ").append(filter.getPhrase(phrase));
            }
            try {
                MimeMessage email = new MimeMessage(mailSession);
                email.setFrom(new InternetAddress(sender));
                email.addRecipient(Message.RecipientType.TO,new InternetAddress(receiver));
                email.setSubject(subject.toString());
                email.setText(text.toString());
                Transport transport = mailSession.getTransport("smtp");
                transport.connect("smtp.gmail.com", sender, password);
                transport.sendMessage(email, email.getAllRecipients());
                transport.close();
                if (place == 0){
                    text.delete(addedAt, text.length());
                }
                else if (place == 1){
                    subject.delete(addedAt, subject.length());
                }
            }
            catch (Exception e) {
                throw new MessagingException(e.getMessage());
            }
        }
    }

    //this function picks a sender at random from a list of senders
    public String getSender(){
        int max = senders.size();
        int index = (int)Math.floor(Math.random() * (max+1)) - 1;
        index = Math.max(index, 0);
        return senders.get(index);
    }

    //this function picks a receiver at random from a list of receivers
    public String getReceiver(){
        int max = receivers.size();
        int index = (int)Math.floor(Math.random() * (max+1)) - 1;
        index = Math.max(index, 0);
        return receivers.get(index).getFirst();
    }

    //this function decided whether to add a spam phrase and if so which one to add
    public int spamPhraseToAdd(){
        int size = filter.getSize();
        double ifToAdd = Math.random();
        //don't add spam phrase
        if (ifToAdd < 0.6){
            return -1;
        }
        //add a spam phrase with the index below
        int index = (int)Math.floor(Math.random() * (size+1)) - 1;
        return Math.max(index, 0);
    }

    //this function decides where to add the spam phrase chosen
    public int whereToAddPhrase(){
        double rand = Math.random();
        //add to the subject
        if (rand < 0.65){
            return 1;
        }
        //add to the text
        return 0;
    }
}
