# TravelDiaryProject
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
ex) **articledetail_title**: title of ArticleDetailFragment
**feedback_explanation**: feedback explanation in FeedbackFragment
**feedback_namehint**: hint of name field in FeedbackFragment
**all_done**: generic “done” string



### ID
For IDs, <WHAT> is the class name of the xml element it belongs to. Next is the screen the ID is in, followed by an optional description to distinguish similar elements in one screen.
**[what]_[where]_[description]**
ex) **tablayout_main** -> TabLayout in MainActivity
**imageview_menu_profile** -> profile image in custom MenuView
**textview_articledetail_title** -> title TextView in ArticleDetailFragment

[name 작성방법 출처](https://jeroenmols.com/blog/2016/03/07/resourcenaming/)