package ee.mkv.estonian.domain;

import com.vladmihalcea.hibernate.type.json.JsonType;
import ee.mkv.estonian.model.InternalPartOfSpeech;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "EKILEX_WORDS")
@Slf4j
public class EkilexWord {

    @Id
    @NaturalId
    Long id;

    @ManyToOne
    @JoinColumn(name = "word_representation_id")
    Representation baseForm;

    @Type(JsonType.class)
    @Column(name = "parts_of_speech", columnDefinition = "jsonb")
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    Set<Long> posArray = new HashSet<>();

    @Transient
    Set<InternalPartOfSpeech> partsOfSpeech = new HashSet<>();

    @PrePersist
    @PreUpdate
    private void convertToIntegerSet() {
        log.info("Converting partsOfSpeech {} to posArray", partsOfSpeech);
        if (partsOfSpeech != null) {
            this.posArray = partsOfSpeech.stream()
                    .map(InternalPartOfSpeech::getId)
                    .collect(Collectors.toSet());
        } else {
            throw new UnsupportedOperationException("partsOfSpeech is null when saving EkilexWord: " + this);
        }
    }

    // Convert Set<Integer> back to Set<PartOfSpeech> after loading
    @PostLoad
    private void convertToEnumSet() {
        if (posArray != null) {
            this.partsOfSpeech = posArray.stream()
                    .map(InternalPartOfSpeech::fromId)
                    .collect(Collectors.toSet());
        }
    }
}
