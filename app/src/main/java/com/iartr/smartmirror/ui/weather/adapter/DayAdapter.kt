package com.iartr.smartmirror.ui.weather.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iartr.smartmirror.R
import com.iartr.smartmirror.ui.weather.DayTemp
import com.squareup.picasso.Picasso

class DayAdapter : RecyclerView.Adapter<DayAdapter.DayTempHolder>() {
    val dayTempList = ArrayList<DayTemp>()
    class DayTempHolder(item : View) : RecyclerView.ViewHolder(item) {
        val temp : TextView = item.findViewById(R.id.temp)
        val time : TextView = item.findViewById(R.id.time)
        val icon : ImageView = item.findViewById(R.id.iconView)

        fun bind(dayTemp: DayTemp){
            (dayTemp.temp+"\u00B0").also { temp.text = it }
            time.text = dayTemp.time
            Picasso.get().load("https:"+dayTemp.icon).error(R.drawable.iconholder).into(icon)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayTempHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.daytemp_item, parent, false)
        return DayTempHolder(itemView)
    }

    override fun onBindViewHolder(holder: DayTempHolder, position: Int) {
        holder.bind(dayTempList[position])
    }

    override fun getItemCount(): Int {
        return dayTempList.size
    }

    fun addDayTemp(dayTemp: DayTemp){
        dayTempList.add(dayTemp)
        notifyDataSetChanged()
    }

    fun clear(){
        dayTempList.clear()
    }
}