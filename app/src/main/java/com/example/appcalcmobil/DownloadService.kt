package com.example.appcalcmobil

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast

class DownloadService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "⬇️ Descărcare începută în fundal...", Toast.LENGTH_LONG).show()

        // Simulăm o muncă grea pe un alt thread
        Thread {
            Thread.sleep(3000) // Așteaptă 3 secunde
            // Nota: Serviciile nu ar trebui să afișeze Toast de pe alte thread-uri în mod direct,
            // dar pentru simplitate lăsăm așa sau folosim Logcat.
        }.start()

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}