# TravelDiaryProject
### 개발환경
- SDK version: 31 api


### Layout naming 규칙
|구분|ex|
|:---:|:---:|
|Activity|activity_main|
|Fragment|fragment_favorite|
|Dialog|dialog_login|
|List Item View|item_bus|
|ProgressBar|pb|


### Drawable nameing
|구분|Prefix|ex|
|:---:|:---:|:---:|
|Button|btn_|btn_login|
|Icon|ic_|ic_like_feed|
|Menu|menu_|menu_more|
|CheckBox|chb_|chb_alarm|
|Tab|ic_tab|ic_tab_something|

Button의 경우는 명확하게 구분해서



### Strings
**[Where]_[Description]**

ex)
- **articledetail_title**: title of ArticleDetailFragment
- **feedback_explanation**: feedback explanation in FeedbackFragment
- **feedback_namehint**: hint of name field in FeedbackFragment
- **all_done**: generic “done” string



### ID
For IDs, <WHAT> is the class name of the xml element it belongs to. Next is the screen the ID is in, followed by an optional description to distinguish similar elements in one screen.

**[what]_ [where]_[description]**

ex)
- **tablayout_main** -> TabLayout in MainActivity
- **imageview_menu_profile** -> profile image in custom MenuView
- **textview_articledetail_title** -> title TextView in ArticleDetailFragment

[name 작성방법 출처](https://jeroenmols.com/blog/2016/03/07/resourcenaming/)


<br>

### DB 저장 방식 (firebase 사용 계획)
=> DB 관리 전용 class를 생성하여 별도 관리.
1. Authentication: 사용자 이메일정보를 수집
2. RealTime Database: 회원가입 시 입력한 정보와 게시물 id. 이때 사용자 uid를 컬렉션명으로 함.<br>
   -> user > [UID] > info > [사용자 정보들] <br>
   |Field|Type|ect|
   |:---:|:---:|:---:|
   |idToken|String|UID. 기본키처럼 사용.|
   |userEmail|String|사용자 이메일|
   |userNickName|String|사용자 nickname|

3. firestore Database: 게시물 업로드 내용을 저장함. 이때 이름은 게시물별 id를 생성하여 저장.
   |Field|Type|ect|
   |:---:|:---:|:---:|
   |boardID|String|고유 board ID값. 기본키값처럼 사용|
   |con|String|작성한 게시글 내용. html tag형태 그대로 저장됨.|
   |date|String|여행일|
   |hashTag|Array|선택한 hashTag 저장.|
   |title|String|제목|
   |uploadDate|String|업로드한 날짜 + 시간. 업데이트 한 날짜는 별도 생성.|
   |updateDate|String|최종 수정일|
   |userToken|String|사용자 UID 값|
   |route|Array|경로 입력|
   |bookmark|Array|북마크 한 user의 UID 저장|

5. Storage: image 경로를 저장. (사용할 logo도 저장하여 불러오기) <br>
   -> image > [docId] > [img 경로]  (mainImage도 함께 docId 내 저장. 이름은 mainImg로 저장.) <br>
   -> logo > [image]

<br>

<br>


### memory leaked test
```
  dependencies {
    ...
  
    debugImplementation 'com.squareup.leakcanary:leakcanary-android:2.9'
  
    ...
  }
```