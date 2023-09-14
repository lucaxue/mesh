# Mesh

Ensure you have maven (`mvn`) installed.
Run the command with:

```sh
mvn clean compile exec:java -Dexec.args="{initial-x} {initial-y} {bounded-x} {bounded-y} {IP_ADDRESS_LEFT:PORT} {IP_ADDRESS_RIGHT:PORT} {IP_ADDRESS_UP:PORT} {IP_ADDRESS_UP:PORT}"
```

e.g.

```sh
mvn clean compile exec:java -Dexec.args="0 0 1 0 252.252.252.252:9001 253.253.253.253:9002 254.254.254.254:9003 255.255.255.255:9004
```
