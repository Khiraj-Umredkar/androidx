package com.example.androidxdogs.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.androidxdogs.R
import com.example.androidxdogs.databinding.ItemDogBinding
import com.example.androidxdogs.model.DogBreed
import com.example.androidxdogs.util.getProgressDrawable
import com.example.androidxdogs.util.loadImage
import kotlinx.android.synthetic.main.item_dog.view.*

class DogsListAdapter(val dogList: ArrayList<DogBreed>) : RecyclerView.Adapter<DogsListAdapter.DogsListViewHolder>(),DogClickListner {

    fun updateDogList(newDogList: List<DogBreed>) {
        dogList.clear()
        dogList.addAll(newDogList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogsListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
//        val view = inflater.inflate(R.layout.item_dog, parent, false)
        val view = DataBindingUtil.inflate<ItemDogBinding>(inflater,R.layout.item_dog,parent,false)
        return DogsListViewHolder(view)
    }

    override fun getItemCount() = dogList.count()


    override fun onBindViewHolder(holder: DogsListViewHolder, position: Int) {
        holder.view.dog = dogList[position]
        holder.view.listner = this
       /* holder.view.name.text = dogList[position].dogBreed
        holder.view.lifeSpan.text = dogList[position].lifeSpan

        holder.view.imageView.loadImage(
            dogList[position].imageUrl,
            getProgressDrawable(holder.view.imageView.context)
        )


        holder.view.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToDetailedFragment(dogList[position].uuid)
            Navigation.findNavController(it).navigate(action)
        }*/

    }

    override fun onDogClick(view: View) {
        super.onDogClick(view)
        val uuid = view.dogId.text.toString().toInt()
        val action = ListFragmentDirections.actionListFragmentToDetailedFragment(uuid)
        Navigation.findNavController(view).navigate(action)
    }

    class DogsListViewHolder(var view: ItemDogBinding) : RecyclerView.ViewHolder(view.root)
}