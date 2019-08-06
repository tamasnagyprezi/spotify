# Credentials.scala
You need to fill in appId and appSecret in Credentials.scala as per the following.

Register Your App:
https://developer.spotify.com/documentation/general/guides/app-settings/#register-your-app

* On your Dashboard click CREATE A CLIENT ID.
  https://developer.spotify.com/dashboard/
* Enter Application Name and Application Description and then click CREATE.
  Your application is registered, and the app view opens.
* On the app view, click Edit Settings to view and update your app settings.

Note: Find your Client ID and Client Secret; you need them in the
 authentication phase.

Client ID is the unique identifier of your application.
Client Secret is the key that you pass in secure calls to the Spotify Accounts
 and Web API services. Always store the client secret key securely; never
 reveal it publicly! If you suspect that the secret key has been compromised,
 regenerate it immediately by clicking the link on the edit settings view.


# Usage

    git submodule init
    git submodule update

# Supported requests:

https://github.com/hntd187/spotify/tree/ede67dc08defda7ea554b98a026b7cd59d95d873/core/shared/src/main/scala/io/scarman/spotify/request
