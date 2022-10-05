package com.hfad.team21.ui
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.hfad.team21.R
import com.hfad.team21.domain.Beach
import com.hfad.team21.viewmodels.BeachViewModel
import com.hfad.team21.viewmodels.BeachViewModelFactory
import com.hfad.team21.viewmodels.SharedViewModel
import java.util.*
import kotlin.Comparator

class BeachFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var viewModel: BeachViewModel

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val distanceComparator: Comparator<Beach> = Comparator { b1, b2 ->
        compareValues(b1.distance, b2.distance)
    }

    private val airTempComparator: Comparator<Beach> = Comparator { b1, b2 ->
        compareValues(b2.air_temp, b1.air_temp)
    }

    private val waterTempComparator: Comparator<Beach> = Comparator { b1, b2 ->
        compareValues(b2.temp.toFloat(), b1.temp.toFloat())
    }

    private var currentComparator: Comparator<Beach> = distanceComparator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.beach_fragment, container, false)

        val application = requireActivity().application
        val viewModelFactory = BeachViewModelFactory(application, requireActivity())
        viewModel = ViewModelProvider(this, viewModelFactory).get(BeachViewModel::class.java)

        val allBeachesButton: TextView = root.findViewById(R.id.all_beaches_btn)
        val favoriteBeachesButton: TextView = root.findViewById(R.id.favorite_beaches_btn)
        val recycler: RecyclerView = root.findViewById(R.id.beach_rv)

        val search: SearchView = root.findViewById(R.id.beach_search_bar)
        search.setOnClickListener { search.isIconified = false }

        allBeachesButton.alpha = 1.0F

        val allBeachesAdapter = BeachAdapter(
            { viewModel.setFavorite(it) },
            { viewModel.removeFavorite(it) },
            { moveCamera(it) },
            this,
            R.layout.beach_recycler_item
        )

        val favoriteBeachesAdapter = BeachAdapter(
            { viewModel.setFavorite(it) },
            { viewModel.removeFavorite(it) },
            { moveCamera(it) },
            this,
            R.layout.beach_recycler_item
        )

        // Search listener!
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                allBeachesAdapter.filter.filter(newText)
                favoriteBeachesAdapter.filter.filter(newText)
                return false
            }
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })

        // Show all beaches as default
        recycler.adapter = allBeachesAdapter

        // Observe all beaches
        viewModel.beaches.observe(
            viewLifecycleOwner
        ) {
            Collections.sort(it, currentComparator)
            allBeachesAdapter.originalBeaches = it
            allBeachesAdapter.filter.filter(search.query)
        }

        // Observe favorite beaches
        viewModel.favoriteBeaches.observe(
            viewLifecycleOwner
        ) {
            Collections.sort(it, currentComparator)
            favoriteBeachesAdapter.originalBeaches = it
            favoriteBeachesAdapter.filter.filter(search.query)
        }

        // Swap RecyclerViewAdapter to show all beaches when this is selected
        allBeachesButton.setOnClickListener {
            recycler.swapAdapter(allBeachesAdapter, false)
            favoriteBeachesButton.alpha = 0.6F
            allBeachesButton.alpha = 1.0F
        }

        // Swap RecyclerViewAdapter to show favorite beaches when this is selected
        favoriteBeachesButton.setOnClickListener {
            recycler.swapAdapter(favoriteBeachesAdapter, false)
            favoriteBeachesButton.alpha = 1.0F
            allBeachesButton.alpha = 0.6F
        }

        // Navigate to home screen when back button is clicked
        root.findViewById<ImageView>(R.id.beach_back_btn).setOnClickListener {
            root.findNavController().navigate(R.id.action_navigation_beach_to_navigation_home)
        }

        // Sorts beaches according to the selected comparator
        viewModel.isSorting.observe(viewLifecycleOwner) {
            if (it) {
                Collections.sort(allBeachesAdapter.beaches, currentComparator)
                Collections.sort(favoriteBeachesAdapter.beaches, currentComparator)
                viewModel.finishSorting()
                allBeachesAdapter.notifyDataSetChanged()
                favoriteBeachesAdapter.notifyDataSetChanged()
            }
        }

        // Spinner for selecting what to sort beaches by
        val spinner: Spinner = root.findViewById(R.id.sort_by_sp)
        spinner.onItemSelectedListener = this
        return root
    }

    private fun moveCamera(beach: Beach) {
        val bundle = bundleOf("beachId" to beach.id)
        this.findNavController().navigate(R.id.action_navigation_beach_to_navigation_home, bundle)
        sharedViewModel.startMovingCamera(LatLng(beach.latitude, beach.longitude))
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        currentComparator = when (parent.getItemAtPosition(pos).toString()) {
            "Avstand" -> distanceComparator
            "Lufttemperatur" -> airTempComparator
            else -> waterTempComparator
        }
        viewModel.startSorting()
    }

    // Method required by the AdapterView class. Not used in this app.
    override fun onNothingSelected(parent: AdapterView<*>) {}
}
