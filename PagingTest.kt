package kim.uno.mock.util

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PagingTest {

    private lateinit var paging: Paging<Int>

    @Before
    fun setUp() {
        paging = Paging(
            first = 0,
            request = {

            }
        )
    }

    /**
     * 새로고침 완료 전까지 refresh/load 불리더라도 불필요한 중복호출 차단
     */
    @Test
    fun `refreshing distinct`() {
        var count = 0
        paging = Paging(
            first = 0,
            request = {
                count++
            }
        )
        paging.refresh()
        paging.refresh()
        paging.refresh()
        paging.load()
        paging.load()
        paging.load()
        Assert.assertEquals(count, 1)
    }

    /**
     * 로드중에 중복 호출을 막음
     */
    @Test
    fun `first page load`() {
        paging.refresh()
        paging.load()
        paging.load()
        paging.load()
        Assert.assertEquals(paging.isFirstLoad, true)
    }

    /**
     * 로드중에 중복 호출을 막음
     */
    @Test
    fun `first page load success`() {
        paging.refresh()
        paging.load()
        paging.load()
        paging.load()
        paging.success(next = 1, endOfList = true)
        Assert.assertEquals(paging.isFirstLoad, true)
    }

    @Test
    fun `load more page`() {
        paging.refresh()
        paging.success(next = 1, endOfList = false)
        paging.load()
        Assert.assertEquals(paging.isFirstLoad, false)
    }

    /**
     * isEnded true 이면 불필요한 호출을 막음
     */
    @Test
    fun `no more`() {
        paging.refresh()
        paging.success(next = 1, endOfList = true)
        Assert.assertEquals(paging.isEndOfList, true)
    }

}
