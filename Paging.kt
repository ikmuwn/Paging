package kim.uno.mock.util

class Paging<T>(
    val first: T,
    val request: (next: T) -> Unit
) {

    var next: T = first
    var state = State.NONE

    val isProgress: Boolean
        get() = state == State.PROGRESS

    val isEndOfList: Boolean
        get() = state == State.END_OF_LIST

    var isFirstLoad = true

    fun refresh() {
        if (isProgress) {
            return
        }

        reset()
        load()
    }

    fun reset() {
        state = State.NONE
        next = first
        isFirstLoad = true
    }

    fun load() {
        if (isProgress || isEndOfList) {
            return
        }

        if (state != State.NONE) {
            isFirstLoad = false
        }

        state = State.PROGRESS
        request(next)
    }

    fun success(next: T, endOfList: Boolean = false) {
        this.next = next
        state = if (endOfList) {
            State.END_OF_LIST
        } else {
            State.IDLE
        }
    }

    fun error() {
        state = State.ERROR
    }

    enum class State {
        NONE, IDLE, PROGRESS, END_OF_LIST, ERROR
    }

}