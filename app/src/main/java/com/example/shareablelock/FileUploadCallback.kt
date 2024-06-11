package com.example.shareablelock

interface FileUploadCallback {
    fun onSuccess(fileName: String)
    fun onFailure(errorMessage: String)
}
