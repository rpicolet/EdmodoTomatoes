package rpicolet.edmodotomatoes.control;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import rpicolet.edmodotomatoes.MovieVolley;
import rpicolet.edmodotomatoes.R;
import rpicolet.edmodotomatoes.model.Movie;
import rpicolet.edmodotomatoes.model.MovieList;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {

	public static final String ARG_MOVIE_LIST_POSITION = "movie_list_position";

	private ImageLoader mImageLoader;
	private Movie mMovie = null;

	/**
	 * Empty constructor required for FragmentManager
	 */
	public MovieDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		if (args != null && args.containsKey(ARG_MOVIE_LIST_POSITION)) {
			int position = args.getInt(ARG_MOVIE_LIST_POSITION);
			MovieList movieList = MovieList.getInstance();
			if (position >= 0 && position < movieList.size())
				mMovie = movieList.get(position);
		}

		mImageLoader = MovieVolley.getImageLoader();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);

		// Show the movie content
		if (mMovie != null) {
			// Load the text views
			String text;
			int number;
			text = mMovie.getTitle();
			((TextView)view.findViewById(R.id.title)).setText(text);
			Movie.Ratings ratings = mMovie.getRatings();
			number = ratings.getCriticsScore();
			text = number == Movie.UNSPECIFIED ? "" : Integer.toString(number);
			((TextView)view.findViewById(R.id.critics_ratings)).setText(text);
			number = ratings.getAudienceScore();
			text = number == Movie.UNSPECIFIED ? "" : Integer.toString(number);
			((TextView)view.findViewById(R.id.audience_ratings)).setText(text);
			text = mMovie.getMpaaRating();
			((TextView)view.findViewById(R.id.mpaa_rating)).setText(text);
			number = mMovie.getRuntime();
			text = Integer.toString(number);
			((TextView)view.findViewById(R.id.runtime)).setText(text);
			text = mMovie.getSynopsis();
			((TextView)view.findViewById(R.id.synopsis)).setText(text);

			// Load the detailed image view

			NetworkImageView detailsView =
					(NetworkImageView) view.findViewById(R.id.details);
			String detailedUrl = mMovie.getPosters().getDetailedUrl();
			detailsView.setImageUrl(detailedUrl, mImageLoader);


			//((TextView) rootView.findViewById(R.id.movie_detail)).setText(mMovie.content);
		}

		return view;
	}
}
