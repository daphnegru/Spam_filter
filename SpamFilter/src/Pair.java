public class Pair {

    private final String first;
    private Integer second;

    public Pair(String first, Integer second){
        this.first = first;
        this.second = second;
    }

    //gets the first of the pair
    public String getFirst(){
        return this.first;
    }

    //gets the second of the pair
    public Integer getSecond(){
        return this.second;
    }

    //sets the second of the pair
    public void setSecond(int count){
        this.second = count;
    }
}
