package be.raildelays.domain.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import be.raildelays.domain.Language;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Immutable entity defining a train station.
 * 
 * Unicity of a train is done on the English name.
 * 
 * @author Almex
 * @see Entity
 */
@Entity
@Table(name = "STATION")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Station extends AbstractI18nEntity implements Serializable, Cloneable, Comparable<Station> {

	private static final long serialVersionUID = -3436298381031779337L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected final Long id;

	@Column(name = "ENGLISH_NAME", updatable = false, unique = true)
	@NotNull
	protected final String englishName;

	@Column(name = "FRENCH_NAME")
	protected final String frenchName;

	@Column(name = "DUTCH_NAME")
	protected final String dutchName;

	/**
	 * Default contrcutor.
	 */
	protected Station() {
		this.id = null;
		this.englishName = "";
		this.dutchName = "";
		this.frenchName = "";
	}

	/**
	 * Initialization constructor.
	 * 
	 * @param englishName English name for this train station.
	 */
	public Station(final String englishName) {
		this.id = null;
		this.englishName = englishName;
		this.dutchName = "";
		this.frenchName = "";
	}

    /**
     * Initialization constructor.
     *
     * @param name for this train station.
     */
    public Station(final String name, Language language) {
        this.id = null;
        switch (language) {
            case EN:
                this.englishName = name;
                this.dutchName = "";
                this.frenchName = "";
                break;
            case NL:
                this.englishName = "";
                this.dutchName = name;
                this.frenchName = "";
                break;
            case FR:
                this.englishName = "";
                this.dutchName = "";
                this.frenchName = name;
                break;
            default:
                this.englishName = "";
                this.dutchName = "";
                this.frenchName = "";
        }
    }
	
	/**
	 * Initialization constructor.
	 * 
	 * @param englishName English name for this train
	 * @param dutchName Dutch name for this train
	 * @param frenchName French name for this train
	 */
	public Station(final String englishName, final String dutchName, final String frenchName) {
		this.id = null;
		this.englishName = englishName;
		this.dutchName = dutchName;
		this.frenchName = frenchName;
	}

	@Override
	public String toString() {
		return new StringBuilder("Station: ") //
				.append("{ ") //
				.append("id: ").append(id).append(", ") //
				.append("dutchName: ").append(dutchName).append(", ") //
				.append("englishName: ").append(englishName).append(", ") //
				.append("frenchName: ").append(frenchName) //
				.append(" }").toString();
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;

		if (obj == this) {
			result = true;
		} else {
			if (obj instanceof Station) {
				Station station = (Station) obj;

				result = new EqualsBuilder().append(englishName,
						station.englishName).isEquals();
			} else {
				result = false;
			}
		}

		return result;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder() //
				.append(englishName) //
				.toHashCode();
	}

	public Long getId() {
		return id;
	}

	public String getEnglishName() {
		return englishName;
	}

	public String getFrenchName() {
		return frenchName;
	}

	public String getDutchName() {
		return dutchName;
	}

    @Override
    public int compareTo(Station station) {
        int result = 0;

        if (station == null) {
            result = -1;
        } else {
            result = new CompareToBuilder()
                    .append(StringUtils.stripAccents(englishName), StringUtils.stripAccents(station.getEnglishName()), String.CASE_INSENSITIVE_ORDER)
                    .toComparison();
        }

        return result;
    }
}
