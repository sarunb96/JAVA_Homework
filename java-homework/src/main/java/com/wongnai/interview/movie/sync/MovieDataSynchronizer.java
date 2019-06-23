package com.wongnai.interview.movie.sync;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wongnai.interview.movie.Movie;
import com.wongnai.interview.movie.MovieRepository;
import com.wongnai.interview.movie.external.MovieDataService;
import com.wongnai.interview.movie.external.MoviesResponse;

@Component
public class MovieDataSynchronizer {
	@Autowired
	private MovieDataService movieDataService;

	@Autowired
	private MovieRepository movieRepository;

	@Transactional
	public void forceSync() {
		//TODO: implement this to sync movie into repository
		MoviesResponse movieResp = movieDataService.fetchAll();
		Movie movie;
		for (int i = 0; i < movieResp.size(); i++) {
			movie = new Movie(movieResp.get(i).getTitle());
			movie.setId(Long.valueOf(i));
			movie.setName(movieResp.get(i).getTitle());
			movie.getActors().addAll(movieResp.get(i).getCast());
			movieRepository.save(movie);
		}
	}
}
