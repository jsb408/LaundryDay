# Laundry Day

세탁소 정보 공유 플랫폼

- 진행기간 : 2020. 11. 23 ~ 2020. 12. 30
- 사용기술 : Android Studio, Kotlin, okhttp, Firebase, Firestore, Realm, Naver Map API, TedClustering

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116541959-0c4b1d00-a927-11eb-90d9-f9ceed413401.jpg"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116541970-0ead7700-a927-11eb-8ae0-a329c901ada4.jpg"/></p>

## 서비스 소개

- Laundry Day는 쉽게 주변 세탁소 정보를 탐색하고 정보를 공유할 수 있는 플랫폼입니다.
- 사용자의 현재 위치를 기반으로 같은 행정구역에 위치한 세탁소의 위치들이 표시됩니다.
- 주변 세탁소의 상세 정보 및 다른 회원들의 세탁소 리뷰도 볼 수 있습니다.
- 지도에 나와있지 않은 세탁소가 있다면 직접 정보를 등록할 수 있습니다.
- 자주가는 세탁소를 즐겨찾기해두고 쉽게 상세 페이지로 찾아갈 수 있습니다.

## 상세 기능 소개

### 1. 주변 세탁소

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116542166-4fa58b80-a927-11eb-9767-82bdf6d71537.jpg"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116542193-56cc9980-a927-11eb-86c4-3cfacf1c0484.jpg"/></p>

- 네이버 지도를 기반으로 공공 API 정보 중 세탁업 공공데이터를 사용해 세탁소 정보를 받아옵니다.
- 받아온 세탁소 정보를 네이버 지도 위에 마커로 표시합니다.
- 우측 상단의 목록 버튼을 터치하면 세탁소 목록을 리스트 형태로 조회할 수 있습니다.

### 1-1. 세탁소 상세 정보

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116542402-93989080-a927-11eb-9039-9920204915fe.jpg"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116542421-97c4ae00-a927-11eb-8dbd-4859cb856875.jpg"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116542439-9b583500-a927-11eb-8f69-e1e46603925c.jpg"/></p>

- 마커를 터치하면 해당 위치의 세탁소 정보가 간략하게 표시됩니다.
- 표시 된 정보를 터치하거나 리스트에서 세탁소를 터치하면 세탁소 상세 정보가 표시됩니다.
- 세탁소의 지도를 터치하면 해당 세탁소를 중심으로 한 지도가 표시됩니다.
- 세탁소 상세 지도에서는 확대, 축소만 가능하며 이동이 불가능합니다.

### 1-2. 세탁소 전화 연결

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116542698-e70ade80-a927-11eb-9ff5-344bd8d7a9de.jpg"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116542728-f12cdd00-a927-11eb-811b-c5dba7bffd45.jpg"/></p>

- 요약 정보의 전화 아이콘을 터치하면 전화 앱으로 연결됩니다.
- 상세 정보에서 세탁소의 전화번호를 터치하면 전화 앱으로 연결됩니다.

### 1-3. 세탁소 길 찾기

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116542965-3ea94a00-a928-11eb-8c33-82d9c16b6178.jpg"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116542978-44069480-a928-11eb-8aca-6552910f6b5f.jpg"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116542985-45d05800-a928-11eb-86d8-82abf604a510.jpg"/></p>

- 요약 정보의 지도 아이콘을 터치하면 외부 지도 앱의 길찾기로 연결됩니다.
- 현재 위치를 기반으로 해당 세탁소까지 가는 길을 안내합니다.
- 네이버맵, 구글맵스를 지원합니다.

### 1-4. 즐겨찾기 등록

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116543122-6f897f00-a928-11eb-87e5-7ba44d0b7f2d.jpg"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116543135-731d0600-a928-11eb-8f75-b7665610618b.jpg"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116543149-787a5080-a928-11eb-8056-878c73a3e0d9.jpg"/></p>


- 세탁소 요약 정보, 상세 정보, 목록 등에서 하트 아이콘을 터치하면 즐겨찾기 등록 및 해제가 가능합니다.
- 즐겨찾기로 등록된 세탁소는 마이페이지의 찜한 세탁소에서 관리할 수 있습니다.

### 2. 마이페이지

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116543227-95168880-a928-11eb-8177-1dc51a6c254a.jpg"/></p>

- 좌측 상단의 사람 아이콘을 터치하면 좌측 드로어를 통해 프로필이 표시됩니다.
- 최근 찜한 세탁소 1개, 최근 본 세탁소 3개를 볼 수 있습니다.
- 각각을 터치하며 해당 세탁소의 상세 페이지로 넘어갑니다.

### 2-1. 찜한 세탁소

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116543298-a8c1ef00-a928-11eb-8338-cec9f132cf5e.jpg"/></p>

- 찜한 세탁소 옆의 숫자를 터치하면 전체 찜한 세탁소 목록을 확인할 수 있습니다.
- 찜한 시각을 기준으로 내림차순 정렬되어 표시됩니다.
- 각각의 세탁소를 터치하여 해당 세탁소의 상세 정보를 확인할 수 있습니다.
- 전화 아이콘, 지도 아이콘을 터치해 전화 연결 및 길찾기가 가능합니다.

### 2-2. 최근 본 세탁소

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116543361-bd9e8280-a928-11eb-9492-e2b4ef82dfd1.jpg"/></p>

- 최근 본 세탁소 옆의 숫자를 터치하면 최근 본 세탁소의 목록을 확인할 수 있습니다.
- 세탁소를 본 시각을 기준으로 내림차순 정렬되어 표시됩니다.
- 이미 본 세탁소를 다시 볼 경우 최상단으로 갱신되어 표시됩니다.
- 각각의 세탁소를 터치하여 해당 세탁소의 상세 정보를 확인할 수 있습니다.
- 전화 아이콘, 지도 아이콘을 터치해 전화 연결 및 길찾기가 가능합니다.

### 3. 로그인

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116543442-d5760680-a928-11eb-950c-b6e888ac2c5e.jpg"/></p>

- 마이페이지의 상단 박스를 터치하여 로그인 메뉴에 진입할 수 있습니다.
- 로그인 시 로컬에 저장된 찜 목록 정보가 서버와 동기화됩니다.

### 3-1. 회원가입

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116543532-f0e11180-a928-11eb-88d3-8c75c204094b.jpg"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116543545-f4749880-a928-11eb-8342-00b53c70dcb5.jpg"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116543552-f63e5c00-a928-11eb-9ac7-08f6a508dfc7.jpg"/></p>


- 회원가입 버튼으로 가입할 수 있습니다.
- 양식에 맞게 모두 적으면 가입하기 버튼이 활성화되고 회원정보 등록이 가능합니다.

### 3-2. 이메일로 로그인

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116543714-2b4aae80-a929-11eb-83db-eea50bee7293.jpg"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116543730-300f6280-a929-11eb-89bd-b2f4aef323b8.jpg"/></p>

- Firebase의 Authentification을 사용하여 이메일로 로그인이 가능합니다.
- 로그인 완료 시 메인화면으로 넘어갑니다.

### 3-3. 구글 로그인

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116543912-6d73f000-a929-11eb-991b-e462b5626c89.jpg"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116543954-795fb200-a929-11eb-9103-26960b846c81.jpg"/></p>

- Firebase의 Authentification을 사용하여 구글 간편 로그인이 가능합니다.
- 이메일로 가입한 계정과 같은 이메일로 구글 로그인을 할 경우 이메일로 가입된 계정이 구글 간편 계정으로 변경됩니다.
- 로그인 완료 시 메인화면으로 넘어갑니다.

### 4. 리뷰 작성

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116544058-a1e7ac00-a929-11eb-96d5-0cb7de4fe6d8.jpg"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116544073-a613c980-a929-11eb-9e54-d593977fe976.jpg"/></p>

- 세탁소 상세 정보 페이지에서 리뷰를 작성할 수 있습니다.
- 리뷰 작성은 로그인이 필요하며 로그인 상태가 아닐 경우 로그인 다이얼로그가 표시됩니다.

### 4-1. 리뷰 내용 작성

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116544254-da878580-a929-11eb-920f-07d2b499482a.jpg"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116544259-dce9df80-a929-11eb-881c-28789fc1cfc8.jpg"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116544267-df4c3980-a929-11eb-8d1a-4ea82f547b81.jpg"/></p>

- 로그인 상태에서 리뷰작성하기 버튼을 터치하면 리뷰 작성 페이지로 넘어갑니다.
- 별점과 리뷰 내용을 입력하고 작성하기 버튼을 터치하면 리뷰가 등록됩니다.

### 4-2. 내가 쓴 리뷰

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116544407-0e62ab00-a92a-11eb-8af4-7da7fd15beb0.jpg"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116544413-102c6e80-a92a-11eb-913f-3cfdc7d2709a.jpg"/></p>

- 로그인 한 상태에서 마이페이지 진입 시 내가 쓴 리뷰 버튼이 활성화됩니다.
- 내가 쓴 리뷰 버튼을 터치하면 내가 썼던 리뷰 기록들이 표시됩니다.
- 기록은 리뷰 작성 날짜를 기준으로 내림차순 정렬되어 표시됩니다.

### 5. 세탁소 제보

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116544548-423dd080-a92a-11eb-83b8-d3d0169567a1.jpg"/></p>

- 로그인 한 상태에서 마이페이지 진입 시 세탁소 제보 버튼이 활성화됩니다.
- 세탁소 제보 버튼을 터치하면 세탁소 제보가 가능합니다.

### 5-1. 제보 작성

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116544620-571a6400-a92a-11eb-97bf-371f4a995cff.jpg"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116544631-5aadeb00-a92a-11eb-94bc-89e86ec8e918.jpg"/></p>

- 제보 양식에 맞게 내용을 입력하면 제출하기 버튼이 활성화됩니다.
- 제출한 제보는 Firestore에서 확인 후 지도에 등록할 수 있습니다.

### 5-2. 세탁소 등록

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116544736-7addaa00-a92a-11eb-9a88-b849aff23483.png"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116544739-7d400400-a92a-11eb-92d0-5455d3417910.png"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116544746-7dd89a80-a92a-11eb-8d1f-f623ac32bdc9.jpg"/></p>

- Firestore에서 세탁소 콜렉션을 필터한 후 해당 세탁소를 찾아 정보가 정확히 입력되었는지 확인합니다. (좌표값이 들어가있는지 확인)
- 정보가 정확하다면 registered를 true로 바꿔줍니다.
- 앱 내 지도에 마커가 추가된 것을 확인할 수 있습니다.

## 보완 사항

- 카카오, 네이버, 애플 로그인 추가
- 코인세탁소, 일반세탁소 구분

## 기타 사항

- 네이버맵 클러스터 구현

<p align="center"><img width="30%" src="https://user-images.githubusercontent.com/55052074/116544888-a82a5800-a92a-11eb-8663-d5117e93d66f.jpg"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116544904-ad87a280-a92a-11eb-8518-0ec695495abc.jpg"/> <img width="30%" src="https://user-images.githubusercontent.com/55052074/116544907-aeb8cf80-a92a-11eb-9340-1c2889667626.jpg"/></p>

줌인, 줌아웃 정도에 따라 주변 세탁소들이 합쳐져서 보이는 클러스터를 구현했습니다. 클러스터 터치 시 해당 지점을 기준으로 지도 이동 후 줌인됩니다.
