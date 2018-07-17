package org.readium.r2.navigator.UserSettings

class WordSpacing(value: Float?){

    companion object  {
        val max = 0.5f
        val min = 0.0f
        val step = 0.25f
    }

    var value = value ?: min

    fun increment(){
        if (value + step <= max){
            value += step
        }
    }

    fun decrement(){
        if (value - step >= min){
            value -= step
        }
    }

    override fun toString() : String {
        return value.toString() + "rem"
    }

}