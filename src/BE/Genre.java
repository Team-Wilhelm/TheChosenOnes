package BE;

public class Genre {
    int id;
    String name;

    public Genre(String name){
        this.name = name;
    }

    public Genre(int id, String name){
        this(name);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
