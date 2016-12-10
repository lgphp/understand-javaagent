# Understand [JavaAgent][ja]

This is a code journey that let you know how does the java agent work,
and what can it do.

## Warm up

### Build packages

```
mvn clean package
```

### Run pre-main agent

```
java -javaagent:agent/target/agent.jar -jar demo/target/demo.jar
```

### Run attaching agent

```
java -jar agent/target/agent.jar <PID>
```

## Rock & Roll

Just `git checkout` one of branches, enjoy yourself!

[ja]:https://docs.oracle.com/javase/8/docs/api/java/lang/instrument/package-summary.html