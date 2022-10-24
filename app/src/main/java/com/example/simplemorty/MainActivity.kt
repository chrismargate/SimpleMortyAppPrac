package com.example.simplemorty

import android.net.DnsResolver
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.simplemorty.databinding.ActivityMainBinding
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val BASE_URL = "https://rickandmortyapi.com/api/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

        val retroFit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        val rickAndMortyService: RickAndMortyService = retroFit.create(RickAndMortyService::class.java)

        rickAndMortyService.getCharacterById(3).enqueue(object: Callback<GetCharacterByIdResponse> {

            override fun onResponse(call: Call<GetCharacterByIdResponse>, response: Response<GetCharacterByIdResponse>){
                Log.i("MainActivity",response.toString())

                if(!response.isSuccessful){
                    Toast.makeText(this@MainActivity,"Unsuccessful network call!!", Toast.LENGTH_SHORT).show()
                    return
                }

                val body = response.body()!!
                val imageUrl = body.image
                Picasso.get().load(imageUrl).into(binding.headerImageView);
                binding.nameTextView.text = body.name
                binding.aliveTextView.text = body.status
                binding.originTextView.text = body.origin?.name
                binding.speciesTextView.text = body.species

                if(body.gender.equals("male",true)){
                    binding.genderImageView.setImageResource(R.drawable.ic_male_24)
                }else{
                    binding.genderImageView.setImageResource(R.drawable.ic_female_24)
                }
            }

            override fun onFailure(call: Call<GetCharacterByIdResponse>, t: Throwable){
                Log.i("MainActivity",t.message ?: "Null message")
            }
        })
    }
}