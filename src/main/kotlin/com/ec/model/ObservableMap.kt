package com.ec.model

import java.util.function.Consumer
import java.util.function.Predicate

data class ObservableMapAction<K, V>(
    val type: ObservableMapActionType,
    val key: K,
    val value: V?
)

enum class ObservableMapActionType {
    PUT,
    REMOVE,
    GET,
}

class ObservableMap<K, V>: MutableMap<K, V>, Observable<ObservableMapAction<K, V>>() {

    private val mapper: MutableMap<K, V> = mutableMapOf()

    override val size: Int
        get() = mapper.size
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = mapper.entries
    override val keys: MutableSet<K>
        get() = mapper.keys
    override val values: MutableCollection<V>
        get() = mapper.values

    override fun containsKey(key: K): Boolean {
        return mapper.containsKey(key)
    }

    override fun containsValue(value: V): Boolean {
        return mapper.containsValue(value)
    }

    override fun isEmpty(): Boolean {
        return mapper.isEmpty()
    }

    override fun get(key: K): V? {
        val value = mapper[key]
        onNext(ObservableMapAction(ObservableMapActionType.GET, key, value))
        return value
    }

    override fun clear() {
        mapper.forEach { (k, v) ->
            onNext(ObservableMapAction(ObservableMapActionType.REMOVE, k ,v))
        }
        return mapper.clear()
    }

    override fun put(key: K, value: V): V? {
        mapper[key] = value
        onNext(ObservableMapAction(ObservableMapActionType.PUT, key, value))
        return value
    }

    override fun putAll(from: Map<out K, V>) {
        mapper.putAll(from)
        from.forEach { (k, v) ->
            onNext(ObservableMapAction(ObservableMapActionType.PUT, k, v))
        }
    }

    override fun remove(key: K): V? {
        val remove =  mapper.remove(key)
        onNext(ObservableMapAction(ObservableMapActionType.REMOVE, key, remove))
        return remove
    }

    override fun dispose() {

    }
}