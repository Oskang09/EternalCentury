package com.ec.model

import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate
import kotlin.concurrent.schedule

abstract class Observable<T> where T: Any {

    private val subscriber: MutableList<Consumer<T>> = mutableListOf()
    private val subscriberOnce: MutableList<Consumer<T>> = mutableListOf()
    private val predicateSubscriber: MutableMap<Predicate<T>, Consumer<T>> = mutableMapOf()
    private val predicateSubscriberOnce: MutableMap<Predicate<T>, Consumer<T>> = mutableMapOf()
    abstract fun dispose()

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

        subscriberOnce
            .map { mapper ->
                subscriberOnce.remove(mapper)
                return@map mapper
            }
            .forEach { consumer ->
                consumer.accept(it)
            }
    }

    fun subscribe(predicate: Predicate<T>?, consumer: Consumer<T>): () -> Unit {
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

    fun subscribeOnce(predicate: Predicate<T>?, consumer: Consumer<T>) {
        if (predicate == null) {
            subscriberOnce.add(consumer)
            return
        }
        predicateSubscriberOnce[predicate] = consumer
    }

    fun subscribeOnceWithTimeout(predicate: Predicate<T>?, timeout: Long = 30000L, onTimeout: () -> Unit, consumer: Consumer<T>) {
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