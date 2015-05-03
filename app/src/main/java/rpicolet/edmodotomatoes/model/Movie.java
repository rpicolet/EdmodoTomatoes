package rpicolet.edmodotomatoes.model;

import org.json.JSONObject;

public class Movie {

	public static final int UNSPECIFIED = -1;

	public class Ratings {
		private String mCriticsRating = null;
		private int mCriticsScore = UNSPECIFIED;
		private String mAudienceRating = null;
		private int mAudienceScore = UNSPECIFIED;

		Ratings(JSONObject jsonRatings) {
			if (jsonRatings != null) {
				mCriticsRating = jsonRatings.optString("critics_rating");
				mCriticsScore = jsonRatings.optInt("critics_score");
				mAudienceRating = jsonRatings.optString("audience_rating");
				mAudienceScore = jsonRatings.optInt("audience_score");
			}
		}

		public String getCriticsRating() {return mCriticsRating;}
		public int getCriticsScore() {return mCriticsScore;}
		public String getAudienceRating() {return mAudienceRating;}
		public int getAudienceScore() {return mAudienceScore;}
	}

	public class Posters {
		private String mThumbnailUrl = null;
		private String mProfileUrl = null;
		private String mDetailedUrl = null;
		private String mOriginalUrl = null;

		Posters(JSONObject jsonPosters) {
			if (jsonPosters != null) {
				mThumbnailUrl = jsonPosters.optString("thumbnail");
				mProfileUrl = jsonPosters.optString("profile");
				mDetailedUrl = jsonPosters.optString("detailed");
				mOriginalUrl = jsonPosters.optString("original");
			}
		}

		public String getThumbnailUrl() {return mThumbnailUrl;}
		public String getProfileUrl() {return mProfileUrl;}
		public String getDetailedUrl() {return mDetailedUrl;}
		public String getOriginalUrl() {return mOriginalUrl;}
	}

	private String mId = null;
	private String mTitle = null;
	private String mMpaaRating = null;
	private int mRuntime = -1;
	private String mCriticsConsensus = null;
	private Ratings mRatings = null;
	private String mSynopsis = null;
	private Posters mPosters = null;

	Movie(JSONObject jsonMovie) {
		mId = jsonMovie.optString("id");
		mTitle = jsonMovie.optString("title");
		mMpaaRating = jsonMovie.optString("mpaa_rating");
		mRuntime = jsonMovie.optInt("runtime");
		mCriticsConsensus = jsonMovie.optString("critics_consensus");
		mRatings = new Ratings(jsonMovie.optJSONObject("ratings"));
		mSynopsis = jsonMovie.optString("synopsis");
		mPosters = new Posters (jsonMovie.optJSONObject("posters"));
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
