public class Group {
    
    private String name; 
    
    public Group(String name) {
        this.name = name;
    }

    public String getName() {return name;}


    @Override
    public String toString() {return getName();}
    
}