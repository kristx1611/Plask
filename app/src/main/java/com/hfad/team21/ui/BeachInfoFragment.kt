package com.hfad.team21.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hfad.team21.R
import com.hfad.team21.domain.BeachInfo
import com.hfad.team21.viewmodels.BeachInfoViewModel
import com.hfad.team21.viewmodels.BeachInfoViewModelFactory
import com.squareup.picasso.Picasso

/*

Displays detailed information about beaches.

*/
class BeachInfoFragment : Fragment() {

    private lateinit var viewModel: BeachInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.beach_info_fragment, container, false)
        val application = requireNotNull(this.activity).application
        val viewModelFactory = BeachInfoViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(BeachInfoViewModel::class.java)

        root.findViewById<ImageView>(R.id.beach_info_iv).setOnClickListener {
            findNavController().navigate(R.id.navigation_home_beach_overview_fragment)
        }

        val name: TextView = root.findViewById(R.id.beach_name_tv)
        val favorite: ImageView = root.findViewById(R.id.favorite_indicator_iv)
        val distance: TextView = root.findViewById(R.id.distance_tv)
        val air: TextView = root.findViewById(R.id.air_temp_tv)
        val water: TextView = root.findViewById(R.id.water_temp_tv)
        val wind: TextView = root.findViewById(R.id.wind_tv)
        val image: ImageView = root.findViewById(R.id.beach_photo_iv)
        val description: TextView = root.findViewById(R.id.beach_description_tv)
        val jetty: TextView = root.findViewById(R.id.jetty_tv)
        val diving: TextView = root.findViewById(R.id.diving_tv)
        val booth: TextView = root.findViewById(R.id.kiosk_tv)
        val toilet: TextView = root.findViewById(R.id.toilet_tv)
        val googleMap: Button = root.findViewById(R.id.open_google_maps_btn)
        val weather: ImageView = root.findViewById(R.id.weather_type_iv)

        viewModel.beachInfo.observe(viewLifecycleOwner) {
            if (it != null) {
                updateUI(
                    it,
                    name,
                    favorite,
                    image,
                    distance,
                    air,
                    water,
                    wind,
                    description,
                    jetty,
                    diving,
                    booth,
                    toilet,
                    googleMap,
                    weather
                )
            }
        }

        val id = arguments?.getInt("beachId") ?: 0 // arguments should never be null
        viewModel.getBeachInfo(id)

        return root
    }

    private fun updateUI(
        info: BeachInfo,
        name: TextView,
        favorite: ImageView,
        image: ImageView,
        distance: TextView,
        air: TextView,
        water: TextView,
        wind: TextView,
        description: TextView,
        jetty: TextView,
        diving: TextView,
        booth: TextView,
        toilet: TextView,
        mapButton: Button,
        weather: ImageView
    ) {

        name.text = info.name

        weather.setImageResource(resources.getIdentifier("drawable/" + info.weather_data_type, null, activity?.packageName))
        Picasso.get().load(info.image).into(image)

        if (info.favorite_id == null) {
            favorite.setImageResource(R.drawable.ic_baseline_favorite_24)
            viewModel.isFavorite = false
        } else {
            favorite.setImageResource(R.drawable.ic_baseline_favorite_24_red)
            viewModel.isFavorite = true
        }

        favorite.setOnClickListener {
            if (viewModel.isFavorite) {
                favorite.setImageResource(R.drawable.ic_baseline_favorite_24)
                viewModel.removeFavorite(info)
                viewModel.isFavorite = false
            } else {
                favorite.setImageResource(R.drawable.ic_baseline_favorite_24_red)
                viewModel.setFavorite(info)
                viewModel.isFavorite = true
            }
        }

        if (info.distance == null)
            distance.text = getString(R.string.location_off)
        else
            distance.text = getString(R.string.kilometer, String.format("%.2f", info.distance))

        air.text = getString(R.string.celsius, info.air_temp.toString())
        water.text = getString(R.string.celsius, info.temp)
        wind.text = getString(R.string.meters_per_second, info.wind.toString())

        if (info.description != null)
            description.text = info.description

        if (!info.has_jetty)
            jetty.visibility = View.GONE
        if (!info.has_diving)
            diving.visibility = View.GONE
        if (!info.has_booths)
            booth.visibility = View.GONE
        if (!info.has_toilets)
            toilet.visibility = View.GONE

        mapButton.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:${info.latitude},${info.longitude}?z=17") // Optionally add ?q=${Uri.encode(info.name)}
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            mapIntent.resolveActivity(requireContext().packageManager)?.let {
                startActivity(mapIntent)
            }
        }
    }
}
