package com.example.todolist

import com.google.gson.annotations.SerializedName
import java.util.UUID

class Task(
    @SerializedName("text") var text: String,
    @SerializedName("id") val id: String = UUID.randomUUID().toString(),
    @SerializedName("status") var status: Boolean = false
) {
    public fun GetText() : String{
        return text
    }

    public fun SetText(text: String){
        this.text = text
    }

    public fun SetStatus(status: Boolean){
        this.status = status
    }

    public fun GetStatus(): Boolean{
        return status
    }
}