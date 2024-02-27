# Payment integration

Simple solution to integrate a pyament provider

### DEV

- add the provider credentials in the application properties file
- run application locally using an IDE: Vscode / IntelliJ

### Todo

- Logs are currently writtten to a file. It's difficult to read and search. Stream logs to a logging/APM platform: graylog/elasticsearch/datadog
- Requests are handled asychronously to avoid blocking. In cases of merchant/provider service failuires, we retry three times and abort. Explore persiting failed request into DB and retrying via a background service that runs periodically.
- Implement unified/centralized logging with pointcuts using Springboot AOP
- Add some more tests :)
