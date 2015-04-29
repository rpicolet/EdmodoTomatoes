package rpicolet.edmodotomatoes.control;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import rpicolet.edmodotomatoes.R;
import rpicolet.edmodotomatoes.model.Movie;
import rpicolet.edmodotomatoes.model.MovieList;

/**
 * The MovieList adapter
 */
public class MovieListAdapter extends ArrayAdapter<Movie> {

	private boolean mIsEmpty = true;
	private MovieListFragment mMovieListFragment;
	private ImageLoader mImageLoader;
	private LayoutInflater mInflater;
	private MovieList mMovieList;

	MovieListAdapter(Context context,
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
		mMovieList.setAdapter(this);
	}

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
		if(mMovieList.loadMovies(onlyIfEmpty))
			mMovieListFragment.onLoadRequest();
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		mMovieListFragment.onLoadResponse();
	}

	public void onLoadError() {
		AlertDialog.Builder b = new AlertDialog.Builder(getContext());
		b.setMessage("Movie List Load Error!");
		b.show();
	}
}
