rm -rf com
javac -d . src/com/escalonador/**/*.java
java com.escalonador.core.Escalonador $1
