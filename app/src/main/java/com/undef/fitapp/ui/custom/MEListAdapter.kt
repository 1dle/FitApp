package com.undef.fitapp.ui.custom

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.undef.fitapp.R
import com.undef.fitapp.models.Food
import com.undef.fitapp.models.FoodNMet
import kotlinx.android.synthetic.main.list_item_mne.view.*

class MEListAdapter(private val myDataset: MutableList<FoodNMet>) :
    RecyclerView.Adapter<MEListAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        // create a new view
        val singleItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_mne, parent, false)
        //        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(singleItem)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.textView.text = myDataset[position]
        holder.view.tvResultItemTitle.text = myDataset[position].getTitle()
        holder.view.tvResultItemKcals.text = "${myDataset[position].getKcals()} kcals"
        holder.view.ivResultItemType.setImageResource(myDataset[position].getIcon())
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}
