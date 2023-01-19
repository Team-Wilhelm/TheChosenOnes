package Budgetflix.BE;

public class Genre {
    private int id;
    private String name;

    public Genre(String name) {
        this.name = name;
    }

    public Genre(int id, String name) {
        this(name);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    //TODO a useful comment (with a bit of explanation) could be nice
    @Override
    public boolean equals(Object obj){
        if(obj == null)
            return false;
        try {
            Genre genre = (Genre) obj;
            if (genre.name.equals(this.name) && genre.id == this.id)
                return true;
            else
                return false;
        }catch (ClassCastException cce)
        {
            if (((String)obj).equals(this.name))
                return true;
            else
                return false;
        }
    }
}
