package ee.mkv.estonian.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "REPRESENTATIONS")
public class Representation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    public Representation(String representation) {
        this.representation = representation;
    }

    private String representation;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRepresentation() {
        return representation;
    }

    public void setRepresentation(String representation) {
        this.representation = representation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Representation that = (Representation) o;
        return id == that.id &&
                representation.equals(that.representation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, representation);
    }
}
