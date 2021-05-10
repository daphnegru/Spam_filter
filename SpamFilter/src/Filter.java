import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.regex.*;

public class Filter {

    private final Map<Integer, Pattern> spamWords;

    private Filter(){
        spamWords = new HashMap<>();
    }

    //filter is a singleton
    private static class SingletonHolder{
        private static final Filter filter = new Filter();
    }

    public static Filter getInstance() {
        return SingletonHolder.filter;
    }

    //initiates the list of spam phrases, allows to add phrases later
    public void init(String filePath){
        BufferedReader buffer;
        int index = spamWords.size();
        try {
            buffer = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = buffer.readLine()) != null){
                spamWords.put(index, Pattern.compile(line, Pattern.CASE_INSENSITIVE));
                index++;
            }
        }
        catch (Exception e) {
            System.out.println("file not found");
        }
    }

    //gets the size of the list of phrases
    public int getSize(){
        return spamWords.size();
    }

    //gets the spam phrase with the index given
    public String getPhrase(int index) {
        if (index < 0 || index > spamWords.size()){
            return "";
        }
        return spamWords.get(index).toString();
    }

    //given an email message checks whether the subject line or the content has one of the spam words or phrases
    public boolean isSpam(MimeMessage message) throws MessagingException, IOException {
        String subject = message.getSubject();
        String content = message.getContent().toString();
        for(Map.Entry<Integer, Pattern> entry: spamWords.entrySet()){
            Pattern spamPhrase = entry.getValue();
            boolean inSubject = spamPhrase.matcher(subject).find();
            boolean inContent =  spamPhrase.matcher(content).find();
            if (inSubject || inContent){
                return true;
            }
        }
        return false;
    }
}
