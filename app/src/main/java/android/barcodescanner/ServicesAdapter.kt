package android.barcodescanner

import android.dataStorage.Service
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ServicesAdapter : RecyclerView.Adapter<ViewHolder>() {

    var data = listOf<Service>()
    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.services_item_view, parent, false) as TextureView

        return ViewHolder(view)
    }
}


class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

}