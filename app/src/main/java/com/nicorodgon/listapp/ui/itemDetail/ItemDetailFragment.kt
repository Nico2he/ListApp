package com.nicorodgon.listapp.ui.itemDetail

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.nicorodgon.listapp.R
import com.nicorodgon.listapp.databinding.FragmentItemDetailBinding
import com.nicorodgon.listapp.model.ItemLista
import com.nicorodgon.listapp.model.Notification
import com.nicorodgon.listapp.model.messageExtra
import com.nicorodgon.listapp.model.notificationID
import com.nicorodgon.listapp.model.titleExtra
import java.util.Calendar
import java.util.Date

class ItemDetailFragment : Fragment(R.layout.fragment_item_detail) {
    private val viewModel: ItemDetailViewModel by viewModels {
        ItemDetailViewModelFactory(arguments?.getParcelable(EXTRA_ITEM)!!)
    }
    companion object {
        const val EXTRA_ITEM = "ItemDetailFragment:ItemLista"
        private const val CHANNEL_ID = "channel01"
    }
    private lateinit var binding: FragmentItemDetailBinding
    private var nombreItem = ""

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentItemDetailBinding.bind(view)

        binding.notifyItemButton.setOnClickListener {
            scheduleNotification()
        }

        val imagenItemDetail = view.findViewById<ImageView>(R.id.imagenItemDetail)
        val nombreItemDetail = view.findViewById<TextView>(R.id.nombreItemDetail)
        val descripcionItemDetail = view.findViewById<TextView>(R.id.descripcionItemDetail)

        viewModel.item.observe(viewLifecycleOwner){ item: ItemLista ->
            (requireActivity() as AppCompatActivity).supportActionBar?.title = item.nombre_item
            Glide.with(this).load(item.imagen_item).into(imagenItemDetail)
            nombreItemDetail.text = item.nombre_item
            descripcionItemDetail.text = item.descripcion
            nombreItem = item.nombre_item
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleNotification() {
        createNotificationChannel()
        val intent = Intent(context, Notification::class.java)
        val title = "Recordatorio"
        val message = "Tienes acciones pendientes con: " + nombreItem
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        showAlert(time)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "MyNotification"
            val description = "My notification channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(CHANNEL_ID, name, importance)
            notificationChannel.description = description
            val notificationManager = context?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun getTime(): Long {
        val minute = 0
        val hour = 0
        val day = binding.datePickerItem.dayOfMonth
        val month = binding.datePickerItem.month
        val year = binding.datePickerItem.year

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)
        return calendar.timeInMillis
    }

    private fun showAlert(time: Long) {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(context)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(context)

        AlertDialog.Builder(context)
            .setTitle("Notification Programada")
            .setMessage(
                "Recibirás una notificación en la siguiente fecha:\n"
                        + dateFormat.format(date) + " " + timeFormat.format(date))
            .setPositiveButton("Aceptar"){_,_ ->}
            .show()
    }

}