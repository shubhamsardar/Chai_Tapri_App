package `in`.co.tripin.chai_tapri_app.adapters

import `in`.co.tripin.chai_tapri_app.POJOs.Responces.PendingOrdersResponce
import `in`.co.tripin.chai_tapri_app.R
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.item_order_pending.view.*

class PendingAdapter(val data: List<PendingOrdersResponce.Datum>,
                     val context :Context,
                     val pendingOrdersInteractionCallback: PendingOrdersInteractionCallback) :
        RecyclerView.Adapter<PendingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_order_pending, parent, false))
    }

    override fun onBindViewHolder(holder: PendingAdapter.ViewHolder, position: Int) {
        holder.bindItems(data[position])

        holder.b1.setOnClickListener{
            pendingOrdersInteractionCallback.onOrderAccepted(data[position].id)
        }
        holder.b2.setOnClickListener{
            pendingOrdersInteractionCallback.onOrderRejected(data[position].id)

        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val totalCost = itemView.findViewById<TextView>(R.id.totalcost)

        val b1 = itemView.findViewById<Button>(R.id.b1)
        val b2 = itemView.findViewById<Button>(R.id.b2)


        fun bindItems(order : PendingOrdersResponce.Datum) = with(itemView)  {

            itemView.totalcost.text = "₹"+order.totalAmount.toString()

            val selectedItemsRecyclerAdapter = SelectedItemsRecyclerAdapter(order.details)
            val layoutManager = CustomLinearLayoutManager(context)
            itemView.selected_items_list.layoutManager = layoutManager
            itemView.selected_items_list.adapter = selectedItemsRecyclerAdapter


            itemView.b2.setOnClickListener{}

        }
    }
}