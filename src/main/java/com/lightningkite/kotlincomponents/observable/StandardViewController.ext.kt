package com.lightningkite.kotlincomponents.observable

import com.lightningkite.kotlincomponents.viewcontroller.StandardViewController

/**
 * Created by jivie on 3/15/16.
 */
fun <T> StandardViewController.listen(observable: MutableCollection<T>, item: T) {
    onUnmake.add {
        observable.remove(item)

    }

    observable.add(item)
}

fun <T> StandardViewController.bind(observable: MutableCollection<(T) -> Unit>, init: T, item: (T) -> Unit) {
    onUnmake.add {
        observable.remove(item)

    }

    observable.add(item)
    item(init)
}

fun <A, B> StandardViewController.listen(observableA: KObservableInterface<A>, observableB: KObservableInterface<B>, action: (A, B) -> Unit) {

    val itemA = { item: A -> action(item, observableB.get()) }
    val itemB = { item: B -> action(observableA.get(), item) }
    onUnmake.add {
        observableA.remove(itemA)
        observableB.remove(itemB)

    }

    observableA.add(itemA)
    observableB.add(itemB)
}

fun <T> StandardViewController.bind(observable: KObservableListInterface<T>, item: (KObservableListInterface<T>) -> Unit) {
    onUnmake.add {
        observable.onUpdate.add(item)

    }

    item(observable)
    observable.onUpdate.add(item)
}

fun <T> StandardViewController.bind(observable: KObservableInterface<T>, item: (T) -> Unit) {
    onUnmake.add {
        observable.remove(item)

    }

    item(observable.get())
    observable.add(item)
}

fun <A, B> StandardViewController.bind(observableA: KObservableInterface<A>, observableB: KObservableInterface<B>, action: (A, B) -> Unit) {

    val itemA = { item: A -> action(item, observableB.get()) }
    val itemB = { item: B -> action(observableA.get(), item) }
    onUnmake.add {
        observableA.remove(itemA)
        observableB.remove(itemB)

    }

    action(observableA.get(), observableB.get())
    observableA.add(itemA)
    observableB.add(itemB)
}

fun <A, B, C> StandardViewController.bind(
        observableA: KObservableInterface<A>,
        observableB: KObservableInterface<B>,
        observableC: KObservableInterface<C>,
        action: (A, B, C) -> Unit
) {

    val itemA = { item: A -> action(item, observableB.get(), observableC.get()) }
    val itemB = { item: B -> action(observableA.get(), item, observableC.get()) }
    val itemC = { item: C -> action(observableA.get(), observableB.get(), item) }
    onUnmake.add {
        observableA.remove(itemA)
        observableB.remove(itemB)
        observableC.remove(itemC)

    }

    action(observableA.get(), observableB.get(), observableC.get())
    observableA.add(itemA)
    observableB.add(itemB)
    observableC.add(itemC)
}

fun <A, B> StandardViewController.bind(observableA: KObservableInterface<A>, observableB: KObservableInterface<B>, action: () -> Unit) {

    val itemA = { item: A -> action() }
    val itemB = { item: B -> action() }
    onUnmake.add {
        observableA.remove(itemA)
        observableB.remove(itemB)

    }

    action()
    observableA.add(itemA)
    observableB.add(itemB)
}

fun <A, B, C> StandardViewController.bind(
        observableA: KObservableInterface<A>,
        observableB: KObservableInterface<B>,
        observableC: KObservableInterface<C>,
        action: () -> Unit
) {

    val itemA = { item: A -> action() }
    val itemB = { item: B -> action() }
    val itemC = { item: C -> action() }
    onUnmake.add {
        observableA.remove(itemA)
        observableB.remove(itemB)
        observableC.remove(itemC)

    }

    action()
    observableA.add(itemA)
    observableB.add(itemB)
    observableC.add(itemC)
}