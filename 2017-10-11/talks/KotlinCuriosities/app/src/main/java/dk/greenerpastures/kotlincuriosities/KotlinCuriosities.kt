package dk.greenerpastures.kotlincuriosities

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlin.reflect.KProperty

/**
 *               **** Kotlin Curiosities ****
 *
 *         A few goodies and gotchas you might
 *                  not know (yet..)
 *
 */
















/**
 * Item 1: Don't ya know `Nothing`?
 */





// If you've heard about Kotlin, you've heard about nullable types

val someString: String = "Hello"
val nullableString: String? = null

val someInt: Int = 42
val nullableInt: Int? = null


// http://natpryce.com/articles/000818/nullable-hierarchy.png]

















// Have you heard of `Nothing`
// http://natpryce.com/articles/000818/nothing.png



// Great, let's make one!
var someItem : Nothing = ?? // You can't!













// ...mkay?
// ...but, why, then?














// What's the return-type of this function?

fun justThrow() = throw IllegalStateException()
















// Answer `throw` evaluates to `Nothing`



// ...but WHY!?





fun getStringOrThrow() =
        nullableString ?: throw IllegalStateException()



// The entire type-hierarchy looks like this
// http://natpryce.com/articles/000818/entire-hierarchy.png











// What is the type of null?
val justNull = null










/**
 * Item 2: Fun with generics and nullability
 */


fun <T> genericFunction(someValue: T) {
    // Do something generic
}

fun test() {
    genericFunction(null)
}


// But, what if we want to ensure only non-nullable types can be
// used with genericFunction?















fun <T : Any> genericFunction2(someValue: T) {
    // Do something generic safely with T
}


fun test2() {
    genericFunction2(null)
    genericFunction2(42)
}











// Buuut, what if we want the generic method to accept both,
// and just want to detect it?


inline fun <reified T> superSmartGenericMethod(someValue: T) {

    if (null is T) {
        // T is nullable
    } else {
        // T is not nullable
    }
}










/**
 * Item 3: SAM i am (just not in Kotlin)
 */


// As with Java 8, if a method takes an interface with a
// Single Abstract Method, you can just pass a function literal

fun addClickHandler(view: View) {
    view.setOnClickListener { Log.d("tag", "$it was Clicked!") }
}

val runnable = Runnable { println("This runs in a runnable") }







// ....but

interface MyClickListener {
    fun onClick(view: View)
}

interface MyView {
    fun setOnClickListener(listener: MyClickListener)
}

fun addClickHandler(view: MyView) {
    view.setOnClickListener { Log.d("tag", "$it was Clicked!") }
}



// :-(


// Bottom line: SAM conversion is part of Java interop, not Kotlin
// language itself











/**
 * Item 3: apply / with / let
 */

class Manager {
    fun init() {}
}

// How Java
fun createSomeManager() : Manager {
    val manager = Manager()
    manager.init()
    return manager
}


// How Kotlin!
fun createSomeManagerSmartly() = Manager().apply { init() }












@SuppressLint("CommitPrefEdits")
fun funWithWith(prefs: SharedPreferences) {

    with (prefs.edit()) {
        putString("api_token", "1234")
        putInt("click_count", 2)
    }.apply()

}




/**
 * Item 4: `lazy`: cool, but costly
 */


// This is run once at first access, then cached
val lazyInt by lazy { 42 }





// But, let's have a look on the inside...












// What if you don't need the synchronization?


val lazyFastInt by lazy(LazyThreadSafetyMode.NONE) { 42 }







// There's also...

fun costlyFunction(): Int = 42

val lazyToo by lazy(LazyThreadSafetyMode.PUBLICATION) { costlyFunction() }


// This one is good for slow, but idempotent production of values
// costlyFunction() _could_ be called twice in race conditions around initialization











/**
 * Delegated properties – let's roll our own!
 */


// lazy isn't special, it's just a library implementation of a language
// called "delegated properties"

// There are others, like `map`

class MapStorageClass {
    val map: MutableMap<String, String> = HashMap()

    var valueOne by map
    var valueTwo by map
}

fun testMapStorage() {

}



class UserPreferences(context: Context) : Preferences(context, "user_prefs") {
    var apiToken: String? by key("api_token")

    var buyClickCount: Int by key("click_count", default = 0)

    var someOther: Int? by key()
}


fun usePrefs(context: Context) {
    val userPrefs = UserPreferences(context)

    // Save the API token
    userPrefs.apiToken = "1234"

    // Read the apiToken
    val apiToken = userPrefs.apiToken

    userPrefs.buyClickCount += apiToken?.length ?: 0


    userPrefs.someOther = null
}









// ...wait? ...what!? ....HOW!?!?








interface ReadWriteProperty<in R, T> {
    /**
     * Returns the value of the property for the given object.
     * @param thisRef the object for which the value is requested.
     * @param property the metadata for the property.
     * @return the property value.
     */
    operator fun getValue(thisRef: R, property: KProperty<*>): T

    /**
     * Sets the value of the property for the given object.
     * @param thisRef the object for which the value is requested.
     * @param property the metadata for the property.
     * @param value the value to set.
     */
    operator fun setValue(thisRef: R, property: KProperty<*>, value: T)
}


















fun reference() {
    someString
    nullableString
    someInt
    nullableInt
    getStringOrThrow()
    justNull
    createSomeManager()
    createSomeManagerSmartly()
    lazyInt
    lazyFastInt
    lazyToo
    usePrefs(null)
    superSmartGenericMethod(nullableInt)
    funWithWith(3)
    addClickHandler(null)
    object : MyClickListener {
        override fun onClick(view: View) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
    test2()
    justThrow()
    someItem
}
