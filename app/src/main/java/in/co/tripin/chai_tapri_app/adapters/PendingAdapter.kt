package `in`.co.tripin.chai_tapri_app.adapters

import `in`.co.tripin.chai_tapri_app.Managers.Logger
import `in`.co.tripin.chai_tapri_app.POJOs.Responces.PendingOrdersResponce
import `in`.co.tripin.chai_tapri_app.R
import `in`.co.tripin.chai_tapri_app.activities.OrderHistoryActivity
import `in`.co.tripin.chai_tapri_app.activities.QRCodeScannerActivity
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
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

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: PendingAdapter.ViewHolder, position: Int) {
        holder.bindItems(data[position])
        val status = data[position].orderStatus
        val paymentType = data[position].paymentType;
        holder.linearLayout.visibility=View.GONE
        holder.paymentmethod.text = paymentType;
        holder.orderId.text ="#"+data[position].shortId
        val timestamp = data[position].createdAt
        holder.orderDate.text = timestamp.substring(0,timestamp.indexOf('T'))

        holder.tvCustName.text = data[position].userId.fullName;
        if(data[position].userId.office!=null)
        {
            holder.tvOfficeName.visibility=View.VISIBLE
            //holder.tvBlockName.text = "/ "+data[position].userId.office.block.name
            holder.tvOfficeName.text =data[position].userId.office.name+"/ "+data[position].userId.office.officeCode+"/ "+data[position].userId.office.block.name
        }

        if(status == "accepted"){
            holder.b2.background = ContextCompat.getDrawable(context,R.drawable.button_main_selector)
            holder.b2.text = "Send Order"
            //holder.b2.setTextColor(ContextCompat.getColor(context,R.color.colorGreyLight))
            holder.b1.text = "Contact Customer"
        }else if(status == "on the way"){
            if(paymentType == "WOD")
            {
                holder.linearLayout.visibility=View.VISIBLE
            }
            holder.b2.background = null
            holder.b2.text = "On the way!"
            holder.b1.text = "Contact Customer"

        }

        holder.b1.setOnClickListener{

            if(status == "accepted"){
                pendingOrdersInteractionCallback.onCalledCustomer(data[position].userId.mobile)
                Logger.v("Mobile: ${data[position].UserId().mobile}")
            }else if(status == "ordered"){
                pendingOrdersInteractionCallback.onOrderRejected(data[position].id)
            }

        }
        holder.b2.setOnClickListener{
            if(status == "accepted"){
                pendingOrdersInteractionCallback.onOrderSent(data[position].id)
            }else if(status == "ordered"){
                pendingOrdersInteractionCallback.onOrderAccepted(data[position].id)
//                holder.b2.background = ContextCompat.getDrawable(context,R.drawable.button_light_selector)
//                holder.b2.text = "Send Order"
//                //holder.b2.setTextColor(ContextCompat.getColor(context,R.color.colorGreyLight))
//                holder.b1.text = "Contact Customer"
            }
        }



            holder.linearLayout.setOnClickListener{
            val intent = Intent(context, QRCodeScannerActivity::class.java)
            intent.putExtra("ORDERID", data[position].id);
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val totalCost = itemView.findViewById<TextView>(R.id.totalcost)

        val b1 = itemView.findViewById<Button>(R.id.b1)
        val b2 = itemView.findViewById<Button>(R.id.b2)
        val linearLayout = itemView.findViewById<LinearLayout>(R.id.buttonScan);
        val paymentmethod= itemView.findViewById<TextView>(R.id.paymentmethod);
        val orderId = itemView.findViewById<TextView>(R.id.orderid)
        val orderDate = itemView.findViewById<TextView>(R.id.orderdate)
        val tvCustName = itemView.findViewById<TextView>(R.id.tvCustomerName);
        //val tvBlockName = itemView.findViewById<TextView>(R.id.tvBlockName)
        val tvOfficeName = itemView.findViewById<TextView>(R.id.tvOfficeName)
        //val tvOfficeCode = itemView.findViewById<TextView>(R.id.tvOfficeCode)

        fun bindItems(order : PendingOrdersResponce.Datum) = with(itemView)  {

            itemView.totalcost.text = "₹"+order.totalAmount.toString()
            itemView.addressall.text = order.addressId.fullAddressString

            val selectedItemsRecyclerAdapter = SelectedItemsRecyclerAdapter(order.details)
            val layoutManager = CustomLinearLayoutManager(context)
            itemView.selected_items_list.layoutManager = layoutManager
            itemView.selected_items_list.adapter = selectedItemsRecyclerAdapter


            itemView.b2.setOnClickListener{}

        }
    }
}