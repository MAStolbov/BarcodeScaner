package android.barcodescanner

import android.barcodescanner.databinding.ServicesItemViewBinding
import android.dataStorage.Service
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ServicesAdapter : ListAdapter<Service,ServicesAdapter.ServicesViewHolder>(ServicesNightDiffCallback()) {

    override fun onBindViewHolder(holder: ServicesViewHolder, position: Int) {
        val item = getItem(position)
        holder.apply { bind(item) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicesViewHolder {
        return ServicesViewHolder.from(parent)
    }

    class ServicesViewHolder private constructor(val binding: ServicesItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item:Service){
            binding.servicePrice.text = "Цена: ${item.price}"
            binding.serviseName.text = "Услуга: ${item.name}"
        }

        companion object{
            fun from(parent:ViewGroup):ServicesViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ServicesItemViewBinding.inflate(layoutInflater,parent,false)
                return ServicesViewHolder(binding)
            }
        }
    }
}

class ServicesNightDiffCallback : DiffUtil.ItemCallback<Service>() {
    override fun areItemsTheSame(oldItem: Service, newItem: Service): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Service, newItem: Service): Boolean {
        return oldItem == newItem
    }
}