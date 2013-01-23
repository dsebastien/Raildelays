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

import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * Entity that represent a train. Unicity of a train is done on the English
 * name.
 * 
 * @author Almex
 * @see Entity
 */
@Entity
@Table(name = "TRAIN")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Train implements Serializable {

	private static final long serialVersionUID = -1527666012499664304L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	protected Long id;

	@Column(nullable = false, updatable = false, unique = true)
	protected String englishName;

	protected String frenchName;

	protected String dutchName;

	public Train() {
		this.id = null;
		this.englishName = "";
		this.dutchName = "";
		this.frenchName = "";
	}

	public Train(String name) {
		this.englishName = name;
	}

	public Long getId() {
		return id;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public String getFrenchName() {
		return frenchName;
	}

	public void setFrenchName(String frenchName) {
		this.frenchName = frenchName;
	}

	public String getDutchName() {
		return dutchName;
	}

	public void setDutchName(String dutchName) {
		this.dutchName = dutchName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((englishName == null) ? 0 : englishName.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		boolean result = false;

		if (obj == this) {
			result = true;
		} else {
			if (obj instanceof Train) {
				Train train = (Train) obj;

				result = new EqualsBuilder().append(englishName,
						train.getEnglishName()).isEquals();
			} else {
				result = false;
			}
		}

		return result;
	}

	@Override
	public String toString() {
		return new StringBuilder("Train: ") //
				.append("{ ") //
				.append("id: ").append(id).append(", ") //
				.append("dutchName: ").append(dutchName).append(", ") //
				.append("englishName: ").append(englishName).append(", ") //
				.append("frenchName: ").append(frenchName) //
				.append(" }").toString();
	}
}
