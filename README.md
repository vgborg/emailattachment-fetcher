# emailattachment-fetcher


## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `code-with-quarkus-1.0.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar target/code-with-quarkus-1.0.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

# Commandline
```shell script
./mvnw compile quarkus:dev -Dquarkus.args=='help'
```
or run packaged jar
```
❯ java -jar .\target\emailattachment-fetcher-1.0.0-SNAPSHOT-runner.jar --help
```

## Usage
```
__  ____  __  _____   ___  __ ____  ______
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/
[...]
Usage: <main class> [-hV] [-a=<dir>] [-e=<dir>] [-o=<dir>] [-s=<seconds>]
  -a, --attachments=<dir>   Where to store attachments
  -e, --errors=<dir>        Where to store errornous mails
  -h, --help                Show this help message and exit.
  -o, --out=<dir>           Where to store processed emails
  -s, --seconds=<seconds>   Repeat every x seconds
  -V, --version             Print version information and exit.
[...]
```
