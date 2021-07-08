package com.ec.model

data class ObservableListAction<K>(
    val type: ObservableListActionType,
    val value: K
)

enum class ObservableListActionType {
    ADD,
    REMOVE
}

class ObservableList<K>: Set<K>, Observable<ObservableListAction<K>>() {

    private val sets = hashSetOf<K>()

    override val size: Int
        get() = sets.size

    fun clear() {
        sets.forEach {
            onNext(ObservableListAction(ObservableListActionType.REMOVE, it))
        }
        sets.clear()
    }

    fun remove(element: K) {
        sets.remove(element)
        onNext(ObservableListAction(ObservableListActionType.REMOVE, element))
    }

    fun addAll(element: Collection<K>) {
        sets.addAll(element)
        element.forEach {
            onNext(ObservableListAction(ObservableListActionType.ADD, it))
        }
    }

    fun add(element: K) {
        sets.add(element)
        onNext(ObservableListAction(ObservableListActionType.ADD, element))
    }

    override fun contains(element: K): Boolean {
        return sets.contains(element)
    }

    override fun containsAll(elements: Collection<K>): Boolean {
        return sets.containsAll(elements)
    }

    override fun isEmpty(): Boolean {
        return sets.isEmpty()
    }

    override fun iterator(): Iterator<K> {
        return sets.iterator()
    }

}