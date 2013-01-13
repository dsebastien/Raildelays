package be.raildelays.domain.railtime;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Train implements Serializable {

	private static final long serialVersionUID = 1433204594231206313L;

	@NotNull
	@Size(min = 1, max = 8)
	private String idRailtime;

	public Train(String idRailtime) {
		this.setIdRailtime(idRailtime);
	}

	public String getIdRailtime() {
		return idRailtime;
	}

	public void setIdRailtime(String idRailtime) {
		this.idRailtime = idRailtime;
	}

}
