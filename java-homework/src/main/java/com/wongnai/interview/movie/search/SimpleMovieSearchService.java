package com.wongnai.interview.movie.search;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wongnai.interview.movie.Movie;
import com.wongnai.interview.movie.MovieSearchService;
import com.wongnai.interview.movie.external.MovieDataService;
import com.wongnai.interview.movie.external.MoviesResponse;

@Component("simpleMovieSearchService")
public class SimpleMovieSearchService implements MovieSearchService {
	@Autowired
	private MovieDataService movieDataService;

	@Override
	public List<Movie> search(String queryText) {
		//TODO: Step 2 => Implement this method by using data from MovieDataService
		// All test in SimpleMovieSearchServiceIntegrationTest must pass.
		// Please do not change @Component annotation on this class
		MoviesResponse movieResp = movieDataService.fetchAll();
		List<Movie> movieList = new ArrayList<Movie>();
		Movie movie;
		for (int i = 0; i < movieResp.size(); i++) {
			if (movieResp.get(i).getTitle().matches(".*\\b" + queryText + "\\b.*")) {
				movie = new Movie(movieResp.get(i).getTitle());
				movie.setId(Long.valueOf(i));
				movie.setName(movieResp.get(i).getTitle());
				movie.getActors().addAll(movieResp.get(i).getCast());
				movieList.add(movie);
			}
		}
		return movieList;
	}
}
