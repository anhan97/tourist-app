package edu.eiu.tourist_app

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

import java.util.Arrays

import edu.eiu.tourist_app.datamodel.WikipediaPage
import edu.eiu.tourist_app.datamodel.WikipediaResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: RecyclerView.Adapter<*>
    private lateinit var placesViewModel: PlacesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView!!.layoutManager = LinearLayoutManager(this)

        placesViewModel = ViewModelProviders.of(this, createViewModelFactory()).get(PlacesViewModel::class.java)
        val placesData = placesViewModel.touristSites
        placesData.observe(this, Observer { touristSites ->
            recyclerAdapter = TouristRecyclerAdapter(touristSites)
            recyclerView.adapter = recyclerAdapter
        })
    }

    private fun createViewModelFactory(): PlacesViewModel.PlacesViewModelFactory {
        val retrofit = createRetrofit()
        val wikipediaService = retrofit.create(WikipediaService::class.java)
        val placesRepository = PlacesRepository(wikipediaService)
        return PlacesViewModel.PlacesViewModelFactory(placesRepository)
    }

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://en.wikipedia.org")
                .build()
    }

}
