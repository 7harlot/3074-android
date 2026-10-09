package ca.gbc.comp3074.morrison.lab4

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ClientAdapter(context: Context, private val customers: List<CustomerModel>): ArrayAdapter<CustomerModel>(
    context,
    R.layout.item_layout,
    customers) {

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_layout,
            parent,
            false
        )
        val customer = customers[position]
        view.findViewById<TextView>(R.id.tvId).text = customer.id.toString()
        view.findViewById<TextView>(R.id.tvName).text = customer.name
        view.findViewById<TextView>(R.id.tvAge).text = customer.age.toString()
        view.findViewById<TextView>(R.id.tvActive).text = customer.active.toString()
        return view
    }

}