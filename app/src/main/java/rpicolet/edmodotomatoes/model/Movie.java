package rpicolet.edmodotomatoes.model;

import org.json.JSONObject;

public class Movie {

	public static final int UNSPECIFIED = -1;

	public class Ratings {
		String mCriticsRating = null;
		int mCriticsScore = UNSPECIFIED;
		String mAudienceRating = null;
		int mAudienceScore = UNSPECIFIED;

		Ratings() {}

		public String getCriticsRating() {return mCriticsRating;}
		public int getCriticsScore() {return mCriticsScore;}
		public String getAudienceRating() {return mAudienceRating;}
		public int getAudienceScore() {return mAudienceScore;}
	}

	public class Posters {
		String mThumbnailUrl = null;
		String mProfileUrl = null;
		String mDetailedUrl = null;
		String mOriginalUrl = null;

		Posters() {}

		public String getThumbnailUrl() {return mThumbnailUrl;}
		public String getProfileUrl() {return mProfileUrl;}
		public String getDetailedUrl() {return mDetailedUrl;}
		public String getOriginalUrl() {return mOriginalUrl;}
	}

	String mId = null;
	String mTitle = null;
	String[] mGenres = null;
	String mMpaaRating = null;
	int mRuntime = -1;
	String mCriticsConsensus = null;
	Ratings mRatings = new Ratings();
	String mSynopsis = null;
	Posters mPosters = new Posters();

	Movie(JSONObject jsonMovie) {
		mId = jsonMovie.optString("id");
		mTitle = jsonMovie.optString("title");
		mMpaaRating = jsonMovie.optString("mpaa_rating");
		mRuntime = jsonMovie.optInt("runtime");
		mCriticsConsensus = jsonMovie.optString("critics_consensus");
		JSONObject jsonRatings = jsonMovie.optJSONObject("ratings");
		if (jsonRatings != null) {
			mRatings.mCriticsRating = jsonRatings.optString("critics_rating");
			mRatings.mCriticsScore = jsonRatings.optInt("critics_score");
			mRatings.mAudienceRating = jsonRatings.optString("audience_rating");
			mRatings.mAudienceScore = jsonRatings.optInt("audience_score");
		}
		mSynopsis = jsonMovie.optString("synopsis");
		JSONObject jsonPosters = jsonMovie.optJSONObject("posters");
		if (jsonPosters != null) {
			mPosters.mThumbnailUrl = jsonPosters.optString("thumbnail");
			mPosters.mProfileUrl = jsonPosters.optString("profile");
			mPosters.mDetailedUrl = jsonPosters.optString("detailed");
			mPosters.mOriginalUrl = jsonPosters.optString("original");
		}
	}

	@Override
	public String toString() {
		return mTitle;
	}

	public String getId() {return mId;}
	public String getTitle() {return mTitle;}
	public String getMpaaRating() {return mMpaaRating;}
	public int getRuntime() {return mRuntime;}
	public String getCriticsConsensus() {return mCriticsConsensus;}
	public Ratings getRatings() {return mRatings;}
	public String getSynopsis() {return mSynopsis;}
	public Posters getPosters() {return mPosters;}
}
