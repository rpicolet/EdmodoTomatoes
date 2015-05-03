package rpicolet.edmodotomatoes.model;

import java.util.List;

public interface IMovieList extends List<Movie> {

	/**
	 * Listener callbacks
	 */
	public interface IOnLoadListener {
		public void onLoadSuccess();
		public void onLoadError();
	}

	/**
	 * Set the MovieListListener
	 * @param listener
	 */
	public void setListener(IOnLoadListener listener);

	/**
	 * Load some more movies if any are available
	 *
	 * @param onlyIfEmpty - if true, only loads if List is empty
	 */
	public void loadMovies(boolean onlyIfEmpty);

	/**
	 * @return - true if a Movie load request is in progress
	 */
	public boolean isLoading();

	/**
	 * @return -  true if all available Movies are loaded
	 */
	public boolean isFullyLoaded();

	/**
	 * @return - true if any Movies are loaded
	 */
	public boolean hasLoadedMovies();
}
