package edu.eiu.tourist_app;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.HashMap;
import java.util.List;

import edu.eiu.tourist_app.datamodel.QueryResponse;
import edu.eiu.tourist_app.datamodel.WikipediaPage;
import edu.eiu.tourist_app.datamodel.WikipediaResponse;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.mock.Calls;

public class PlacesViewModelTest {

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Test
    public void testGetTouristSites() {

        //PlacesViewModel viewModel = new PlacesViewModel(retrofit);

        WikipediaService wikipediaService = new WikipediaService() {
            @Override
            public Call<WikipediaResponse> getPlaces() {
                WikipediaResponse wikipediaResponse = new WikipediaResponse();
                wikipediaResponse.setQuery(new QueryResponse());

                HashMap<Integer, WikipediaPage> pages = new HashMap<Integer, WikipediaPage>();
                wikipediaResponse.getQuery().setPages(pages);

                WikipediaPage wikipediaPage = new WikipediaPage();
                wikipediaPage.setTitle("Statue");
                wikipediaPage.setPageId(1234);

                pages.put(1234, wikipediaPage);

                return Calls.response(wikipediaResponse);
            }
        };
        PlacesRepository placesRepository = new PlacesRepository(wikipediaService);
        PlacesViewModel.PlacesViewModelFactory factory = new PlacesViewModel.PlacesViewModelFactory(placesRepository);
        PlacesViewModel viewModel = factory.create(PlacesViewModel.class);

        LiveData<List<WikipediaPage>> touristSites = viewModel.getTouristSites();


        Assert.assertEquals("Statue", touristSites.getValue().get(0).getTitle());
    }
}
