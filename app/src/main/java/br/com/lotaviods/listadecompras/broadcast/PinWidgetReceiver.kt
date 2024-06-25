package br.com.lotaviods.listadecompras.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class PinWidgetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "Widget adicionado com sucesso!", Toast.LENGTH_SHORT).show()
    }
}