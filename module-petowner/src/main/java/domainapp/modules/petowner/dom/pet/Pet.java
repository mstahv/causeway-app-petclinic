package domainapp.modules.petowner.dom.pet;

import java.util.Comparator;

import jakarta.inject.Named;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.causeway.applib.annotation.DomainObject;
import org.apache.causeway.applib.annotation.DomainObjectLayout;
import org.apache.causeway.applib.annotation.ObjectSupport;
import org.apache.causeway.applib.annotation.Property;
import org.apache.causeway.applib.annotation.PropertyLayout;
import org.apache.causeway.applib.annotation.Publishing;
import org.apache.causeway.applib.annotation.Title;
import org.apache.causeway.applib.jaxb.PersistentEntityAdapter;
import org.apache.causeway.persistence.jpa.applib.integration.CausewayEntityListener;

import domainapp.modules.petowner.types.Notes;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import domainapp.modules.petowner.PetOwnerModule;
import domainapp.modules.petowner.dom.petowner.PetOwner;
import domainapp.modules.petowner.types.PetName;

@Entity
@Table(
        schema= PetOwnerModule.SCHEMA,
        uniqueConstraints = {
                @UniqueConstraint(name = "Pet__owner_name__UNQ", columnNames = {"owner_id, name"})
        }
)
@EntityListeners(CausewayEntityListener.class)
@Named(PetOwnerModule.NAMESPACE + ".Pet")
@DomainObject(entityChangePublishing = Publishing.ENABLED)
@DomainObjectLayout()
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
@ToString(onlyExplicitlyIncluded = true)
public class Pet implements Comparable<Pet> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    @Getter @Setter
    private Long id;

    @Version
    @Column(name = "version", nullable = false)
    @PropertyLayout(fieldSetId = "metadata", sequence = "999")
    @Getter @Setter
    private long version;


    Pet(PetOwner petOwner, String name) {
        this.petOwner = petOwner;
        this.name = name;
    }

    @ObjectSupport
    public String iconName() {
        return getSpecies().name().toLowerCase();
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id")
    @PropertyLayout(fieldSetId = "identity", sequence = "1")
    @Getter @Setter
    private PetOwner petOwner;

    @PetName
    @Title
    @Column(name = "name", length = PetName.MAX_LEN, nullable = false)
    @Getter @Setter
    @PropertyLayout(fieldSetId = "identity", sequence = "2")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Getter @Setter
    @PropertyLayout(fieldSetId = "details", sequence = "1")
    private PetSpecies species;

    @Notes
    @Column(length = Notes.MAX_LEN, nullable = true)
    @Getter @Setter
    @Property(commandPublishing = Publishing.ENABLED, executionPublishing = Publishing.ENABLED)
    @PropertyLayout(fieldSetId = "details", sequence = "2")
    private String notes;

    private final static Comparator<Pet> comparator =
            Comparator.comparing(Pet::getPetOwner).thenComparing(Pet::getName);

    @Override
    public int compareTo(final Pet other) {
        return comparator.compare(this, other);
    }

}
