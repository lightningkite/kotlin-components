# TODO Items for Kotlin Components

## New Features

### NetList

A list that both pulls from a URL and pushes changes to it.  The list won't actually change until 
it is done.

### adapter()

A more Anko-like adapter that uses *UI* internally like so:

    adapter(list){ itemObservable ->
        textView(){
            bindAny(itemObservable)
        }
    }
    