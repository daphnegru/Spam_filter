import com.sun.mail.pop3.POP3Store;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ReceiveMail {

    private Map<Integer, Pair> receivers;
//    private static MimeMessage email;
    private final Filter filter;
    private static final String password = "spamFilterPassword1!";
    private static final String hostVal = "pop.gmail.com";
    private POP3Store emailStore;

    public ReceiveMail(){
        receivers = new HashMap<>();
        filter = Filter.getInstance();

    }

    private static class SingletonHolder{
        private static final ReceiveMail receiver = new ReceiveMail();
    }

    public static ReceiveMail getInstance() {
        return SingletonHolder.receiver;
    }

    public void init(HashMap<Integer, Pair> receives) throws NoSuchProviderException {
        receivers = receives;
        Properties serverProperties = System.getProperties();
        serverProperties.put("mail.pop3.host", hostVal);
        serverProperties.put("mail.pop3.port", "995");
        serverProperties.put("mail.pop3.starttls.enable", "true");
        Session mailSession = Session.getDefaultInstance(serverProperties);
        emailStore = (POP3Store) mailSession.getStore("pop3s");
    }

    public void getMessages(int index, String receiver) throws MessagingException, IOException {
        Pair p = receivers.get(index);
        emailStore.connect(hostVal,receiver, password);
        Folder emailFolder = emailStore.getFolder("INBOX");
        emailFolder.open(Folder.READ_WRITE);
        Message[] messages = emailFolder.getMessages();
        for (Message message : messages) {
            if (filter.isSpam((MimeMessage) message)) {
                Flags spam = new Flags("SPAM");
                message.setFlags(spam, true);
                int count = p.getSecond() + 1;
                receivers.get(index).setSecond(count);
//                Message[] arr = new Message[1];
//                arr[0] = message;
                //flags the spam message
//                emailFolder.setFlags(arr,new Flags(Flags.Flag.FLAGGED), true);
            }
        }
        emailFolder.close(false);
    }
}
