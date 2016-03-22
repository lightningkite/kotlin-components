package com.lightningkite.kotlincomponents.observable

/**
 * Allows you to observe the changes to a list.
 * Created by josep on 9/7/2015.
 */
interface KObservableListInterface<E> : MutableList<E> {

    val onAdd: MutableSet<(E, Int) -> Unit>
    val onChange: MutableSet<(E, Int) -> Unit>
    val onUpdate: KObservableInterface<KObservableListInterface<E>>
    val onReplace: MutableSet<(KObservableListInterface<E>) -> Unit>
    val onRemove: MutableSet<(E, Int) -> Unit>

}