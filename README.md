# kotlin-components-starter

A framework for making Android apps, designed with a different scheme in mind than most.


## Overall Philosophy

*Complete reuse:*  Everything that is commonly used should be included, preferably as an inline 
extension function to reduce the app's method count.

*Intuitiveness:*  Use inline extension functions to make common tasks simple, such as requesting a 
photo from the user.  In standard Android, doing this properly would take at least a large file by 
itself.  We have extension functions that do it in one line.


## Anko

This framework is based on Anko, a UI building library that uses Kotlin DSL instead of XML.  
This allows your layout and associated code to be mixed, allowing greater cohesion in your apps.  
While you should separate out business logic from display logic, now the display logic can actually  
be where the layout is.

Note that Anko is still based on Android views, so everything should still be compatible.


## View Controllers

This framework does away with Activities and Fragments as much as possible, due to:
    - Irritating serialization / deserialization needed to transfer data between them
    - Difficulty of state retention
    - Various life-cycle issues

The replacement is called a ViewController.  Every screen (and sub screen) is a ViewController.

A ViewController is simply in charge of a set of views, creating them and cleaning up after them.  
They are retained statically and should *NOT* retain references to the views they create directly.


## Observables

The Observable system was built to make ViewControllers cleaner, better separating display logic and 
business logic.

An observable is simply a value holder that executes callbacks whenever it is changed.  It is, in 
itself, a mutable collection of lambdas.

You can attach a view to an observable by *binding* it.  Binding data is simple:

    view.bind(observable){ value ->
        text = value
        //This lambda is called both immediately and when the value changes.
    }
    view.listen(observable){ value ->
        text = value
        //This lambda is called both immediately and when the value changes.
    }
    
Alternately, if you need to listen to any changes in a way unrelated to a view, you can also add the 
callbacks manually.

    val callback = { value:String ->
       //do something with the value
       //This lambda is called only when the value changes.
    }
    observable.add(callback)
    
However, keep in mind that you probably need to remove the listeners at some point.

    observable.remove(callback)
    
    
## Observable Lists

There are also observable lists, which fire various callbacks whenever the list is changed.  This 
allows the animation of list changes using a RecyclerView.


## Adapters

While you could use traditional adapters, it is far easier to use one of the adapter from the 
library.  Use is simple and doesn't require an extra file:

    //Anko, makes a vertical recycler view.  A RecyclerView is the modern equivalent of a ListView.
    verticalRecyclerView() {
    
        //This wonderful shortcut function sets up the adapter using a list of items and a lambda 
        //which takes an observable of an item and makes a view using standard Anko.
        standardAdapter(items) { obs ->
            textView {
                bindString(obs)
                gravity = Gravity.CENTER
                textSize = 18f
                minimumHeight = dip(40)
                backgroundResource = selectableItemBackgroundResource
                onLongClick {
                    items.removeAt(obs.position)
                    true
                }
            }.lparams(matchParent, wrapContent)
        }
    }