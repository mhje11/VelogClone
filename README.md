# 프로젝트 기능 목록

## 메인 페이지
로그인 전
   ![image](https://github.com/user-attachments/assets/e49cad67-6f60-4125-be1a-136e0e3596a3)

로그인 후
  ![image](https://github.com/user-attachments/assets/d3fd7713-b6ef-4016-b789-4749186c4a69)


## User 관련
1. **회원가입**
   ![image](https://github.com/user-attachments/assets/675459dc-b1b9-4c3d-997e-37daca0633fe)

3. **로그인**: 스프링 시큐리티 세션 이용
![image](https://github.com/user-attachments/assets/590bd344-d5af-4c20-9249-56da3f679c9f)

4. **회원탈퇴 구현**
5. **유저의 역할 관련**
    - Role을 테이블 대신 Enum으로 관리
    - 회원가입 창에서 역할 부여
6. **관리자 페이지 구현** : 관리자는 모든 게시물 댓글에 대한 삭제 권한을 갖고 있음
7. **프로필 이미지 업로드 구현**

## Blog 관련
1. **블로그 관리 기능**
    - 블로그 이름 수정, 게시글 삭제 및 수정 
2. **블로그 메인 페이지**
    - 게시글 최신순 또는 시리즈별 페이징 

## Follow 관련
1. **팔로워 목록 구현**
    - blogId로 검색 시 해당 블로그 Id를 팔로잉 하는 User들을 찾아 팔로워 목록 나타냄
2. **팔로잉 목록 구현**
    - User로 검색 시 해당 유저가 팔로잉 하는 blogId를 찾아 blog에 있는 userId로 팔로잉 목록 나타냄
3. **팔로워, 팔로잉 목록 리다이렉트**
    - 해당 유저의 블로그가 존재하면 해당 유저의 블로그로 리다이렉트 되게 설정

## Post 관련
1. **게시글 정보 구현**
    - 저자, 게시일, 내용, 제목 등 구현
2. **게시글 생성 및 수정**
    - 오픈소스 ToastUi 사용하여 구현
3. **게시물 이미지 포함**
    - ToastUi가 마크다운 형식으로 내용에 저장 후 별도로 엔티티에서 관리
4. **게시글 저장 시**
    - 마크다운 형식을 HTML로 변환 (commonMark 이용)
5. **게시글 수정 시**
    - 저장된 content(html)을 마크다운으로 변환 (flexMark 이용)
6. **이미지 크기 조정**
    - ToastUi는 이미지 크기 조정 불가능하여 thumbnailator 이용해 크기 적절히 고정

## Comment 관련
1. **댓글 기능 구현**
    - 댓글 작성 유저의 로그인 아이디, 프로필 이미지, 댓글 내용, 수정 및 삭제 버튼 구현 

## Tag 관련
1. **태그 기능 구현**
    - N : N 관계로 태그 입력폼에 띄어쓰기를 기준으로 구분하여 리스트로 저장하게 구현

## Likes 관련
1. **좋아요 기능 구현**
    - 게시글Id, 로그인Id를 이용해 DB에서 검색하여 중복 좋아요 불가능하게 구현
    - 한번 더 좋아요 버튼 누를 시 좋아요 취소되게 구현

## Series 관련
1. **시리즈 목록 구현**
    - 블로그 메인 페이지에 시리즈 항목 클릭 시 시리즈별 카드 형식으로 시리즈 목록 구현
2. **시리즈별 포스트 표시**:
    - 해당 카드 클릭 시 시리즈별 포스트가 나타남