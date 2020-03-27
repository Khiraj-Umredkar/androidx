package com.example.androidxdogs.view

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.telephony.SmsManager
import android.view.*
import androidx.appcompat.app.AlertDialog
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
import com.example.androidxdogs.databinding.SendSmsDialogBinding
import com.example.androidxdogs.model.DogBreed
import com.example.androidxdogs.model.DogPallet
import com.example.androidxdogs.model.SmsInfo
import com.example.androidxdogs.util.getProgressDrawable
import com.example.androidxdogs.util.loadImage
import com.example.androidxdogs.viewmodel.DetailScreenViewModel
import kotlinx.android.synthetic.main.fragment_detailed.*
import java.util.jar.Manifest

class DetailedFragment : Fragment() {
    private lateinit var viewModel:DetailScreenViewModel
    private lateinit var dataBinding: FragmentDetailedBinding
    private var dogUuid = 0
    private  var sendSmsStarted = false
    private var currentDog: DogBreed? = null
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
            currentDog = dog
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
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plane"
                intent.putExtra(Intent.EXTRA_SUBJECT,"Checout this Dog Breed")
                intent.putExtra(Intent.EXTRA_TEXT,"${currentDog?.dogBreed} bred for ${currentDog?.bredFor}")
                intent.putExtra(Intent.EXTRA_STREAM,"${currentDog?.imageUrl}")
                startActivity(Intent.createChooser(intent,"Share With"))

            }
        }

        return super.onOptionsItemSelected(item)
    }
    fun onPermissionResult(permissionGrantetd: Boolean){

        if(sendSmsStarted && permissionGrantetd){
            context?.let {
                val smsInfo = SmsInfo("","${currentDog?.dogBreed} bred for ${currentDog?.bredFor}","${currentDog?.imageUrl}")

                val dialogBinding = DataBindingUtil.inflate<SendSmsDialogBinding>(LayoutInflater.from(it),R.layout.send_sms_dialog,null,false)

                AlertDialog.Builder(it)
                    .setView(dialogBinding.root)
                    .setPositiveButton("Send Sms") {
                        dialog, which ->
                        if(!dialogBinding.smsDestination.text.isNullOrEmpty()){
                            smsInfo.to = dialogBinding.smsDestination.text.toString()
                            sendSms(smsInfo)
                        }
                    }
                    .setNegativeButton("Cancle"){ dialog, which -> }
                    .show()

                dialogBinding.smsInfo = smsInfo


            }
        }
    }

    private fun sendSms(smsInfo: SmsInfo){

        val intent = Intent(context,MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context,0,intent,0)
        val sms = SmsManager.getDefault()

        sms.sendTextMessage(smsInfo.to,null,smsInfo.text,pendingIntent,null)

    }

}
