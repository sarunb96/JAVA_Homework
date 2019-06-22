package com.wongnai.interview.movie.external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Component
public class MovieDataServiceImpl implements MovieDataService {
	public static final String MOVIE_DATA_URL
			= "https://raw.githubusercontent.com/prust/wikipedia-movie-data/master/movies.json";

	@Autowired
	private RestOperations restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public MoviesResponse fetchAll() {
		//TODO:
		// Step 1 => Implement this method to download data from MOVIE_DATA_URL and fix any error you may found.
		// Please noted that you must only read data remotely and only from given source,
		// do not download and use local file or put the file anywhere else.
		JSONArray arrJson = new JSONArray();
		JSONObject objJson = new JSONObject();
		MovieData movieData;
		MoviesResponse movieResp = new MoviesResponse();
		try {
			arrJson = new JSONArray(getURL(MOVIE_DATA_URL));
			for (int i = 0; i < arrJson.length(); i++) {
				objJson = (JSONObject) arrJson.get(i);
				movieData = new MovieData();
				movieData.setTitle(objJson.get("title").toString());
				movieData.setYear(Integer.parseInt(objJson.get("year").toString()));
				movieData.setCast((List<String>) Arrays.asList(objJson.get("cast").toString().replace("[", "").replace("]", "")));
				movieData.setGenres((List<String>) Arrays.asList(objJson.get("genres").toString().replace("[", "").replace("]", "")));
				movieResp.add(movieData);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return movieResp;
	}
	
	private String getURL(String url) {
		String readData = new String();
		StringBuilder strMovie = new StringBuilder();
		try {
			URL dataURL = new URL(url);
			URLConnection urlConnect = dataURL.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(urlConnect.getInputStream(), StandardCharsets.UTF_8));
			while ((readData = br.readLine()) != null) {
				strMovie.append(readData);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (readData == null || readData.equals("")) {
			strMovie.append("");
		}
		
		return strMovie.toString();
	}
}
