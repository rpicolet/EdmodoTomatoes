package rpicolet.edmodotomatoes.control;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import rpicolet.edmodotomatoes.R;
import rpicolet.edmodotomatoes.control.MovieListFragment.OnSelectListener;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of Movies, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * Movie details. On tablets, the activity presents the list of Movies and
 * Movie details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of Movies is a
 * {@link MovieListFragment} and the Movie details
 * (if present) is a {@link MovieDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link rpicolet.edmodotomatoes.control.MovieListFragment.OnSelectListener} interface
 * to listen for Movie selections.
 */
public class MovieListActivity extends FragmentActivity
		implements OnSelectListener {

	private static final String TAG = MovieListActivity.class.getSimpleName();

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	public MovieListActivity() {
		super();
		Log.d(TAG, "ctor()");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate()");

		setContentView(R.layout.activity_movie_list);

		if (findViewById(R.id.movie_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, MovieList entries should be given the
			// 'selected' state when touched.
			((MovieListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.movie_list))
					.setSelectedOnItemClick(true);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart()");
	}
	@Override
	protected void onResume() {
		super.onStart();
		Log.d(TAG, "onResume()");
	}

	@Override
	protected void onPause() {
		super.onStart();
		Log.d(TAG, "onPause()");
	}

	@Override
	protected void onStop() {
		super.onStart();
		Log.d(TAG, "onStop()");
	}

	@Override
	protected void onDestroy() {
		super.onStart();
		Log.d(TAG, "onDestroy()");
	}

	/**
	 * Callback method from {@link rpicolet.edmodotomatoes.control.MovieListFragment.OnSelectListener}
	 * indicating that the Movie at the given list position was selected.
	 */
	@Override
	public void onMovieSelected(int movieListPosition) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putInt(MovieDetailFragment.ARG_MOVIE_LIST_POSITION,
					movieListPosition);
			MovieDetailFragment fragment = new MovieDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.movie_detail_container, fragment)
					.commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, MovieDetailActivity.class);
			detailIntent.putExtra(MovieDetailFragment.ARG_MOVIE_LIST_POSITION,
					movieListPosition);
			startActivity(detailIntent);
		}
	}
}
