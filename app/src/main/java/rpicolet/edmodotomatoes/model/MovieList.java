package rpicolet.edmodotomatoes.model;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import rpicolet.edmodotomatoes.MovieVolley;
import rpicolet.edmodotomatoes.control.MovieListAdapter;

public class MovieList extends ArrayList<Movie> implements List<Movie> {

	private static final int INITIAL_ALLOC = 200;
	private static final int PAGE_LIMIT = 20;
	private static final String RT_BASE_URL =
			"http://api.rottentomatoes.com/api/public/v1.0/lists/movies/in_theaters.json";
	private static final String RT_API_KEY =
			"44tst5e3kxqk7wtr98j5qwmv";

	private static final String TAG = MovieList.class.getSimpleName();

	private static MovieList mInstance = null;

	private boolean mLoadInProgress = false;
	private int mTotalMovies = 0;
	private RequestQueue mRequestQueue;
	private Response.Listener<JSONObject> mSuccessListener;
	private Response.ErrorListener mErrorListener;
	private MovieListAdapter mMovieListAdapter;

	public static MovieList getInstance() {
		if (mInstance == null)
			mInstance = new MovieList();
		return mInstance;
	}

	private MovieList() {
		super(INITIAL_ALLOC);
		mRequestQueue = MovieVolley.getRequestQueue();
	}

	public void setAdapter(MovieListAdapter adapter) {
		if (mMovieListAdapter != null)
			// Returning from MovieDetailActivity...
			return;
		mMovieListAdapter = adapter;
		mSuccessListener = createSuccessListener();
		mErrorListener = createErrorListener();
	}

	/**
	 *
	 * @param onlyIfEmpty - if true, only loads if List is empty
	 * @return - true if load Request generated
	 */
	public boolean loadMovies(boolean onlyIfEmpty) {
		if (mMovieListAdapter == null)
			throw new IllegalStateException("MovieListAdapter not set");

		if ((onlyIfEmpty && hasLoadedMovies()) ||
				isFullyLoaded() || mLoadInProgress)
			return false;

		Log.d(TAG, "load Request...");
		mLoadInProgress = true;
		int page = 1 + (size() / PAGE_LIMIT);
		JsonObjectRequest jsonObjReq = new JsonObjectRequest(
				Request.Method.GET,
				RT_BASE_URL + "?" +
						"&page_limit=" + PAGE_LIMIT +
						"&page="       + page +
						"&country="    + "us" +
						"&apikey="     + RT_API_KEY,
				null,
				mSuccessListener,
				mErrorListener);

		mRequestQueue.add(jsonObjReq);
		return true;
	}

	public boolean hasLoadedMovies() {
		return size() > 0;
	}

	public boolean isFullyLoaded() {
		return hasLoadedMovies() && mTotalMovies <= size();
	}

	private Response.Listener<JSONObject> createSuccessListener() {
		return new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					Log.d(TAG, "load Response...");
					mTotalMovies = response.getInt("total");
					JSONArray jsonMovies = response.getJSONArray("movies");
					int length = jsonMovies.length();
					for (int i = 0; i < length; i++)
						add(new Movie(jsonMovies.getJSONObject(i)));
					mMovieListAdapter.notifyDataSetChanged();
					mLoadInProgress = false;
				} catch (JSONException e) {
					mMovieListAdapter.onLoadError();
				}
			}
		};
	}

	private Response.ErrorListener createErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				mMovieListAdapter.onLoadError();
			}
		};
	}
}
