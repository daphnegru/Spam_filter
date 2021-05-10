public class Pair {

    private final String first;
    private Integer second;

    public Pair(String first, Integer second){
        this.first = first;
        this.second = second;
    }

    public String getFirst(){
        return this.first;
    }

    public Integer getSecond(){
        return this.second;
    }

    public void setSecond(int count){
        this.second = count;
    }

}
