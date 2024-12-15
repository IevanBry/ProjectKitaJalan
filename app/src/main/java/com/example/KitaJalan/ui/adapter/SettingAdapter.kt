package com.example.KitaJalan.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.KitaJalan.databinding.ItemSettingBinding

data class SettingItem(val title: String, val iconResId: Int)

class SettingsAdapter(context: Context, private val resource: Int, private val data: List<SettingItem>) :
    ArrayAdapter<SettingItem>(context, resource, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = convertView?.let {
            ItemSettingBinding.bind(it)
        } ?: ItemSettingBinding.inflate(LayoutInflater.from(context), parent, false)

        val item = data[position]

        binding.settingTitle.text = item.title
        binding.settingIcon.setImageResource(item.iconResId)

        return binding.root
    }
}
