package rpicolet.edmodotomatoes.control;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import rpicolet.edmodotomatoes.MovieVolley;
import rpicolet.edmodotomatoes.R;
import rpicolet.edmodotomatoes.model.IMovieList;
import rpicolet.edmodotomatoes.model.Movie;
import rpicolet.edmodotomatoes.model.MovieList;

//import rpicolet.edmodotomatoes.model.Movie;

//import rpicolet.edmodotomatoes.model.Movie;

/**
 * A ListFragment for a list of Movies. This fragment supports tablet devices
 * by allowing a listed movie to be marked as 'selected', indicating it is
 * currently being viewed in a {@link MovieDetailFragment}.
 * <p/>
 * Activities containing this fragment must implement:
 * {@link rpicolet.edmodotomatoes.control.MovieListFragment.OnSelectListener}
 * <p/>
 * Credit: Paging/scrolling logic heavily cannibalized from:
 *      com.github.volley_examples.app.Act_NetworkListView
 * by Ognyan Bankov - thanx!
 */
public class MovieListFragment extends ListFragment {

	public interface OnSelectListener {
		public void onMovieSelected(int movieListPosition);
	}

	public class EndlessScrollListener implements AbsListView.OnScrollListener {
		// how many entries earlier to start loading next page
		private static final int DEFAULT_THRESHHOLD = 15;

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
	private static final OnSelectListener DETACHED_LISTENER =
			new OnSelectListener() {
		@Override
		public void onMovieSelected(int movieListPosition) {
			// Presumed late, ignore and swallow
		}
	};

	private static int sSelectedPosition = ListView.INVALID_POSITION;

	private OnSelectListener mOnSelectListener = DETACHED_LISTENER;
	private OnLoadAdapter mMovieListAdapter;

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

		if (!(activity instanceof OnSelectListener)) {
			throw new IllegalStateException("Activity must implement Callbacks.");
		}
		mOnSelectListener = (OnSelectListener) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		mMovieListAdapter = new OnLoadAdapter(getActivity(), this,
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
		// Enable callbacks
		getListView().setOnScrollListener(new EndlessScrollListener());
	}

	@Override
	public void onPause() {
		Log.d(TAG, "onPause()");
		super.onPause();
		getListView().setOnScrollListener(null);
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
		mOnSelectListener = DETACHED_LISTENER;
	}

	// ListFragment methods

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		// Update the MovieList selected position
		setSelectedPosition(position);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that a Movie has been selected.
		mOnSelectListener.onMovieSelected(position);
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

	// For EndlessScrollListener
	boolean isLoading() {
		return mMovieListAdapter.isLoading();
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

	/**
	 * The MovieList adapter - mediates between MovieList model and this
	 * ListFragment's built-in ListView
	 */
	public static class OnLoadAdapter
			extends ArrayAdapter<Movie>
			implements IMovieList.IOnLoadListener {

		private boolean mIsEmpty = true;
		private MovieListFragment mMovieListFragment;
		private ImageLoader mImageLoader;
		private LayoutInflater mInflater;
		private MovieList mMovieList;

		OnLoadAdapter(Context context,
		              MovieListFragment fragment,
		              ImageLoader imageLoader) {
			super(context,
					R.layout.movie_list_item,
					R.id.title,
					MovieList.getInstance());
			mMovieListFragment = fragment;
			mImageLoader = imageLoader;
			mInflater = (LayoutInflater)context.getSystemService
					(Context.LAYOUT_INFLATER_SERVICE);
			mMovieList = MovieList.getInstance();
			mMovieList.setListener(this);
		}

		// Required adapter method implementation
		@Override
		public View getView(int position, View reuseView, ViewGroup parent) {

			Movie movie = getItem(position);
			if (movie == null)
				return null;

			View view;
			NetworkImageView thumbnailView;

			if (reuseView == null) {
				view = mInflater.inflate(R.layout.movie_list_item, null);
				thumbnailView = (NetworkImageView) view.findViewById(R.id.thumbnail);
				thumbnailView.setDefaultImageResId(R.drawable.no_image);
				thumbnailView.setErrorImageResId(R.drawable.error_image);
				// Use tag to avoid future findViewByIds...
				view.setTag(R.id.thumbnail_view_id, thumbnailView);
			}
			else
				view = reuseView;

			// Load the text views
			String text;
			int number;
			text = movie.getTitle();
			((TextView)view.findViewById(R.id.title)).setText(text);
			text = movie.getMpaaRating();
			((TextView)view.findViewById(R.id.mpaa_rating)).setText(text);
			Movie.Ratings ratings = movie.getRatings();
			number = ratings.getCriticsScore();
			text = number == Movie.UNSPECIFIED ? "" : Integer.toString(number);
			((TextView)view.findViewById(R.id.critics_score)).setText(text);
			number = ratings.getAudienceScore();
			text = number == Movie.UNSPECIFIED ? "" : Integer.toString(number);
			((TextView)view.findViewById(R.id.audience_score)).setText(text);

			// Load the thumbnail view
			thumbnailView = (NetworkImageView) view.getTag(R.id.thumbnail_view_id);
			String thumbnailUrl = movie.getPosters().getThumbnailUrl();
			thumbnailView.setImageUrl(thumbnailUrl, mImageLoader);

			return view;
		}

		public void loadMovies(boolean onlyIfEmpty) {
			mMovieList.loadMovies(onlyIfEmpty);
		}

		public boolean isLoading() {
			return mMovieList.isLoading();
		}

		// IMovieListListener methods

		@Override
		public void onLoadSuccess() {
			notifyDataSetChanged();
		}

		@Override
		public void onLoadError() {
			AlertDialog.Builder b = new AlertDialog.Builder(getContext());
			b.setMessage("Movie List Load Error!");
			b.show();
		}
	}
}
