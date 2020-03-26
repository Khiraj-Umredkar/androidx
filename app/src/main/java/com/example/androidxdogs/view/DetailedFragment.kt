package com.example.androidxdogs.view

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

import com.example.androidxdogs.R
import com.example.androidxdogs.databinding.FragmentDetailedBinding
import com.example.androidxdogs.model.DogPallet
import com.example.androidxdogs.util.getProgressDrawable
import com.example.androidxdogs.util.loadImage
import com.example.androidxdogs.viewmodel.DetailScreenViewModel
import kotlinx.android.synthetic.main.fragment_detailed.*

class DetailedFragment : Fragment() {
    private lateinit var viewModel:DetailScreenViewModel
    private lateinit var dataBinding: FragmentDetailedBinding
    private var dogUuid = 0
    private  var sendSmsStarted = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        dataBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_detailed,container,false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            dogUuid = DetailedFragmentArgs.fromBundle(it).doguuid

        }
        viewModel = ViewModelProvider(this).get(DetailScreenViewModel::class.java)
        viewModel.fetch(dogUuid)



        observeViewModel()
       /* button_back.setOnClickListener {
            val action = DetailedFragmentDirections.actionDetailedFragmentToListFragment()
            Navigation.findNavController(it).navigate(action)
        }*/
    }

    fun observeViewModel(){
        viewModel.dogLiveData.observe(viewLifecycleOwner, Observer {dog ->
            dog?.let {
//                dogName.text = dog.dogBreed
//                dogPurpose.text  = dog.bredFor
//                dogTemprament.text = dog.temperament
//                dogLifeSpan.text = dog.lifeSpan
//                context?.let {
//                    dogImage.loadImage(dog.imageUrl, getProgressDrawable(it))
//                }

                dataBinding.dog = dog

                it.imageUrl?.let {
                    setBackGroundColor(it)
                }

            }

        })
    }

    fun setBackGroundColor(url:String){
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object :CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {}

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource)
                        .generate { palette ->
                            val intColor  = palette?.dominantSwatch?.rgb ?: 0
                            val myPallett = DogPallet(intColor)
                            dataBinding.pallet = myPallett
                        }
                }

            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_send_sms -> {
            sendSmsStarted = true
                (activity as MainActivity).checkSmsPermission()
            }

            R.id.action_share -> {

            }
        }

        return super.onOptionsItemSelected(item)
    }
    fun onPermissionResult(permissionGrantetd: Boolean){
    }

}
