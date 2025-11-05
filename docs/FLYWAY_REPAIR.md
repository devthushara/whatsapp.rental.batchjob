Flyway checksum repair guidance

If you see a Flyway Validate error about a checksum mismatch (because a migration file was edited after being applied), you have two main options:

1) Restore the original migration file content so the checksum matches what's recorded in the database (recommended when you didn't intend to change applied migrations).

2) Use Flyway repair to update the checksum in the database to match the local file (use with caution in production).

Maven command to run repair (uses application.yml for datasource properties):

```bash
mvn -Dflyway.configFiles=src/main/resources/application.yml flyway:repair
```

Flyway CLI repair example:

```bash
flyway -url=jdbc:postgresql://host:5432/db -user=user -password=pass repair
```

Important: Repair modifies the Flyway schema history table. Only run in production with explicit approvals and backups.

