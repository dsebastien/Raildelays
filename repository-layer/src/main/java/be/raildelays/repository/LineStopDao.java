package be.raildelays.repository;

import java.util.Date;
import java.util.List;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.railtime.Stop;

/**
 * Repository that manage storing a {@link Stop}.
 * 
 * @author Almex
 */
public interface LineStopDao {

	/**
	 * Persist a new LineStop to the repository.
	 * 
	 * @param lineStop
	 * @return the same entity with its id filled-in
	 */
	public LineStop createLineStop(LineStop lineStop);

	/**
	 * Search a list of line stops that belong to a direction for a certain day.
	 * 
	 * @param departure
	 *            station where you are coming from
	 * @param arrival
	 *            station where you are going to
	 * @param date
	 *            day of the year for which you do the search
	 * @return a list of line stop
	 */
	public List<LineStop> retrieveLineStop(Station departure, Station arrival,
			Date date);

	/**
	 * Search a list of line stops that belong to a train for a certain day.
	 * 
	 * @param idTrain
	 *            train's id in Railtime format.
	 * @param date
	 *            day of the year for which you do the search
	 * @return a list of line stop
	 */
	public List<LineStop> retrieveLineStop(String idTrain, Date date);

	/**
	 * Remove a line stop.
	 * 
	 * @param idLineStop
	 *            line stop id
	 */
	public void deleteLineStop(Long idLineStop);

	/**
	 * Update a line stop.
	 * 
	 * @param lineStop
	 *            line stop that should contain at least an id.
	 * @return the persisted version of the line stop after updating.
	 */
	public LineStop updateLineStop(LineStop lineStop);
}
