# Lemerlei - An introductory Clojure microservice

> Hippopotamus Lemerlei is a species of extinct dwarf hippos,
  because, you know, it's a microservice on the JVM.

Lemerlei is a "microservice" written in Clojure, to showcase some of
the tools available to help with microservice development. It aims to
be thoroughly commented and simple to understand, in the hopes it
might be useful even for Clojure beginners.

Its purpose is to download commit lists from github and feed them into [code-maat](https://github.com/adamtornhill/code-maat) for analysis.

## Usage

Lemerlei requires Leiningen and code-maat to run, so you first need to
install Leiningen from [leiningen.org](https://leiningen.org) and then:

```
git clone https://github.com/adamtornhill/code-maat.git
cd code-maat
lein install
```

Then set up your git account:

```
cp lein-env-example .lein-env
vim .lein-env
```

### To run the application locally

`lein ring server`

### Run the tests

`lein test`

### Packaging and running as standalone jar

```
lein do clean, ring uberjar
java -jar target/server.jar
```

### Packaging as war

`lein ring uberwar`

## License

Copyright © 2016 Rasmus Buchmann
Distributed under the Eclipse Public License, the same as Clojure.
