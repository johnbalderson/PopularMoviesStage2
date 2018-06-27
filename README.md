<b>Note to Reviewer</b><br><br>
Literal YOUR API KEY in gradle.properties must be changed to your individual themoviesdb.org API key .<br>
<br>
App will terminate if this key is not changed or if you are not online. This termination is done via a dialog box.<br>

<br>
<b>Project Summary</b>

Most of us can relate to kicking back on the couch and enjoying a movie with friends and family. <br><br>
In this project, you’ll build an app to allow users to discover the most popular movies playing.

Stage 1:  Main Discovery Screen, A Details View, and Settings<br><br>
<b>User Experience</b><br>
In this stage you’ll build the core experience of your movies app.

Your app will:

* Upon launch, present the user with an grid arrangement of movie posters.
* Allow your user to change sort order via a setting:<br>
  * The sort order can be by most popular, or by top rated
* Allow the user to tap on a movie poster and transition to a details screen with additional information such as:<br>
* original title
* movie poster image thumbnail
* A plot synopsis (called overview in the api)
* user rating (called vote_average in the api)
* release date

Stage 2: Trailers, Reviews, and Favorites<br><br>
<b>User Experience</b><br>
In this stage you’ll add additional functionality to the app you built in Stage 1.

You’ll add more information to your movie details view:

* Allow users to view and play trailers ( either in the youtube app or a web browser).
* Allow users to read reviews of a selected movie.
* Allow users to mark a movie as a favorite in the details view by tapping a button(star). This is for a local movies collection that you will maintain and does not require an API request.
* Modify the existing sorting criteria for the main view to include an additional pivot to show their favorites collection.

