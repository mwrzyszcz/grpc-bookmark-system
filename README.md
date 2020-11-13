## Bookmarks System via gRPC

---

Stack:

* JDK 11
* Maven
* gRPC
* Protobuf plugin
* Jsoup

### How to use

1. Clone repository `$ git clone https://github.com/mwrzyszcz/grpc-bookmark-system.git`
2. Run application

* OpenJDK 11
* Maven 3+

`$ mvn clean install`

By default server starting on `8080` port

3. Use/install CLI client with RPC support

I used [Evans CLI](https://github.com/ktr0731/evans)

4. Run CLI

#### Example

In CLI folder `$ .\evans.exe --cli -r repl -p 8080`

then you get:

```textmate
  ______
 |  ____|
 | |__    __   __   __ _   _ __    ___
 |  __|   ' ' / /  / _. | | '_ '  / __|
 | |____   ' V /  | (_| | | | | | '__ ,
 |______|   '_/    '__,_| |_| |_| |___/

 more expressive universal gRPC client


proto.BookmarkService@127.0.0.1:8080>
```

5. Check Bookmark Service API

`$ show service`

then you get:

```text
+--------------------+-------------+-----------------+------------------+
|      SERVICE       |     RPC     |  REQUEST TYPE   |  RESPONSE TYPE   |
+--------------------+-------------+-----------------+------------------+
| BookmarkController | addBookmark | BookmarkRequest | BookmarkResponse |
+--------------------+-------------+-----------------+------------------+
```

6. Use `addBookmark` method to create new bookmark

`$ call addBookmark`

then provide arguments

```textmate
url (TYPE_STRING) => https://revdebug.com/
tags (TYPE_STRING) => reverse-debugging work-related java csharp javascript python
```

then check the response

```json5
{
  "status": "OK. Bookmark added."
}
```

7. Check `bookmarks.md` file in `target` folder

We've got markdown file as follows:

```markdown
## Error monitoring that works | RevDeBug

* https://revdebug.com/
* reverse-debugging, work-related, java, csharp, javascript, python
* RevDeBug's solution allows engineering teams to record & replay software execution in local and remote environments. Resole your errors in minutes not days.
```

8. Wrong URL in parameter response

Provide wrong URL in parameters

E.g.

```textmate
url (TYPE_STRING) => https://revdebug.pl
tags (TYPE_STRING) => tag1 tag2 tag3
{
  "status": "Cannot connect with given URL"
}
```

---

## Notes

###### It's only simple example of gRPC service
