## Это репозиторий проекта "FilmoRate".
*Сервис, который будет работать с фильмами и оценками пользователей, а также возвращать топ-5 фильмов, рекомендованных к просмотру. Теперь ни вам, ни вашим друзьям не придётся долго размышлять, что посмотреть вечером.*

Каркас Spring Boot приложения Filmorate. В дальнейшем сервис будет обогащаться новым функционалом и с каждым спринтом становиться лучше.

---
Добавьте в файл README.md ссылку на файл диаграммы. Если использовать разметку markdown, то схему будет видно непосредственно в README.md.
Там же напишите небольшое пояснение к схеме: приложите примеры запросов для основных операций вашего приложения.

### **База данных:** ###
![Схема таблиц базы данных](/resources/DB.png)

**Пример запросов для фильмов:**

*1. Получить все фильмы*
```SQL
SELECT *
FROM films;
```
*2. Получить фильм по id*
```SQL
SELECT *
FROM films
WHERE film_id = ?;
```
*3. Получить 10 самых популярных фильмов*
```SQL
SELECT * 
FROM films f 
JOIN film_ratings_mpa mpa ON f.rating_mpa_id = mpa.mpa_id
WHERE f.ID IN (SELECT f.ID FROM FILMS f LEFT JOIN FILM_LIKES fl ON f.ID = fl.FILM_ID
GROUP BY f.ID ORDER BY COUNT(fl.USER_ID) DESC LIMIT ?)
```


**Пример запросов для пользователей:**

*1. Получить всех пользователей*
```SQL
SELECT * 
FROM users
```
*2. Получить пользователя по id*
```SQL
SELECT * 
FROM users 
WHERE id = ?
```
*3. Получить всех друзей пользователя*
```SQL
SELECT u.* 
FROM FRIENDS f 
JOIN USERS u ON f.friend_id = u.ID 
WHERE f.user_id = ?
```
*4. Получить общих друзей*
```SQL
SELECT * 
FROM USERS
WHERE id IN (SELECT f1.FRIEND_ID FROM FRIENDS f1 
LEFT JOIN FRIENDS f2 ON f1.FRIEND_ID = f2.FRIEND_ID
WHERE F1.USER_ID = ? AND F2.USER_ID = ?)
```