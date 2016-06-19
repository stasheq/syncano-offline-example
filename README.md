Syncano Android library offline feature
=======================================

This example demonstrates how to use Syncano library to store data in a device local storage.
It is a simple chat app. Every user see the same. It's possible to see received messages when internet connection drops and application was turned off. It is also possible to write messages and send them when connection will be back.

Prepare your Syncano instance name, further called INSTANCE and Syncano account API key, further called ACCOUNT_KEY.

Starting the app
----------------
Create a "messages" class on your Syncano instance, with one item in schema: type - text, name - text.
```bash
curl -X "POST" "https://api.syncano.io/v1.1/instances/<INSTANCE>/classes/" -H "X-API-KEY: <ACCOUNT_KEY>" -H "Content-Type: application/json" -d '{"name":"messages","schema":[{"type":"text","name":"text"}]}'
```

Set your Syncano account key and instance name in `app/gradle.properties` file in apps source code.

Offline feature
---------------
Code related to offline is in `MessagesIO.java`
Check code and comments inside of this file to get more information.