package edu.eiu.tourist_app;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.eiu.tourist_app.datamodel.QueryResponse;
import edu.eiu.tourist_app.datamodel.WikipediaPage;
import edu.eiu.tourist_app.datamodel.WikipediaResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlacesRepository {

    private WikipediaService wikipediaService;

    public PlacesRepository(WikipediaService wikipediaService) {
        this.wikipediaService = wikipediaService;
    }

    public LiveData<List<WikipediaPage>> getTouristSites() {
        final MutableLiveData<List<WikipediaPage>> liveData = new MutableLiveData<>();

        wikipediaService.getPlaces().enqueue(new Callback<WikipediaResponse>() {
            @Override
            public void onResponse(Call<WikipediaResponse> call, Response<WikipediaResponse> response) {
                WikipediaResponse wikipediaResponse = response.body();
                QueryResponse queryResponse = wikipediaResponse.getQuery();
                Map<Integer, WikipediaPage> pagesMap = queryResponse.getPages();
                List<WikipediaPage> pages = new ArrayList<>(pagesMap.values());
                Collections.sort(pages, new Comparator<WikipediaPage>() {
                    @Override
                    public int compare(WikipediaPage lhs, WikipediaPage rhs) {

                        return lhs.getIndex()-rhs.getIndex();
                    }
                });

                liveData.postValue(pages);
            }

            @Override
            public void onFailure(Call<WikipediaResponse> call, Throwable t) {
                Log.e("PlacesRespository", "Wikipedia request bamboozled", t);
            }
        });

        return liveData;
    }

    private List<WikipediaPage> handleResponse(InputStream response) {
        Gson gson = new Gson();
        BufferedReader reader = new BufferedReader(new InputStreamReader(response));
        WikipediaResponse wikipediaResponse = gson.fromJson(reader, WikipediaResponse.class);
        QueryResponse queryResponse = wikipediaResponse.getQuery();
        Map<Integer, WikipediaPage> pagesMap = queryResponse.getPages();
        List<WikipediaPage> pages = new ArrayList<>(pagesMap.values());
        return pages;
    }

}
