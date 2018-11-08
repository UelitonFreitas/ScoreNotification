package com.notification.score.scorenotification

import android.app.Application
import com.google.firebase.FirebaseApp

class SocoreNotificationApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
    }
}