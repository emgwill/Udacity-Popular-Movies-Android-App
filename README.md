# Udacity-Popular-Movies-Android-App

In order to run, get an API key from TheMovieDb and put it in the strings.xml file in the variable "api_key".

This app:

Presents the user with a grid arrangement of movie posters upon launch.
- Allows your user to change sort order via a setting:
      - The sort order can be by most popular, by highest-rated, or by favorites
- Allows the user to tap on a movie poster and transition to a details screen with additional information such as:
      - original title
      - movie poster image thumbnail
      - A plot synopsis (called overview in the api)
      - user rating (called vote_average in the api)
      - release date
      - trailers (which on click will create an intent to watch the video through youtube)
      - reviews
      - set movie as a favorite
      
Next steps:
      - This app is effective, but not very pretty. The next thing I would do would be to make the app look better.
      - There are a lot of commented out sections, log statements, and poorly names variables. I would want to clean them up.

Resources:
- Udacity Sunshine app lessons
- https://developer.android.com/guide/topics/ui/layout/gridview.html
- http://stackoverflow.com/questions/18953632/how-to-set-image-from-url-for-imageview
- http://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent

