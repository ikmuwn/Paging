# Paging
- 범용 페이지네이션 유틸리티
- 다양한 자료형의 페이지 인디케이터 지원 (int, String, nullable)
- 다양한 방식의 페이지 처리방식 지원 (page 1씩 증가, next page hash string 처리, limit offset 20씩 증가 등)
- 네트워크 연동시 중복 요청 방지
- 간편한 초기화, 더 불러오기 기능 지원
- [RecyclerViewViewModel#24](https://github.com/ikmuwn/Mock-android/blob/599b2181761df2ac88a91ecfc78162c73d5bf1d2/app/src/main/java/kim/uno/mock/ui/recyclerview/RecyclerViewViewModel.kt#L24) Paging 적용 모델
- [PagingFragment#15](https://github.com/ikmuwn/Mock-android/blob/599b2181761df2ac88a91ecfc78162c73d5bf1d2/app/src/main/java/kim/uno/mock/ui/paging/PagingFragment.kt#L15) 더 불러오기 호출
- [Mock-android](https://github.com/ikmuwn/Mock-android)

## Use

- 한 페이지를 20개씩 로드하는 샘플
- 아이템 갯수의 10개 이전의 row에 도달했을때 더 불러오기 함수를 호출할 수 있도록 함

  ```kotlin
  val adapter = RecyclerViewAdapter.Builder()
        .addHolder(holder = MockHolder::class)
        .addBinder { holder, position, itemCount ->
            if (position == itemCount - 10) {
                // 남은 갯수가 10개 일때 더보기 함수 호출
                // viewModel에서 paging.load() 함수를 호출하도록 함
                viewModel.loadMore()
            }
        }
        .build()
  
  recyclerView.adapter = adapter
  ```
  

- request에 전달되는 delegate를 통해 요청해야할 페이지의 인디케이터를 전달받을 수 있도록 함

  ```kotlin
  val paging by lazy {
  
      // 새로고침시 사용할 첫번째 페이지 인디케이터를 null로 지정해서
      // null인경우 가장 첫번째 데이터를 가져오도록 하고
      // 첫번째 리스트의 마지막 항목에서 다음 페이지 인디케이터를 paging.success(next, endOfList)를 통해 전달한다
      Paging<Long?>(
          first = null,
          request = this::getMockList
      )
  }
  ```

- Paging이 대신 호출해줄 getMockList 함수

  ```kotlin
  fun getMockList(postTime: Long?) {
      viewModelScope.launch {
          try {
              val mockList = dataRepository.getMockList(
                  size = 20,
                  postTime = postTime
              )

              if (paging.isFirstLoad) {
                  _mockList.value = mockList
              } else {
                  val notificationsMerge = ArrayList(_mockList.value!!)
                  notificationsMerge.addAll(mockList)
                  _mockList.value = notificationsMerge
              }

              // 페이징 처리가 성공시 다음 페이지의 인디케이터를 저장하고 
              // 페이지의 끝을 알 수 있다면 endOfList를 true로 전달하여 불필요한 request가 발생하지 않도록 함
              paging.success(
                  next = mockList.last().postTime,
                  endOfList = mockList.size < 20
              )
          } catch (e: Exception) {
              // 페이징 처리가 실패한 경우 불필요한 request가 발생하지 않도록 함
              paging.error()
          }
      }
  }
  ```
