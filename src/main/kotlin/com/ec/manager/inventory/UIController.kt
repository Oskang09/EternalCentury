package com.ec.manager.inventory

class UIController(
    private var state: Map<String, Any>,
    private val update: (Map<String, Any>) -> Unit,
    val page: Int,
    private val setPage: (Int) -> Unit,
    private var hasNext: Boolean = false,
) {
    private var nextState = state.toMutableMap()

    fun <T> getStateByKey(key: String): T {
        return state[key]!! as T
    }

    fun <T> getStateByKeyOrDefault(key: String, default:T ): T {
        val value = state[key] ?: return default
        return value as T
    }

    fun getState(): Map<String, Any> {
        return state
    }

    fun setStateByKey(key: String, value: Any) {
        nextState[key] = value
    }

    fun setState(values: Map<String, Any>) {
        nextState = values.toMutableMap()
    }

    fun refreshState() {
        update(nextState.toMap())
    }

    fun previous() {
        if (page > 1) {
            setPage(page - 1)
            refreshState()
        }
    }

    fun next() {
        if (hasNext) {
            setPage(page + 1)
            refreshState()
        }
    }

    fun setHasNext(hasNext: Boolean) {
        this.hasNext = hasNext
    }

}