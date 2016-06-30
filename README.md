Syncano Android library - offline feature example
=================================================

This example demonstrates how to use Syncano library to store data in a device local storage.
It is a simple chat app. Every user see the same. It's possible to see received messages when internet connection drops and application was turned off. It is also possible to write messages offline and send them when connection will be back.

![screenshot](https://raw.githubusercontent.com/stasheq/syncano-offline-example/master/readme/screenshot.png)

Starting the app
----------------
Create a "messages" class on your Syncano instance, with one item in schema: type - text, name - text.
```bash
curl -X "POST" "https://api.syncano.io/v1.1/instances/<INSTANCE>/classes/" -H "X-API-KEY: <ACCOUNT_KEY>" -H "Content-Type: application/json" -d '{"name":"messages","schema":[{"type":"text","name":"text"}]}'
```

Create a channel "messages_changes" on your Syncano instance, for realtime informing about new messages.
```bash
curl -X "POST" "https://api.syncano.io/v1.1/instances/<INSTANCE>/channels/" -H "X-API-KEY: <ACCOUNT_KEY>" -H "Content-type: application/json"  -d '{"name":"messages_changes","custom_publish":false,"type":"default"}'
```

Set your Syncano account key and instance name in `app/gradle.properties` file in applications source code.
```
syncano_api_key=""
syncano_instance=""
```

Offline feature
---------------
Code related to offline feature is in [MessagesIO.java](https://github.com/stasheq/syncano-offline-example/blob/master/app/src/main/java/com/syncano/offlinesample/MessagesIO.java) file.
Check code and comments inside of this file to get more information.
