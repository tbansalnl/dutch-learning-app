package org.tbansal.dutch.learning.app.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Objects;

@Entity
@Data
public class DutchWords {

    @Id
    @GeneratedValue
    private Long id;
    private String dutchWord;
    private String englishTranslation;
    private String example;
    private int failedAttempts;
    private String type;
    @Transient
    private String answer;
    @Transient
    private boolean matching;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DutchWords that = (DutchWords) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DutchWords{" +
                "id=" + id +
                ", dutchWord='" + dutchWord + '\'' +
                ", englishTranslation='" + englishTranslation + '\'' +
                ", example='" + example + '\'' +
                ", failedAttempts=" + failedAttempts +
                ", type='" + type + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }

}