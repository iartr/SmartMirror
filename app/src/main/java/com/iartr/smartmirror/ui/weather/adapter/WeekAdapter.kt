package com.iartr.smartmirror.ui.weather.adapter

import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.iartr.smartmirror.R
import com.iartr.smartmirror.ui.weather.WeekTemp
import com.squareup.picasso.Picasso

class WeekAdapter(val listener: Listener): RecyclerView.Adapter<WeekAdapter.WeekTempHolder>() {
    val weekTempList = ArrayList<WeekTemp>()

    class WeekTempHolder(item : View) : ViewHolder(item) {
        val temp : TextView = item.findViewById(R.id.temp)
        val date : TextView = item.findViewById(R.id.date)
        val icon : ImageView = item.findViewById(R.id.iconView)

        fun bind(weekTemp: WeekTemp, listener: Listener){
            (weekTemp.temp+"\u00B0").also { temp.text = it }
            date.text = weekTemp.date
            itemView.setOnClickListener{
                listener.onClick(weekTemp)
            }
            Picasso.get().load("https:"+weekTemp.icon).error(R.drawable.iconholder).into(icon)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekTempHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.weektemp_item, parent, false)
        return WeekTempHolder(itemView)
    }

    override fun onBindViewHolder(holder: WeekTempHolder, position: Int) {
        holder.bind(weekTempList[position], listener)
    }

    override fun getItemCount(): Int {
        return weekTempList.size
    }

    fun addWeekTemp(weekTemp: WeekTemp){
        weekTempList.add(weekTemp)
        notifyDataSetChanged()
    }

    fun clear(){
        weekTempList.clear()
    }

    interface Listener{
        fun onClick(weekTemp: WeekTemp)
    }
}