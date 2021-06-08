package com.ec.model

import reactor.core.Disposable
import reactor.core.publisher.Flux

class ObservableObject<T>(observable: Flux<T>): Observable<T>() where T: Any {

    private val disposer: Disposable = observable.subscribe {
        onNext(it)
    }

    override fun dispose() {
        disposer.dispose()
    }

}