package be.raildelays.domain.railtime;

import java.io.Serializable;
import java.util.Date;

public class Step extends LineStop implements Serializable  {

	private static final long serialVersionUID = -3386080893909407089L;

	private Long delay;
	
	private boolean canceled;
	
	public Step (String stationName, Date timestamp, Long delay, boolean canceled) {
		super(stationName, timestamp);
		this.setDelay(delay);
		this.canceled = canceled;		
	}

	public Long getDelay() {
		return delay;
	}

	public void setDelay(Long delay) {
		this.delay = delay;
	}

	public boolean isCanceled() {
		return canceled;
	}
	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}
}
