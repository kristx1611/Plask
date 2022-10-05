package com.hfad.team21.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.hfad.team21.R
import com.hfad.team21.domain.Beach
import java.util.*

/*

RecyclerView Adapter which handles CardViews with information about beaches.

 */
class BeachAdapter(
    val favorite: (beach: Beach) -> Unit,
    val unfavorite: (beach: Beach) -> Unit,
    val moveCamera: (beach: Beach) -> Unit,
    val context: Fragment,
    private val itemLayout: Int
) :
    RecyclerView.Adapter<BeachAdapter.BeachViewHolder>(), Filterable {

    inner class BeachViewHolder(cardView: View) : RecyclerView.ViewHolder(cardView) {
        val layout: ConstraintLayout = cardView.findViewById(R.id.beach_layout)
        val favoriteIndicator: ImageView = cardView.findViewById(R.id.favorite_indicator_iv)
        val nameTextview: TextView = cardView.findViewById(R.id.beach_name_tv)
        val distance: TextView = cardView.findViewById(R.id.beach_distance_tv)
        val airTemp: TextView = cardView.findViewById(R.id.air_temp_tv)
        val waterTemp: TextView = cardView.findViewById(R.id.water_temp_tv)
        val wind: TextView = cardView.findViewById(R.id.wind_tv)
        val weatherType: ImageView = cardView.findViewById(R.id.weather_type_iv)
    }

    var originalBeaches: List<Beach> = emptyList()

    var beaches: List<Beach> = originalBeaches
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeachViewHolder {
        val cardView = LayoutInflater
            .from(parent.context)
            .inflate(
                itemLayout,
                parent,
                false
            )
        return BeachViewHolder(cardView)
    }

    // Updates the CardViews when the user scrolls through the RecyclerView
    override fun onBindViewHolder(holder: BeachViewHolder, position: Int) {
        val beach = beaches[position]
        holder.layout.setOnClickListener {
            moveCamera(beach)
        }
        holder.nameTextview.text = beach.name
        holder.airTemp.text = context.getString(R.string.celsius, beach.air_temp.toString())
        holder.waterTemp.text = context.getString(R.string.celsius, beach.temp)
        holder.wind.text = context.getString(R.string.meters_per_second, beach.wind.toString())

        if (beach.distance == null)
            holder.distance.text = context.getString(R.string.location_off)
        else
            holder.distance.text = context.getString(R.string.kilometer, String.format("%.2f", beach.distance))

        holder.weatherType.setImageResource(context.resources.getIdentifier("drawable/" + beach.weather_data_type, null, context.activity?.packageName))

        // Checks whether a beach is "favorited", and sets the favorite indicator accordingly
        val drawableResource = if (beach.favorite_id == null) R.drawable.ic_baseline_favorite_24 else R.drawable.ic_baseline_favorite_24_red
        holder.favoriteIndicator.setImageResource(drawableResource)

        // Updates the favorite status and favorite indicator when the latter is clicked.
        holder.favoriteIndicator.setOnClickListener {
            if (beach.favorite_id == null) {
                holder.favoriteIndicator.setImageResource(R.drawable.ic_baseline_favorite_24_red)
                favorite(beach)
            } else {
                holder.favoriteIndicator.setImageResource(R.drawable.ic_baseline_favorite_24)
                unfavorite(beach)
            }
        }
    }

    override fun getItemCount(): Int = beaches.size

    override fun getFilter(): Filter {
        return beachFilter
    }

    // Search filter
    private val beachFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredBeaches: MutableList<Beach> = mutableListOf()

            if (constraint.isEmpty())
                filteredBeaches.addAll(originalBeaches)
            else {
                val filterPattern: String = constraint.toString().toLowerCase(Locale.ROOT).trim()
                for (beach in originalBeaches) {
                    if (beach.name.toLowerCase(Locale.ROOT).contains(filterPattern))
                        filteredBeaches.add(beach)
                }
            }

            val results = FilterResults()
            results.values = filteredBeaches
            return results
        }

        @Suppress("UNCHECKED_CAST") // results.values should never be anything that can't be cast to List<Beach>
        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            beaches = results.values as List<Beach>
        }
    }
}
