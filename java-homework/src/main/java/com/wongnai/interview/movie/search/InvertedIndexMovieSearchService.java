package com.wongnai.interview.movie.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.wongnai.interview.movie.Movie;
import com.wongnai.interview.movie.MovieRepository;
import com.wongnai.interview.movie.MovieSearchService;

@Component("invertedIndexMovieSearchService")
@DependsOn("movieDatabaseInitializer")
public class InvertedIndexMovieSearchService implements MovieSearchService {
	@Autowired
	private MovieRepository movieRepository;

	@Override
	public List<Movie> search(String queryText) {
		//TODO: Step 4 => Please implement in-memory inverted index to search movie by keyword.
		// You must find a way to build inverted index before you do an actual search.
		// Inverted index would looks like this:
		// -------------------------------
		// |  Term      | Movie Ids      |
		// -------------------------------
		// |  Star      |  5, 8, 1       |
		// |  War       |  5, 2          |
		// |  Trek      |  1, 8          |
		// -------------------------------
		// When you search with keyword "Star", you will know immediately, by looking at Term column, and see that
		// there are 3 movie ids contains this word -- 1,5,8. Then, you can use these ids to find full movie object from repository.
		// Another case is when you find with keyword "Star War", there are 2 terms, Star and War, then you lookup
		// from inverted index for Star and for War so that you get movie ids 1,5,8 for Star and 2,5 for War. The result that
		// you have to return can be union or intersection of those 2 sets of ids.
		// By the way, in this assignment, you must use intersection so that it left for just movie id 5.
		TreeMap<String, List<Long>> invertIndex = new TreeMap<String, List<Long>>(String.CASE_INSENSITIVE_ORDER);
		Iterable<Movie> movie = movieRepository.findAll();
		String[] term;
		List<Long> ids = new ArrayList<Long>();
		List<Long> movieIds = new ArrayList<Long>();
		for (Movie movieData : movie) {
			term = movieData.getName().split(" ");
			ids.add(movieData.getId());
			for (int i = 0; i < term.length; i++) {
				term[i] = term[i].replaceAll("[^A-Za-z0-9]", "").toLowerCase();
				if (invertIndex.containsKey(term[i])) {
					if (!invertIndex.get(term[i]).containsAll(ids)) {
						invertIndex.get(term[i]).addAll(ids);
					}
				} else {
					invertIndex.put(term[i], ids);
				}
			}
			ids = new ArrayList<Long>();
		}

		String[] movieName;
		if (!queryText.equals("") || queryText != null) {
			movieName = queryText.toLowerCase().split(" ");
			// get all ID of search movie
			for (int i = 0; i < movieName.length; i++) {
				movieName[i] = movieName[i].replaceAll("[^A-Za-z0-9]", "");
				if (invertIndex.containsKey(movieName[i])) {
					ids.addAll(invertIndex.get(movieName[i]));
				}
			}

			// check if ID duplicated
			Long distinctCount = ids.parallelStream().distinct().count();
			if (distinctCount != ids.size()) {
				for (Long movieId : ids) {
					if (Collections.frequency(ids, movieId) >= movieName.length) {
						movieIds.add(movieId);
					}
				}
			} else {
				movieIds = ids;
			}
		}
		return (List<Movie>) movieRepository.findAllById(movieIds);
	}
}
