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

    /**
     * Overriding equals is to make work the method: containsAll(genres)
     * We implement it in filtering when checking if the Movie.containsAll(List<Genres>)
     * containsAll by default compares the objects next to each other and because they can have the same values but not the same object, we need to check it manually.
     * @param obj
     * @return true or false based on the id of the genre.
     */
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
