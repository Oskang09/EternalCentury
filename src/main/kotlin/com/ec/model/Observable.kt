package com.ec.model

import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate
import kotlin.concurrent.schedule

open class Observable<T> where T: Any {

    private val subscriber: MutableList<Consumer<T>> = mutableListOf()
    private val subscriberOnce: MutableList<Consumer<T>> = mutableListOf()
    private val predicateSubscriber: MutableMap<Predicate<T>, Consumer<T>> = mutableMapOf()
    private val predicateSubscriberOnce: MutableMap<Predicate<T>, Consumer<T>> = mutableMapOf()

    open fun dispose() {

    }

    fun onNext(it: T) {
        predicateSubscriber.keys
            .filter { predicate -> predicate.test(it) }
            .map { mapper -> predicateSubscriber[mapper] }
            .forEach { consumer ->
                consumer!!.accept(it)
            }

        predicateSubscriberOnce.keys
            .filter { predicate -> predicate.test(it) }
            .map { mapper -> predicateSubscriberOnce.remove(mapper) }
            .forEach { consumer ->
                consumer!!.accept(it)
            }

        subscriber.forEach { consumer ->
            consumer.accept(it)
        }

        val onceIterator = subscriberOnce.iterator()
        while (onceIterator.hasNext()) {
            val consumer = onceIterator.next()
            consumer.accept(it)
            onceIterator.remove()
        }
    }

    fun subscribe(predicate: Predicate<T>? = null, consumer: Consumer<T>): () -> Unit {
        if (predicate == null) {
            subscriber.add(consumer)
            return {
                subscriber.remove(consumer)
            }
        }
        predicateSubscriber[predicate] = consumer
        return {
            predicateSubscriber.remove(predicate)
        }
    }

    fun subscribeOnce(predicate: Predicate<T>? = null, consumer: Consumer<T>) {
        if (predicate == null) {
            subscriberOnce.add(consumer)
            return
        }
        predicateSubscriberOnce[predicate] = consumer
    }

    fun subscribeOnceWithTimeout(predicate: Predicate<T>? = null, timeout: Long = 30000L, onTimeout: () -> Unit, consumer: Consumer<T>) {
        val timerTask = Timer("", false).schedule(timeout) {
            if (predicate == null) {
                subscriberOnce.remove(consumer)
            } else {
                predicateSubscriberOnce.remove(predicate)
            }
            onTimeout()
        }

        if (predicate == null) {
            subscriberOnce.add {
                timerTask.cancel()
                consumer.accept(it)
            }
        } else {
            predicateSubscriberOnce[predicate] = Consumer {
                timerTask.cancel()
                consumer.accept(it)
            }
        }
    }

    fun onDestroy() {
        predicateSubscriber.clear()
        predicateSubscriberOnce.clear()
        subscriber.clear()
        subscriberOnce.clear()
        dispose()
    }

}