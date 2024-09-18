# VelogClone 프로젝트
VelogClone 프로젝트를통해 벨로그의 블로그 기능을 구현했습니다.
이 프로젝트를 통해 사용자 인증, 게시글 관리, 팔로우/팔로잉, 댓글 및 좋아요 같은 핵심 기능을 학습했습니다.

## 기술 스택
- **프론트엔드**: ThymeLeaf
- **백엔드**: Spring Boot, Spring Security, JPA, MySQL
- **기타 도구**: ToastUI, FlexMark, CommonMark

## 설치 및 실행 방법

1. **프로젝트 클론**
```bash
git clone https://github.com/your-repo/VelogClone.git
```


# 프로젝트 기능 목록

<details>
<summary>메인 페이지</summary>

로그인 전

   <img src="https://github.com/user-attachments/assets/e49cad67-6f60-4125-be1a-136e0e3596a3" alt="image" width="50%" height="50%">

로그인 후

   <img src="https://github.com/user-attachments/assets/d3fd7713-b6ef-4016-b789-4749186c4a69" alt="image" width="50%" height="50%">

</details>

<details>
<summary>User 관련</summary>

1. **회원가입**
2. **유저의 역할 관련**
    - Role을 테이블 대신 Enum으로 관리
    - 회원가입 창에서 역할 부여

   <img src="https://github.com/user-attachments/assets/675459dc-b1b9-4c3d-997e-37daca0633fe" alt="image" width="50%" height="50%">

3. **로그인**: 스프링 시큐리티 세션 이용

   <img src="https://github.com/user-attachments/assets/590bd344-d5af-4c20-9249-56da3f679c9f" alt="image" width="50%" height="50%">

4. **회원탈퇴 구현**
5. **프로필 이미지 업로드 구현**

   <img src="https://github.com/user-attachments/assets/4884a5e6-0661-407f-a8bb-7ab72b8a0e1b" width="50%" height="50%">

6. **관리자 페이지 구현** : 관리자는 모든 게시물 댓글에 대한 삭제 권한을 갖고 있음

   <img src="https://github.com/user-attachments/assets/3df518d0-6486-4c9d-8192-4d1a973fc795" alt="image" width="50%" height="50%">

</details>

<details>
<summary>Blog 관련</summary>

1. **블로그 관리 기능**
    - 블로그 이름 수정, 게시글 삭제 및 수정

   <img src="https://github.com/user-attachments/assets/34f90561-271f-410c-afb9-29b64e16fe3f" alt="image" width="50%" height="50%">

2. **블로그 메인 페이지**
    - 게시글 최신순 또는 시리즈별 페이징 

   <img src="https://github.com/user-attachments/assets/537e71cf-0297-402e-aaef-fdf5c2b1f2cf" alt="image" width="50%" height="50%">

</details>

<details>
<summary>Follow 관련</summary>

1. **팔로워 목록 구현**
    - blogId로 검색 시 해당 블로그 Id를 팔로잉 하는 User들을 찾아 팔로워 목록 나타냄
2. **팔로잉 목록 구현**
    - User로 검색 시 해당 유저가 팔로잉 하는 blogId를 찾아 blog에 있는 userId로 팔로잉 목록 나타냄
3. **팔로워, 팔로잉 목록 리다이렉트**
    - 해당 유저의 블로그가 존재하면 해당 유저의 블로그로 리다이렉트 되게 설정

   <img src="https://github.com/user-attachments/assets/f8a69eb5-5910-4e90-a00e-8297886b4248" alt="image" width="50%" height="50%">

</details>

<details>
<summary>Post 관련</summary>

1. **게시글 정보 구현**
    - 저자, 게시일, 내용, 제목 등 구현

   <img src="https://github.com/user-attachments/assets/16747fb8-c42a-46a8-95e3-197914b8c2ea" alt="image" width="50%" height="50%">

2. **게시글 생성 및 수정**
    - 오픈소스 ToastUi 사용하여 구현

   <img src="https://github.com/user-attachments/assets/9f9ce5bc-5286-4f63-a65f-6bc9f760cf76" alt="image" width="50%" height="50%">
   
   <img src="https://github.com/user-attachments/assets/c3b6039b-78ae-4ebc-9583-8e43f7f37cb3" alt="image" width="50%" height="50%">

</details>

<details>
<summary>Comment 관련</summary>

1. **댓글 기능 구현**
    - 댓글 작성 유저의 로그인 아이디, 프로필 이미지, 댓글 내용, 수정 및 삭제 버튼 구현 

</details>

<details>
<summary>Tag 관련</summary>

1. **태그 기능 구현**
    - N : N 관계로 태그 입력폼에 띄어쓰기를 기준으로 구분하여 리스트로 저장하게 구현

</details>

<details>
<summary>Likes 관련</summary>

1. **좋아요 기능 구현**
    - 게시글Id, 로그인Id를 이용해 DB에서 검색하여 중복 좋아요 불가능하게 구현
    - 한번 더 좋아요 버튼 누를 시 좋아요 취소되게 구현

</details>

<details>
<summary>Series 관련</summary>

1. **시리즈 목록 구현**
    - 블로그 메인 페이지에 시리즈 항목 클릭 시 시리즈별 카드 형식으로 시리즈 목록 구현
   <img src="https://github.com/user-attachments/assets/73c95e70-87bc-4d93-a3ae-323e38482400" alt="image" width="50%" height="50%">

2. **시리즈별 포스트 표시**:
    - 해당 카드 클릭 시 시리즈별 포스트가 나타남

</details>

<details>
<summary>ERD</summary>

1. **ERD**
<img src="https://github.com/user-attachments/assets/673c8164-ff0c-4533-b372-e77225377e85" alt="image" width="100%" height="100%">

</details>
