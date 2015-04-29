package rpicolet.edmodotomatoes.control;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import rpicolet.edmodotomatoes.MovieVolley;

/**
 * A ListFragment for a list of Movies. This fragment supports tablet devices
 * by allowing a listed movie to be marked as 'selected', indicating it is
 * currently being viewed in a {@link MovieDetailFragment}.
 * <p/>
 * Activities containing this fragment must implement the {@link Callbacks}
 * interface.
 * <p/>
 * Credit: Paging/scrolling logic heavily cannibalized from:
 *      com.github.volley_examples.app.Act_NetworkListView
 * by Ognyan Bankov - thanx!
 */
public class MovieListFragment extends ListFragment {

	public interface Callbacks {
		public void onMovieSelected(int movieListPosition);
	}

	public class EndlessScrollListener implements AbsListView.OnScrollListener {
		// how many entries earlier to start loading next page
		private static final int DEFAULT_THRESHHOLD = 5;

		private int mVisibleThreshold;

		public EndlessScrollListener() {
			this(DEFAULT_THRESHHOLD);
		}
		public EndlessScrollListener(int visibleThreshold) {
			this.mVisibleThreshold = visibleThreshold;
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
		                     int visibleItemCount, int totalItemCount) {
			Log.d(TAG, "onScroll()");
			if (!isLoading() &&	totalItemCount <=
					firstVisibleItem + visibleItemCount + mVisibleThreshold)
				mMovieListAdapter.loadMovies(false);
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

		}
	}

	private static final String TAG = MovieListFragment.class.getSimpleName();

	private static final String STATE_SELECTED_POSITION = "selected_position";
	private static final Callbacks DETACHED_CALLBACKS = new Callbacks() {
		@Override
		public void onMovieSelected(int movieListPosition) {
			// Presumed late, ignore and swallow
		}
	};

	private static int sSelectedPosition = ListView.INVALID_POSITION;

	private boolean mIsLoading;
	private Callbacks mCallbacks = DETACHED_CALLBACKS;
	private MovieListAdapter mMovieListAdapter;

	/**
	 * Empty constructor required for FragmentManager
	 */
	public MovieListFragment() {
	}

	// Lifecycle methods

	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "onAttach()");
		super.onAttach(activity);

		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException("Activity must implement Callbacks.");
		}
		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		mMovieListAdapter = new MovieListAdapter(getActivity(), this,
				MovieVolley.getImageLoader());
		setListAdapter(mMovieListAdapter);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Log.d(TAG, "onViewCreated()");
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized selected Movie position.
		if (savedInstanceState != null &&
				savedInstanceState.containsKey(STATE_SELECTED_POSITION))
			setSelectedPosition(savedInstanceState
					.getInt(STATE_SELECTED_POSITION));
		else if (sSelectedPosition != ListView.INVALID_POSITION)
			setSelectedPosition(sSelectedPosition);

		getListView().setOnScrollListener(new EndlessScrollListener());
	}

	@Override
	public void onStart() {
		Log.d(TAG, "onStart()");
		super.onStart();
	}

	@Override
	public void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();
		mMovieListAdapter.loadMovies(true);
	}

	@Override
	public void onPause() {
		Log.d(TAG, "onPause()");
		super.onPause();
	}

	@Override
	public void onStop() {
		Log.d(TAG, "onStop()");
		super.onStop();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.d(TAG, "onSaveInstanceState()");
		super.onSaveInstanceState(outState);
		if (sSelectedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the selected Movie position.
			outState.putInt(STATE_SELECTED_POSITION, sSelectedPosition);
		}
	}

	@Override
	public void onDetach() {
		Log.d(TAG, "onDetach()");
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = DETACHED_CALLBACKS;
	}

	// ListFragment methods

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that a Movie has been selected.
		mCallbacks.onMovieSelected(position);

		// Update the MovieList selected position
		setSelectedPosition(position);
	}

	// ListView methods
	/**
	 * Turns on selection indication-on-click mode. When this mode is on, list items will be
	 * given the 'selected' state when touched.
	 */
	public void setSelectedOnItemClick(boolean showSelectedOnClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give the MovieList entry the 'selected' state when touched.
		getListView().setChoiceMode(showSelectedOnClick
				? ListView.CHOICE_MODE_SINGLE
				: ListView.CHOICE_MODE_NONE);
	}

	// MovieListAdapter callbacks
	void onLoadRequest() {
		mIsLoading = true;
	}
	void onLoadResponse() {
		mIsLoading = false;
	}

	// For EndlessScrollListener
	boolean isLoading() {
		return mIsLoading;
	}

	// Helper/utility methods

	private void setSelectedPosition(int position) {
		ListView listView = getListView();
		// Deselect any current selection
		if (sSelectedPosition != ListView.INVALID_POSITION)
			listView.setItemChecked(sSelectedPosition, false);
		if (position != ListView.INVALID_POSITION) {
			// Apply new selected position
			listView.setItemChecked(position, true);
			listView.setSelection(position);
		}
		sSelectedPosition = position;
	}
}
