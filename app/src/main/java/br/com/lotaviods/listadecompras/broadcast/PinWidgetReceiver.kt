package br.com.lotaviods.listadecompras.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import br.com.lotaviods.listadecompras.R

class PinWidgetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(
            context,
            context?.getString(R.string.widget_added),
            Toast.LENGTH_SHORT
        ).show()
    }
}