# emailattachment-fetcher

Checks IMAP(S) folder (`INBOX`) for new mails, extracts attachments and saves them to a specified
folder in the file system.
Saves the mail as `eml` to `out` or `errors` dir and deletes message from server.

Currently used to check shared mailboxes at Exchange Online / O365 Office Mail 
(for invoices).

Based on [QUARKUS](https://quarkus.io/) with extensions `picocli` and `camel-mail`.

# Quick-Start

## Config
Copy `config/application.sample.properties` to `config/application.properties`, set your
IMAP-connection details. You can compare to `src/main/resources/application.properties`.

## Startup
package and run packaged jar
```
# ./mvnw package
# java -jar ./target/emailattachment-fetcher-1.0.0-SNAPSHOT-runner.jar --help
```
or in dev mode
```shell script
./mvnw compile quarkus:dev -Dquarkus.args=='help'
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

### samples
Start and check INBOX once and exit.
```
# java -jar ./target/emailattachment-fetcher-1.0.0-SNAPSHOT-runner.jar
```

Start and check INBOX every 60 seconds - repeated until exit via Strg-C.
```
# java -jar ./target/emailattachment-fetcher-1.0.0-SNAPSHOT-runner.jar -r 60
```



# Dev Info
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
It produces the `emailattachment-fetcher-1.0.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar target/emailattachment-fetcher-1.0.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```


## TODOs
* make IMAP connection parameters configurable via cmd parameter
* make some IMAP specific configurations configurable (like enabling plain auth)
* safety check allowed extensions of attachments (not needed for us - because using filter)
* test native image build (target system is W64, I don´t have VC++)
* test docker build (not usable on target system)

# License
licenced under **[GPL v3]**


[gpl v3]: http://www.gnu.org/licenses/gpl.html
