package com.notification.score.scorenotification.imageprovider

interface ImageProvider {
    fun getImage()
    fun startToGetImagePeriodically()
    fun stopToGetImagesPeriodically()
}