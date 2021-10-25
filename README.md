# Comments API

## Features
- [x] Create / Read / Update / Acknowledge REST API
- [x] Comment threads

## Usage

`GET` to `/comment` to load comments. Takes optional boolean parameters:
* `thread`: Loads nested comment threads if true
* `acked`: Shows previously acknowledged comments if true

`POST` to `/comment` to create a new comment. Example body:

```json
{
	"userId": "1",
	"text": "hello"
}
```

`POST` to `/comment/reply` to reply to a comment. Example body:

```json
{
	"userId": "1",
	"text": "hello",
	"parentId": "1"
}
```

`PUT` to `/comment` to edit a comment (also how you acknowledge). Example body:

```json
{
	"id": "1",
	"userId": "1",
	"text": " friend"
}
```


## Does not include
- Linting: My local IntelliJ settings aren't playing nice with the linter
and wildcard imports, so I am not using linting.
- Text search: JetBrain's Kotlin Expose database abstraction library doesn't
have great documentation on `like` operations for string searches, so this
is not fully implemented.
- Multiple replies to a single comment: This is pretty straightforward
to implement when the datastore is Postgres, but I used H2 for simplicity
- Emoji reactions: I just didn't get to this. There's some investigation
needed around language formats and whatnot to allow the characters without
being a security vulnerability
- Input sanitization: I did some escaping, but I couldn't find great
libraries for Java (maybe I wasn't looking in the right places)
- Multimedia attachments: I didn't have time to get into this. I would
design it by allowing each Comment to store a list of ids or URLs or
something and store the content itself in S3. This becomes tricky if you
ever change hosting, say S3 to Cloudinary, but that's out of scope for now.